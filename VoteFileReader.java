import java.io.*;
import java.util.*;

/**
 * Handles reading votes from CSV files.
 */
public class VoteFileReader {
    
    /**
     * Reads booth results from a CSV file.
     * Expected format: booth,constituency,party,votes,registered
     * @param filePath Path to the CSV file
     * @return List of BoothResult objects
     * @throws IOException If file cannot be read
     * @throws MalformedVoteRowException If CSV rows are malformed
     */
    public List<BoothResult> readVotes(String filePath) throws IOException, MalformedVoteRowException {
        List<BoothResult> results = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            boolean isHeader = true;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                // Skip header line
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    BoothResult result = parseRow(line, lineNumber);
                    results.add(result);
                } catch (Exception e) {
                    throw new MalformedVoteRowException(lineNumber, line, e);
                }
            }
        }
        
        return results;
    }
    
    /**
     * Parses a single CSV row into a BoothResult object.
     */
    private BoothResult parseRow(String line, int lineNumber) throws MalformedVoteRowException {
        String[] parts = line.split(",");
        
        if (parts.length != 5) {
            throw new MalformedVoteRowException(lineNumber, line, 
                    String.format("Expected 5 columns, found %d", parts.length));
        }
        
        try {
            String booth = parts[0].trim();
            String constituency = parts[1].trim();
            String party = parts[2].trim();
            int votes = Integer.parseInt(parts[3].trim());
            int registered = Integer.parseInt(parts[4].trim());
            
            return new BoothResult(booth, constituency, party, votes, registered);
        } catch (NumberFormatException e) {
            throw new MalformedVoteRowException(lineNumber, line, "Invalid number format");
        }
    }
}
