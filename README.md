# Context-Free Grammar Implementation

A Java-based implementation for normalizing context-free grammars to **Chomsky Normal Form (CNF)** and **Greibach Normal Form (GNF)**. This tool helps automate the transformation process of formal grammars used in compiler design, formal language theory, and computational linguistics.

## Features

- **Grammar Parsing**: Load context-free grammars from text files with a simple format
- **Chomsky Normal Form (CNF)**: Transform grammars to CNF with the following steps:
  - Left recursion removal
  - Lambda (ε) rule elimination
  - Unitary rule elimination
  - Useless variable removal
  - Terminal conversion to variables
  - Long production breaking
- **Greibach Normal Form (GNF)**: Transform grammars to GNF with sequential variable renaming and back-substitution
- **Step-by-step Logging**: Detailed output showing each transformation step
- **Interactive CLI**: User-friendly command-line interface for choosing normalization type

## Prerequisites

- **Java**: JDK 22 or higher
- **Maven**: 3.6 or higher (for building the project)

## Installation

1. Clone the repository:
```bash
git clone https://github.com/giovvbat/context-free-grammar-implementation.git
cd context-free-grammar-implementation
```

2. Build the project using Maven:
```bash
mvn clean compile
```

## Usage

### Running the Application

Run the application using Maven:
```bash
mvn exec:java -Dexec.mainClass="Main"
```

### Input Format

Create a text file with the following format:

```
VARIABLES: { A B S C }
ALPHABET: { a b c }
START: S
PRODUCTION RULES:
S -> aBC | A
A -> aB | &
B -> C | b
C -> cC | c
```

**Format Rules:**
- **VARIABLES**: Space-separated list of non-terminal symbols
- **ALPHABET**: Space-separated list of terminal symbols
- **START**: The starting variable/symbol
- **PRODUCTION RULES**: One rule per line or multiple alternatives separated by `|`
- Use `&` to represent epsilon (empty string/lambda)

### Example Workflow

1. **Start the program**:
```bash
mvn exec:java -Dexec.mainClass="Main"
```

2. **Enter the grammar file path** when prompted:
```
Enter grammar file path (e.g., assets/indirect.txt): assets/input.txt
```

3. **Choose normalization type**:
```
Enter 'c' for Chomsky (CNF) or 'g' for Greibach (GNF): c
```

4. **View the results**: 
   - The normalized grammar will be displayed in the console
   - A detailed log file will be created in the assets directory showing each transformation step

### Output Files

The program generates detailed log files:
- For CNF: `assets/normalization_<filename>_output_chomsky.txt`
- For GNF: `assets/normalization_<filename>_output_greibach.txt`

These files contain:
- Initial grammar
- Step-by-step transformations
- Final normalized grammar

## Example Grammars

The `assets/` directory contains several example grammar files:
- `input.txt` - Basic grammar example
- `chomsky.txt` - Grammar example for CNF conversion
- `complete.txt` - Complete grammar example with multiple production rules
- `reduced.txt` - Simplified grammar example

## Project Structure

```
context-free-grammar-implementation/
├── src/
│   └── main/
│       └── java/
│           ├── Main.java                    # Entry point
│           ├── model/
│           │   ├── Grammar.java             # Grammar representation
│           │   ├── Variable.java            # Non-terminal symbols
│           │   ├── AlphabetSymbol.java      # Terminal symbols
│           │   ├── GrammarSymbol.java       # Base symbol interface
│           │   └── Rules.java               # Production rules
│           └── service/
│               ├── GrammarService.java      # Core grammar operations
│               ├── NormalizationService.java # Normalization orchestration
│               └── GrammarLoaderService.java # File parsing
├── assets/                                   # Example grammar files
├── pom.xml                                   # Maven configuration
└── README.md                                 # This file
```

## How It Works

### Chomsky Normal Form (CNF)

The program converts grammars to CNF through these steps:

1. **Remove Left Recursion**: Eliminates direct and indirect left-recursive productions
2. **Remove Lambda Rules**: Eliminates ε-productions while preserving language
3. **Remove Unitary Rules**: Eliminates A → B type productions
4. **Remove Useless Variables**: Removes unreachable and non-generative variables
5. **Convert Terminals**: Replaces terminals in mixed productions with new variables
6. **Break Long Productions**: Splits productions with more than 2 symbols

Final CNF form: All productions are either A → BC or A → a

### Greibach Normal Form (GNF)

The program converts grammars to GNF through these steps:

1. **Sequential Renaming**: Renames variables in sequential order (V0, V1, V2, ...)
2. **Remove Left Recursion**: Uses Paull's algorithm to eliminate left recursion
3. **Back Substitution**: Ensures all productions start with terminals

Final GNF form: All productions are of form A → aα where a is terminal and α is a string of variables

## Dependencies

- **Lombok** 1.18.32: Used for reducing boilerplate code with annotations

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## License

This project is open source and available for educational purposes.

## Author

Created by [@giovvbat](https://github.com/giovvbat)

## Acknowledgments

This implementation follows standard algorithms for context-free grammar normalization as described in formal language theory textbooks.
