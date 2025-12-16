import java.util.*;
import java.util.stream.*;

/**
 * Standard implementation of AggregationJob.
 * Aggregates booth-level results to constituencies with full validation and metrics.
 */
public class StandardAggregationJob extends AggregationJob {
    private final TieBreakPolicy tieBreakPolicy;
    private final Rule validationChain;

    public StandardAggregationJob(TieBreakPolicy tieBreakPolicy) {
        super();
        this.tieBreakPolicy = tieBreakPolicy;
        
        // Build validation chain using Chain of Responsibility
        Rule duplicateRule = new DuplicateBoothRule();
        Rule negativeRule = new NegativeValueRule();
        Rule overvotingRule = new OvervotingRule();
        
        duplicateRule.setNext(negativeRule);
        negativeRule.setNext(overvotingRule);
        
        this.validationChain = duplicateRule;
    }

    @Override
    protected void validate() throws DuplicateBoothEntryException, MalformedVoteRowException {
        auditLog.add("=== Validation Phase ===");
        List<String> validationMessages = validationChain.validate(boothResults);
        auditLog.addAll(validationMessages);
    }

    @Override
    protected void aggregate() {
        auditLog.add("=== Aggregation Phase ===");
        
        // Group booth results by constituency using streams
        Map<String, List<BoothResult>> constituencyGroups = boothResults.stream()
                .collect(Collectors.groupingBy(BoothResult::getConstituency));
        
        auditLog.add(String.format("Processing %d constituencies", constituencyGroups.size()));
        
        // For each constituency, aggregate votes
        for (Map.Entry<String, List<BoothResult>> entry : constituencyGroups.entrySet()) {
            String constituency = entry.getKey();
            List<BoothResult> results = entry.getValue();
            
            // Sum votes per party using Map
            Map<String, Integer> partyVotes = new HashMap<>();
            for (BoothResult result : results) {
                partyVotes.merge(result.getParty(), result.getVotes(), Integer::sum);
            }
            
            // Calculate total votes and registered voters
            int totalVotes = partyVotes.values().stream().mapToInt(Integer::intValue).sum();
            int totalRegistered = results.stream()
                    .mapToInt(BoothResult::getRegisteredVoters)
                    .max()
                    .orElse(0);
            
            // Create summary using Builder pattern
            ConstituencySummary.Builder builder = new ConstituencySummary.Builder()
                    .constituency(constituency)
                    .partyVotes(partyVotes)
                    .totalVotes(totalVotes)
                    .totalRegistered(totalRegistered);
            
            summaries.put(constituency, builder.build());
        }
    }

    @Override
    protected void computeMetrics() {
        auditLog.add("=== Metrics Computation Phase ===");
        
        Map<String, ConstituencySummary> updatedSummaries = new LinkedHashMap<>();
        
        for (Map.Entry<String, ConstituencySummary> entry : summaries.entrySet()) {
            String constituency = entry.getKey();
            ConstituencySummary summary = entry.getValue();
            
            // Calculate turnout percentage
            double turnout = summary.getTotalRegistered() > 0 
                    ? (summary.getTotalVotes() * 100.0) / summary.getTotalRegistered()
                    : 0.0;
            
            // Find top 2 parties using streams
            List<Map.Entry<String, Integer>> sortedParties = summary.getPartyVotes().entrySet().stream()
                    .sorted((e1, e2) -> {
                        int cmp = Integer.compare(e2.getValue(), e1.getValue());
                        if (cmp == 0) {
                            // Use tie-break policy
                            String winner = tieBreakPolicy.breakTie(e1.getKey(), e2.getKey(), e1.getValue());
                            return winner.equals(e1.getKey()) ? -1 : 1;
                        }
                        return cmp;
                    })
                    .collect(Collectors.toList());
            
            String winner = sortedParties.size() > 0 ? sortedParties.get(0).getKey() : "NONE";
            int winnerVotes = sortedParties.size() > 0 ? sortedParties.get(0).getValue() : 0;
            String runnerUp = sortedParties.size() > 1 ? sortedParties.get(1).getKey() : "NONE";
            int runnerUpVotes = sortedParties.size() > 1 ? sortedParties.get(1).getValue() : 0;
            int margin = winnerVotes - runnerUpVotes;
            
            // Rebuild summary with metrics using Builder
            ConstituencySummary updatedSummary = new ConstituencySummary.Builder()
                    .constituency(constituency)
                    .partyVotes(summary.getPartyVotes())
                    .totalVotes(summary.getTotalVotes())
                    .totalRegistered(summary.getTotalRegistered())
                    .turnoutPercentage(turnout)
                    .winner(winner)
                    .winnerVotes(winnerVotes)
                    .runnerUp(runnerUp)
                    .runnerUpVotes(runnerUpVotes)
                    .margin(margin)
                    .build();
            
            updatedSummaries.put(constituency, updatedSummary);
        }
        
        summaries = updatedSummaries;
        auditLog.add(String.format("Computed metrics for %d constituencies", summaries.size()));
    }
}
