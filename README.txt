==========================================
  ELECTION RESULT AGGREGATOR
==========================================

OVERVIEW
--------
A Java-based CLI application that aggregates booth-level election results to constituencies,
computes turnout percentages, vote margins, determines winners, and detects data inconsistencies.

FEATURES
--------
✓ Booth-level to constituency-level aggregation
✓ Turnout calculation (votes/registered voters)
✓ Winner determination with margin calculation
✓ Duplicate detection (booth-party combinations)
✓ Data validation (negative values, overvoting)
✓ CSV import/export functionality
✓ Audit logging with session tracking
✓ Interactive CLI interface

ARCHITECTURE & DESIGN PATTERNS
-------------------------------

1. OOP STRUCTURE
   - BoothResult: Entity for booth-level data
   - ConstituencySummary: Entity for aggregated constituency data (uses Builder pattern)
   - AggregationJob: Abstract base class (Template Method pattern)
   - StandardAggregationJob: Concrete implementation

2. DESIGN PATTERNS
   a) Builder Pattern
      - ConstituencySummary.Builder: Clean construction of complex summary objects
   
   b) Chain of Responsibility
      - Rule interface and AbstractRule base class
      - DuplicateBoothRule: Detects duplicate booth-party entries
      - NegativeValueRule: Validates non-negative values
      - OvervotingRule: Detects votes > registered voters
   
   c) Strategy Pattern
      - TieBreakPolicy interface
      - AlphabeticalTieBreak: Handles ties by alphabetical order
   
   d) Template Method
      - AggregationJob: Defines workflow (validate → aggregate → computeMetrics)

3. EXCEPTION HANDLING
   - DuplicateBoothEntryException: Thrown for duplicate booth-party combinations
   - MalformedVoteRowException: Thrown for invalid CSV rows

4. COLLECTIONS & STREAMS
   - Map<Constituency, Map<Party, Integer>>: Nested maps for vote aggregation
   - Java Streams: For grouping, sorting, and ranking
   - Set: For duplicate detection

FILES & COMPONENTS
------------------
Core Entities:
  - BoothResult.java
  - ConstituencySummary.java

Validation Framework:
  - Rule.java (interface)
  - AbstractRule.java
  - DuplicateBoothRule.java
  - NegativeValueRule.java
  - OvervotingRule.java

Aggregation Logic:
  - AggregationJob.java (abstract)
  - StandardAggregationJob.java
  - TieBreakPolicy.java (interface)
  - AlphabeticalTieBreak.java

File I/O:
  - VoteFileReader.java
  - ReportWriter.java

CLI:
  - ElectionCLI.java

Exceptions:
  - DuplicateBoothEntryException.java
  - MalformedVoteRowException.java

USAGE
-----

1. COMPILE
   javac *.java

2. RUN
   java ElectionCLI

3. COMMANDS
   load <filename>   Load votes from CSV file
   aggregate         Aggregate booth results to constituencies
   winners           Display winners for all constituencies
   export            Export results to files
   help              Show available commands
   exit              Exit the application

4. INPUT FORMAT (votes.csv)
   booth,constituency,party,votes,registered
   B001,North District,Party A,1250,5000
   B001,North District,Party B,980,5000
   ...

5. OUTPUT FILES
   - constituency_summary.csv: Detailed constituency-level results
   - winners.txt: Winners report with margins and turnout
   - audit.txt: Validation and processing log (appends sessions)

EXAMPLE SESSION
---------------
> load votes.csv
✓ Successfully loaded 33 booth results

> aggregate
✓ Aggregation complete!
  Constituencies processed: 5

Validation Results:
  ✓ Duplicate check passed: 33 unique booth-party combinations
  ✓ No negative values detected
  ✓ No overvoting detected

> winners
==========================================
    ELECTION WINNERS
==========================================

Central District:
  Winner: Party A (4,300 votes, margin: 300)
  Turnout: 169.29%
...

> export
✓ Constituency summary written to: constituency_summary.csv
✓ Winners report written to: winners.txt
✓ Audit log appended to: audit.txt

> exit
Goodbye!

QUANTITATIVE COMPUTATIONS
--------------------------
1. Turnout = (Σ votes / Σ registered) × 100
2. Margin = Top1 votes - Top2 votes
3. Winner = Party with maximum votes (ties broken alphabetically)
4. Aggregation uses iterative grouping and Map.merge() for vote summation

VALIDATION RULES
----------------
1. No duplicate (booth, party) combinations
2. No negative votes or registered voters
3. Detect overvoting (total votes > registered voters per booth)
4. Validate CSV format (5 columns: booth,constituency,party,votes,registered)

EXTENSIBILITY
-------------
- Add new validation rules by extending AbstractRule
- Implement custom TieBreakPolicy for different tie-breaking strategies
- Extend AggregationJob for alternative aggregation algorithms
- Chain multiple validation rules using setNext()

TECHNICAL DETAILS
-----------------
- Language: Java
- No external dependencies (pure Java SE)
- Uses Java Streams API for functional-style operations
- File I/O with try-with-resources for safe resource management
- Scanner-based CLI for interactive command processing

==========================================
Author: Election Result Aggregator System
Version: 1.0
==========================================
