import java.util.*;

/**
 * Abstract base class for validation rules.
 * Implements Chain of Responsibility pattern.
 */
public abstract class AbstractRule implements Rule {
    private Rule next;

    @Override
    public void setNext(Rule next) {
        this.next = next;
    }

    @Override
    public Rule getNext() {
        return next;
    }

    @Override
    public List<String> validate(List<BoothResult> results) throws DuplicateBoothEntryException, MalformedVoteRowException {
        List<String> messages = new ArrayList<>();
        
        // Execute this rule's validation
        messages.addAll(doValidate(results));
        
        // Execute next rule in chain if exists
        if (next != null) {
            messages.addAll(next.validate(results));
        }
        
        return messages;
    }

    /**
     * Template method for specific validation logic.
     * @param results List of booth results to validate
     * @return List of validation messages
     * @throws DuplicateBoothEntryException if duplicates are detected
     * @throws MalformedVoteRowException if data is malformed
     */
    protected abstract List<String> doValidate(List<BoothResult> results) 
            throws DuplicateBoothEntryException, MalformedVoteRowException;
}
