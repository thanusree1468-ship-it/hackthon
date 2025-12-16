/**
 * Exception thrown when duplicate booth-party entries are detected.
 */
public class DuplicateBoothEntryException extends Exception {
    private final String boothId;
    private final String party;

    public DuplicateBoothEntryException(String boothId, String party) {
        super(String.format("Duplicate entry detected: Booth '%s', Party '%s'", boothId, party));
        this.boothId = boothId;
        this.party = party;
    }

    public String getBoothId() {
        return boothId;
    }

    public String getParty() {
        return party;
    }
}
