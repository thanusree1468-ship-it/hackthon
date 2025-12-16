import java.io.*;
import java.util.*;

/**
 * Handles writing constituency summaries and reports to files.
 */
public class ReportWriter {
    
    /**
     * Writes constituency summary to CSV file.
     * Format: constituency,party,votes,total_votes,registered,turnout,winner,margin
     */
    public void writeConstituencySummary(Map<String, ConstituencySummary> summaries, String filePath) 
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Write header
            writer.println("constituency,party,votes,total_votes,registered,turnout,winner,margin");
            
            // Write data for each constituency
            for (ConstituencySummary summary : summaries.values()) {
                for (Map.Entry<String, Integer> partyEntry : summary.getPartyVotes().entrySet()) {
                    writer.printf("%s,%s,%d,%d,%d,%.2f,%s,%d%n",
                            summary.getConstituency(),
                            partyEntry.getKey(),
                            partyEntry.getValue(),
                            summary.getTotalVotes(),
                            summary.getTotalRegistered(),
                            summary.getTurnoutPercentage(),
                            summary.getWinner(),
                            summary.getMargin());
                }
            }
        }
    }
    
    /**
     * Writes winners report to text file.
     */
    public void writeWinners(Map<String, ConstituencySummary> summaries, String filePath) 
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("==========================================");
            writer.println("    ELECTION WINNERS REPORT");
            writer.println("==========================================");
            writer.println();
            
            // Sort constituencies alphabetically
            List<String> constituencies = new ArrayList<>(summaries.keySet());
            Collections.sort(constituencies);
            
            for (String constituency : constituencies) {
                ConstituencySummary summary = summaries.get(constituency);
                writer.println(String.format("Constituency: %s", constituency));
                writer.println(String.format("  Winner: %s (%,d votes)", 
                        summary.getWinner(), summary.getWinnerVotes()));
                writer.println(String.format("  Runner-up: %s (%,d votes)", 
                        summary.getRunnerUp(), summary.getRunnerUpVotes()));
                writer.println(String.format("  Margin: %,d votes", summary.getMargin()));
                writer.println(String.format("  Turnout: %.2f%% (%,d / %,d)", 
                        summary.getTurnoutPercentage(), 
                        summary.getTotalVotes(), 
                        summary.getTotalRegistered()));
                writer.println();
            }
            
            writer.println("==========================================");
            writer.println(String.format("Total Constituencies: %d", summaries.size()));
        }
    }
    
    /**
     * Writes audit log to text file.
     */
    public void writeAuditLog(List<String> auditLog, String filePath, boolean append) 
            throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, append))) {
            if (append) {
                writer.println();
                writer.println("========================================");
            }
            writer.println(String.format("Session: %s", new Date()));
            writer.println("========================================");
            
            for (String message : auditLog) {
                writer.println(message);
            }
        }
    }
}
