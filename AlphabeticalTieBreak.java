/**
 * Alphabetical tie-break policy - chooses party that comes first alphabetically.
 */
public class AlphabeticalTieBreak implements TieBreakPolicy {
    
    @Override
    public String breakTie(String party1, String party2, int votes) {
        return party1.compareTo(party2) <= 0 ? party1 : party2;
    }
    
    @Override
    public String toString() {
        return "Alphabetical";
    }
}
