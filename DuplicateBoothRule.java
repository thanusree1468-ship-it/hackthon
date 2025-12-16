import java.util.*;

/**
 * Validation rule to detect duplicate booth-party entries.
 */
public class DuplicateBoothRule extends AbstractRule {
    
    @Override
    protected List<String> doValidate(List<BoothResult> results) throws DuplicateBoothEntryException {
        List<String> messages = new ArrayList<>();
        Set<String> seenBoothParty = new HashSet<>();
        
        for (BoothResult result : results) {
            String key = result.getBoothPartyKey();
            if (seenBoothParty.contains(key)) {
                throw new DuplicateBoothEntryException(result.getBoothId(), result.getParty());
            }
            seenBoothParty.add(key);
        }
        
        messages.add(String.format("✓ Duplicate check passed: %d unique booth-party combinations", seenBoothParty.size()));
        return messages;
    }
}
