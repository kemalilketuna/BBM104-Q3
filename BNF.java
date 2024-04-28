import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.StringJoiner;

class BNFExecutor {
    private Map<String, Collection<String>> rules;  // Use Map and Collection interfaces

    public BNFExecutor() {
        rules = new HashMap<>();
    }

    public void addRule(String nonTerminal, Collection<String> productions) {
        rules.put(nonTerminal, productions);
    }

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

public class BNF {
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

    private static void writeToFile(String filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }

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
