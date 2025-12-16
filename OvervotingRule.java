import java.util.*;

/**
 * Validation rule to detect inconsistencies (votes > registered voters).
 */
public class OvervotingRule extends AbstractRule {
    
    @Override
    protected List<String> doValidate(List<BoothResult> results) throws MalformedVoteRowException {
        List<String> messages = new ArrayList<>();
        
        // Group by booth to check total votes vs registered
        Map<String, List<BoothResult>> boothGroups = new HashMap<>();
        for (BoothResult result : results) {
            boothGroups.computeIfAbsent(result.getBoothId(), k -> new ArrayList<>()).add(result);
        }
        
        int inconsistentBooths = 0;
        for (Map.Entry<String, List<BoothResult>> entry : boothGroups.entrySet()) {
            String booth = entry.getKey();
            List<BoothResult> boothResults = entry.getValue();
            
            int totalVotes = boothResults.stream().mapToInt(BoothResult::getVotes).sum();
            int registered = boothResults.get(0).getRegisteredVoters(); // Same for all parties in booth
            
            if (totalVotes > registered) {
                inconsistentBooths++;
                messages.add(String.format("⚠ Overvoting detected at booth '%s': %d votes cast, %d registered (%.1f%% turnout)",
                        booth, totalVotes, registered, (totalVotes * 100.0 / registered)));
            }
        }
        
        if (inconsistentBooths == 0) {
            messages.add("✓ No overvoting detected");
        } else {
            messages.add(String.format("⚠ Total booths with overvoting: %d", inconsistentBooths));
        }
        
        return messages;
    }
}
