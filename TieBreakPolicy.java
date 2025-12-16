/**
 * Strategy interface for handling tie-breaking scenarios.
 */
public interface TieBreakPolicy {
    /**
     * Determines the winner when multiple parties have the same votes.
     * @param party1 First party name
     * @param party2 Second party name
     * @param votes Common vote count
     * @return The party that should be declared winner
     */
    String breakTie(String party1, String party2, int votes);
}
