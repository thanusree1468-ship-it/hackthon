/**
 * Entity class representing a single booth-level election result.
 */
public class BoothResult {
    private final String boothId;
    private final String constituency;
    private final String party;
    private final int votes;
    private final int registeredVoters;

    public BoothResult(String boothId, String constituency, String party, int votes, int registeredVoters) {
        this.boothId = boothId;
        this.constituency = constituency;
        this.party = party;
        this.votes = votes;
        this.registeredVoters = registeredVoters;
    }

    public String getBoothId() {
        return boothId;
    }

    public String getConstituency() {
        return constituency;
    }

    public String getParty() {
        return party;
    }

    public int getVotes() {
        return votes;
    }

    public int getRegisteredVoters() {
        return registeredVoters;
    }

    public String getBoothPartyKey() {
        return boothId + ":" + party;
    }

    @Override
    public String toString() {
        return String.format("BoothResult{booth='%s', constituency='%s', party='%s', votes=%d, registered=%d}",
                boothId, constituency, party, votes, registeredVoters);
    }
}
