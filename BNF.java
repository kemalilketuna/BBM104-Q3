import java.io.*;
import java.util.*;

class BNFExecutor {
    private HashMap<String, List<String>> rules;

    public BNFExecutor() {
        rules = new HashMap<>();
    }

    public void addRule(String nonTerminal, List<String> productions) {
        rules.put(nonTerminal, productions);
    }

    public String expand(String nonTerminal) {
        if (!rules.containsKey(nonTerminal)) {
            return nonTerminal;
        }

        List<String> expressions = rules.get(nonTerminal);
        StringJoiner joiner = new StringJoiner("|", "(", ")");
        for (String expr : expressions) {
            joiner.add(String.join("", Arrays.stream(expr.split("")).map(this::expand).toArray(String[]::new)));
        }
        return joiner.toString();
    }
}

public class BNF {
    private static void loadRulesFromFile(BNFExecutor executor, String filePath) throws IOException{
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("->");
            String nonTerminal = parts[0].trim();
            String[] productions = parts[1].split("\\|");
            
            executor.addRule(nonTerminal, Arrays.asList(productions));
        }
        reader.close();
    }

    private static void writeToFile(String filePath, String content) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(content);
        writer.close();
    }

    public static void main(String[] args) {
        BNFExecutor executor = new BNFExecutor();
        try {
            loadRulesFromFile(executor, args[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            writeToFile(args[1], executor.expand("S"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

