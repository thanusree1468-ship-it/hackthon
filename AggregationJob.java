import java.util.*;

/**
 * Abstract base class for aggregation jobs.
 * Template Method pattern for aggregation workflow.
 */
public abstract class AggregationJob {
    protected List<BoothResult> boothResults;
    protected Map<String, ConstituencySummary> summaries;
    protected List<String> auditLog;

    public AggregationJob() {
        this.boothResults = new ArrayList<>();
        this.summaries = new LinkedHashMap<>();
        this.auditLog = new ArrayList<>();
    }

    /**
     * Template method defining the aggregation workflow.
     */
    public final void execute() throws DuplicateBoothEntryException, MalformedVoteRowException {
        validate();
        aggregate();
        computeMetrics();
        auditLog.add(String.format("Aggregation completed: %d constituencies processed", summaries.size()));
    }

    /**
     * Validate booth results using validation rules.
     */
    protected abstract void validate() throws DuplicateBoothEntryException, MalformedVoteRowException;

    /**
     * Aggregate booth-level results to constituency level.
     */
    protected abstract void aggregate();

    /**
     * Compute metrics like turnout, margins, winners.
     */
    protected abstract void computeMetrics();

    /**
     * Set booth results to process.
     */
    public void setBoothResults(List<BoothResult> boothResults) {
        this.boothResults = boothResults;
    }

    /**
     * Get constituency summaries.
     */
    public Map<String, ConstituencySummary> getSummaries() {
        return Collections.unmodifiableMap(summaries);
    }

    /**
     * Get audit log messages.
     */
    public List<String> getAuditLog() {
        return Collections.unmodifiableList(auditLog);
    }
}
