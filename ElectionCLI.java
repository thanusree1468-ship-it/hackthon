import java.io.*;
import java.util.*;


public class ElectionCLI {
    private List<BoothResult> boothResults;
    private Map<String, ConstituencySummary> summaries;
    private List<String> auditLog;
    private final VoteFileReader fileReader;
    private final ReportWriter reportWriter;
    private final Scanner scanner;
    private boolean running;

    public ElectionCLI() {
        this.boothResults = new ArrayList<>();
        this.summaries = new HashMap<>();
        this.auditLog = new ArrayList<>();
        this.fileReader = new VoteFileReader();
        this.reportWriter = new ReportWriter();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    /**
     * Starts the CLI loop.
     */
    public void start() {
        printWelcome();
        
        while (running) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            processCommand(input);
        }
        
        scanner.close();
    }

    /**
     * Processes a single command.
     */
    private void processCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        
        try {
            switch (command) {
                case "load":
                    if (parts.length < 2) {
                        System.out.println("Usage: load <filename>");
                    } else {
                        loadVotes(parts[1]);
                    }
                    break;
                    
                case "aggregate":
                    aggregate();
                    break;
                    
                case "winners":
                    showWinners();
                    break;
                    
                case "export":
                    export();
                    break;
                    
                case "help":
                    printHelp();
                    break;
                    
                case "exit":
                case "quit":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                    
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Loads votes from CSV file.
     */
    private void loadVotes(String filename) {
        try {
            System.out.println("Loading votes from " + filename + "...");
            boothResults = fileReader.readVotes(filename);
            System.out.println("✓ Successfully loaded " + boothResults.size() + " booth results");
            summaries.clear(); // Clear previous aggregation
        } catch (FileNotFoundException e) {
            System.err.println("✗ File not found: " + filename);
        } catch (MalformedVoteRowException e) {
            System.err.println("✗ Malformed data: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("✗ Error reading file: " + e.getMessage());
        }
    }

    /**
     * Aggregates booth results to constituencies.
     */
    private void aggregate() {
        if (boothResults.isEmpty()) {
            System.out.println("No data loaded. Use 'load <filename>' first.");
            return;
        }
        
        try {
            System.out.println("Aggregating results...");
            
            // Create aggregation job with alphabetical tie-break policy
            AggregationJob job = new StandardAggregationJob(new AlphabeticalTieBreak());
            job.setBoothResults(boothResults);
            job.execute();
            
            summaries = job.getSummaries();
            auditLog = job.getAuditLog();
            
            System.out.println("✓ Aggregation complete!");
            System.out.println("  Constituencies processed: " + summaries.size());
            
            // Show validation summary
            System.out.println("\nValidation Results:");
            for (String log : auditLog) {
                if (log.contains("✓") || log.contains("⚠")) {
                    System.out.println("  " + log);
                }
            }
            
        } catch (DuplicateBoothEntryException e) {
            System.err.println("✗ Duplicate entry detected: " + e.getMessage());
        } catch (MalformedVoteRowException e) {
            System.err.println("✗ Malformed data: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Error during aggregation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Displays winners for all constituencies.
     */
    private void showWinners() {
        if (summaries.isEmpty()) {
            System.out.println("No aggregated data. Use 'aggregate' first.");
            return;
        }
        
        System.out.println("\n==========================================");
        System.out.println("    ELECTION WINNERS");
        System.out.println("==========================================\n");
        
        // Sort constituencies alphabetically
        List<String> constituencies = new ArrayList<>(summaries.keySet());
        Collections.sort(constituencies);
        
        for (String constituency : constituencies) {
            ConstituencySummary summary = summaries.get(constituency);
            System.out.println(String.format("%s:", constituency));
            System.out.println(String.format("  Winner: %s (%,d votes, margin: %,d)", 
                    summary.getWinner(), summary.getWinnerVotes(), summary.getMargin()));
            System.out.println(String.format("  Turnout: %.2f%%\n", summary.getTurnoutPercentage()));
        }
    }

    /**
     * Exports results to files.
     */
    private void export() {
        if (summaries.isEmpty()) {
            System.out.println("No aggregated data. Use 'aggregate' first.");
            return;
        }
        
        try {
            System.out.println("Exporting results...");
            
            // Write constituency summary
            reportWriter.writeConstituencySummary(summaries, "constituency_summary.csv");
            System.out.println("✓ Constituency summary written to: constituency_summary.csv");
            
            // Write winners
            reportWriter.writeWinners(summaries, "winners.txt");
            System.out.println("✓ Winners report written to: winners.txt");
            
            // Write audit log (append mode for sessions)
            reportWriter.writeAuditLog(auditLog, "audit.txt", true);
            System.out.println("✓ Audit log appended to: audit.txt");
            
        } catch (IOException e) {
            System.err.println("✗ Error writing files: " + e.getMessage());
        }
    }

    /**
     * Prints welcome message.
     */
    private void printWelcome() {
        System.out.println("==========================================");
        System.out.println("   ELECTION RESULT AGGREGATOR");
        System.out.println("==========================================");
        System.out.println("Type 'help' for available commands");
    }

    /**
     * Prints help information.
     */
    private void printHelp() {
        System.out.println("\nAvailable commands:");
        System.out.println("  load <filename>  - Load votes from CSV file (format: booth,constituency,party,votes,registered)");
        System.out.println("  aggregate        - Aggregate booth results to constituencies");
        System.out.println("  winners          - Display winners for all constituencies");
        System.out.println("  export           - Export results to files (constituency_summary.csv, winners.txt, audit.txt)");
        System.out.println("  help             - Show this help message");
        System.out.println("  exit             - Exit the application");
    }

    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        ElectionCLI cli = new ElectionCLI();
        cli.start();
    }
}
