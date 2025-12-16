import java.util.*;

/**
 * Entity class representing aggregated results for a constituency.
 * Uses Builder pattern for construction.
 */
public class ConstituencySummary {
    private final String constituency;
    private final Map<String, Integer> partyVotes;
    private final int totalVotes;
    private final int totalRegistered;
    private final double turnoutPercentage;
    private final String winner;
    private final int winnerVotes;
    private final String runnerUp;
    private final int runnerUpVotes;
    private final int margin;

    private ConstituencySummary(Builder builder) {
        this.constituency = builder.constituency;
        this.partyVotes = new HashMap<>(builder.partyVotes);
        this.totalVotes = builder.totalVotes;
        this.totalRegistered = builder.totalRegistered;
        this.turnoutPercentage = builder.turnoutPercentage;
        this.winner = builder.winner;
        this.winnerVotes = builder.winnerVotes;
        this.runnerUp = builder.runnerUp;
        this.runnerUpVotes = builder.runnerUpVotes;
        this.margin = builder.margin;
    }

    public String getConstituency() {
        return constituency;
    }

    public Map<String, Integer> getPartyVotes() {
        return Collections.unmodifiableMap(partyVotes);
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public int getTotalRegistered() {
        return totalRegistered;
    }

    public double getTurnoutPercentage() {
        return turnoutPercentage;
    }

    public String getWinner() {
        return winner;
    }

    public int getWinnerVotes() {
        return winnerVotes;
    }

    public String getRunnerUp() {
        return runnerUp;
    }

    public int getRunnerUpVotes() {
        return runnerUpVotes;
    }

    public int getMargin() {
        return margin;
    }

    public static class Builder {
        private String constituency;
        private Map<String, Integer> partyVotes = new HashMap<>();
        private int totalVotes;
        private int totalRegistered;
        private double turnoutPercentage;
        private String winner;
        private int winnerVotes;
        private String runnerUp;
        private int runnerUpVotes;
        private int margin;

        public Builder constituency(String constituency) {
            this.constituency = constituency;
            return this;
        }

        public Builder partyVotes(Map<String, Integer> partyVotes) {
            this.partyVotes = partyVotes;
            return this;
        }

        public Builder totalVotes(int totalVotes) {
            this.totalVotes = totalVotes;
            return this;
        }

        public Builder totalRegistered(int totalRegistered) {
            this.totalRegistered = totalRegistered;
            return this;
        }

        public Builder turnoutPercentage(double turnoutPercentage) {
            this.turnoutPercentage = turnoutPercentage;
            return this;
        }

        public Builder winner(String winner) {
            this.winner = winner;
            return this;
        }

        public Builder winnerVotes(int winnerVotes) {
            this.winnerVotes = winnerVotes;
            return this;
        }

        public Builder runnerUp(String runnerUp) {
            this.runnerUp = runnerUp;
            return this;
        }

        public Builder runnerUpVotes(int runnerUpVotes) {
            this.runnerUpVotes = runnerUpVotes;
            return this;
        }

        public Builder margin(int margin) {
            this.margin = margin;
            return this;
        }

        public ConstituencySummary build() {
            return new ConstituencySummary(this);
        }
    }

    @Override
    public String toString() {
        return String.format("ConstituencySummary{constituency='%s', winner='%s', votes=%d, margin=%d, turnout=%.2f%%}",
                constituency, winner, winnerVotes, margin, turnoutPercentage);
    }
}
