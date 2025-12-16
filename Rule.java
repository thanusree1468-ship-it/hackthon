import java.util.List;

/**
 * Interface for validation rules.
 * Implements Chain of Responsibility pattern.
 */
public interface Rule {
    /**
     * Validates a list of booth results and returns audit messages.
     * @param results List of booth results to validate
     * @return List of validation messages/warnings
     * @throws DuplicateBoothEntryException if duplicates are detected
     * @throws MalformedVoteRowException if data is malformed
     */
    List<String> validate(List<BoothResult> results) throws DuplicateBoothEntryException, MalformedVoteRowException;
    
    /**
     * Sets the next rule in the chain.
     * @param next The next rule to execute
     */
    void setNext(Rule next);
    
    /**
     * Gets the next rule in the chain.
     * @return The next rule, or null if this is the last rule
     */
    Rule getNext();
}
