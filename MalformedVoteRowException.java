/**
 * Exception thrown when a CSV row cannot be parsed correctly.
 */
public class MalformedVoteRowException extends Exception {
    private final int lineNumber;
    private final String row;

    public MalformedVoteRowException(int lineNumber, String row, String message) {
        super(String.format("Line %d: %s - Row: '%s'", lineNumber, message, row));
        this.lineNumber = lineNumber;
        this.row = row;
    }

    public MalformedVoteRowException(int lineNumber, String row, Throwable cause) {
        super(String.format("Line %d: Failed to parse - Row: '%s'", lineNumber, row), cause);
        this.lineNumber = lineNumber;
        this.row = row;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getRow() {
        return row;
    }
}
