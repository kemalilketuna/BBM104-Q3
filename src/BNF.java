import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * The {@code BNFExecutor} class manages a collection of grammar rules in Backus-Naur Form (BNF)
 * and provides functionality to expand these rules into a complete expression.
 * This class stores grammar rules where each non-terminal symbol can be associated with multiple
 * productions. The expansion process recursively replaces non-terminal symbols with their
 * corresponding productions until only terminal symbols remain.
 *
 * <p>Internally, it uses a {@code Map} to store non-terminal symbols and their associated
 * productions, which are collections of strings.
 */
class BNFExecutor {
    /**
     * Stores the BNF grammar rules with non-terminal symbols as keys and their productions as values.
     */
    private Map<String, Collection<String>> rules;

    /**
     * Constructs a new BNFExecutor with an empty set of grammar rules.
     */
    public BNFExecutor() {
        rules = new HashMap<>();
    }

    /**
     * Adds a rule to the grammar. This method maps a non-terminal symbol to its productions.
     *
     * @param nonTerminal the non-terminal symbol to which the productions are to be associated
     * @param productions a collection of strings representing the productions for the non-terminal symbol
     */
    public void addRule(String nonTerminal, Collection<String> productions) {
        rules.put(nonTerminal, productions);
    }

    /**
     * Expands a non-terminal symbol into its complete expression by recursively replacing
     * each non-terminal in the productions with its expanded form until only terminal symbols remain.
     * If a non-terminal does not exist in the grammar rules, it returns the non-terminal as is.
     *
     * @param nonTerminal the non-terminal symbol to expand
     * @return a string representing the expanded form of the non-terminal symbol,
     *         or the non-terminal itself if it does not exist in the grammar rules
     */
    public String expand(String nonTerminal) {
        if (!rules.containsKey(nonTerminal)) {
            return nonTerminal;
        }

        Collection<String> expressions = rules.get(nonTerminal);
        StringJoiner joiner = new StringJoiner("|", "(", ")");
        for (String expr : expressions) {
            joiner.add(String.join("", Arrays.stream(expr.split("")).map(this::expand).toArray(String[]::new)));
        }
        return joiner.toString();
    }
}


/**
 * This class handles the loading and processing of Backus-Naur Form (BNF) grammar rules from a file,
 * and writing the results of grammar expansion to another file.
 */
public class BNF {

    /**
     * Loads BNF grammar rules from the specified file and adds them to the provided BNFExecutor.
     * Each rule in the file should be in the format "non-terminal -> production1 | production2 | ...".
     *
     * @param executor The BNFExecutor instance to which the rules will be added.
     * @param filePath The path of the file containing the BNF rules.
     * @throws IOException If an I/O error occurs reading from the file.
     */
    private static void loadRulesFromFile(BNFExecutor executor, String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("->");
                String nonTerminal = parts[0].trim();
                Collection<String> productions = Arrays.asList(parts[1].split("\\|"));
                
                executor.addRule(nonTerminal, productions);
            }
        }
    }

    /**
     * Writes the specified content to the file at the specified path.
     *
     * @param filePath The path of the file to which the content will be written.
     * @param content The content to write to the file.
     * @throws IOException If an I/O error occurs writing to the file.
     */
    private static void writeToFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }

    /**
     * Main method to execute the BNF grammar processing.
     * It takes two command line arguments: the path to the input file containing the BNF rules,
     * and the path to the output file where the expanded result will be written.
     *
     * @param args Command line arguments containing the paths of the input and output files.
     */
    public static void main(String[] args) {
        BNFExecutor executor = new BNFExecutor();
        try {
            loadRulesFromFile(executor, args[0]);
            String result = executor.expand("S");
            writeToFile(args[1], result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
