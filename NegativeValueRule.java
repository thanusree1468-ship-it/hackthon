import java.util.*;

/**
 * Validation rule to check for negative votes or registered voters.
 */
public class NegativeValueRule extends AbstractRule {
    
    @Override
    protected List<String> doValidate(List<BoothResult> results) throws MalformedVoteRowException {
        List<String> messages = new ArrayList<>();
        int invalidCount = 0;
        
        for (BoothResult result : results) {
            if (result.getVotes() < 0) {
                invalidCount++;
                messages.add(String.format("⚠ Warning: Negative votes at booth '%s' for party '%s': %d",
                        result.getBoothId(), result.getParty(), result.getVotes()));
            }
            if (result.getRegisteredVoters() < 0) {
                invalidCount++;
                messages.add(String.format("⚠ Warning: Negative registered voters at booth '%s': %d",
                        result.getBoothId(), result.getRegisteredVoters()));
            }
        }
        
        if (invalidCount == 0) {
            messages.add("✓ No negative values detected");
        }
        
        return messages;
    }
}
