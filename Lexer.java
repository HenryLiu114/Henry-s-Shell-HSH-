import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Lexer {

    public enum TokenType {
        CommandStarter,
        BinaryOperator,
        OpenParth, ClosedParth,
        StringQuotes, StringOPs,
        Var, location, free, usevar, system,
        Identifier,
        Integer,
        Comparision,
        IfElse, EndOfCond,
        ProgNLB, ProgNRB, ProgNSplitter;
    }

    public class Token {
        protected String value;
        protected TokenType type;

        public Token(String value, TokenType type) {
            this.value = value;
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public TokenType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type + " : " + value;
        }
    }

    public Token tokens(String val, TokenType t) {
        return new Token(val, t);
    }

    private static LinkedList<String> customSplit(String s) {
        LinkedList<String> res = new LinkedList<>();
        String curString = "";
        boolean isStr = false;
        int curargs = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c == '.' || c == '/' || c == ',') && isStr) {
                curString += c;
            } else if ((c == '.' || c == '/' || c == ',') && !isStr) {
                if (!curString.isEmpty())
                    res.add(curString);
                curString = "";
                res.add(c + "");
            } else if (c == '(' && !isStr) {
                curString += "(";
                curargs++;
            } else if (c == ')' && !isStr) {
                curString += ")";
                curargs--;
            } else if (c == '\"') {
                isStr = !isStr;
            } else if (c == ' ' && !isStr && curargs == 0) {
                if (!curString.isEmpty())
                    res.add(curString);
                curString = "";
            } 
            else {
                curString += c;
            }
        }
        if (!curString.isEmpty())
            res.add(curString);
        return res;
    }

    public LinkedList<Token> tokenize(String src) {
        LinkedList<Token> res = new LinkedList<>();
        LinkedList<String> spl = customSplit(src);

        // Build Tokens
        int i = 0;
        while (i < spl.size()) {
            switch (spl.get(i)) {
                case "/":
                    res.add(tokens(spl.get(i), TokenType.CommandStarter));
                    break;
                case "(":
                    res.add(tokens(spl.get(i), TokenType.OpenParth));
                    break;
                case ")":
                    res.add(tokens(spl.get(i), TokenType.ClosedParth));
                    break;
                case "\"":
                    res.add(tokens(spl.get(i), TokenType.StringQuotes));
                    break;
                case "add":
                case "sub":
                case "mul":
                case "div":
                    res.add(tokens(spl.get(i), TokenType.BinaryOperator));
                    break;
                case "var":
                    res.add(tokens(spl.get(i), TokenType.Var));
                    break;
                case "varat":
                    res.add(tokens(spl.get(i), TokenType.location));
                    break;
                case "free":
                    res.add(tokens(spl.get(i), TokenType.free));
                    break;
                case "usevar":
                    res.add(tokens(spl.get(i), TokenType.usevar));
                    break;
                case "print":
                    res.add(tokens(spl.get(i), TokenType.system));
                    break;
                case "concat":
                    res.add(tokens(spl.get(i), TokenType.StringOPs));
                    break;
                case "eq":
                case "lteq":
                case "lt":
                case "gteq":
                case "gt":
                    res.add(tokens(spl.get(i), TokenType.Comparision));
                    break;
                case "if":
                    res.add(tokens(spl.get(i), TokenType.IfElse));
                    break;
                case "endIf":
                    res.add(tokens(spl.get(i), TokenType.EndOfCond));
                    break;
                case "{":
                    res.add(tokens(spl.get(i), TokenType.ProgNLB));
                    break;
                case "}":
                    res.add(tokens(spl.get(i), TokenType.ProgNRB));
                    break;
                case ",":
                    res.add(tokens(spl.get(i), TokenType.ProgNSplitter));
                    break;
                default:
                    try {
                        Integer.parseInt(spl.get(i));
                        res.add(tokens(spl.get(i), TokenType.Integer));
                    } catch (Exception e) {
                        res.add(tokens(spl.get(i), TokenType.Identifier));
                    }
                    break;
            }
            i++;
        }

        return res;
    }

    public TreeNode parse(LinkedList<Token> lexedList) {
        HashMap<String, Integer> commands = new HashMap<>();
        commands.put("add", 2);
        commands.put("sub", 2);
        commands.put("mul", 2);
        commands.put("div", 2);
        commands.put("var", 2);
        commands.put("allo", 2);
        commands.put("free", 1);
        commands.put("varat", 2);
        commands.put("usevar", 1);
        commands.put("print", 1);
        commands.put("concat", 2);
        commands.put("eq", 2);
        commands.put("lteq", 2);
        commands.put("lt", 2);
        commands.put("gteq", 2);
        commands.put("gt", 2);
        commands.put("if", -1);
        commands.put("{", -1);

        if (lexedList.isEmpty()) {
            return null;
        }

        Token cur = lexedList.removeFirst();

        if (cur.type == TokenType.CommandStarter) {
            if (lexedList.isEmpty())
                return null;
            cur = lexedList.removeFirst();
        }

        String token = cur.value;
        TreeNode curNode = new TreeNode(token);

        if (commands.containsKey(token)) {
            int arity = commands.get(token);
            if (arity != -1) {
                for (int i = 0; i < arity; i++) {
                    TreeNode child = parse(lexedList);
                    curNode.children.add(child);
                }
            } else {
                if (token.equals("if")) {
                    TreeNode ifNode = new TreeNode("if");

                    // condition
                    ifNode.children.add(parse(lexedList));

                    // then branch
                    ifNode.children.add(parse(lexedList));

                    // else branch
                    ifNode.children.add(parse(lexedList));

                    // endif branch
                    ifNode.children.add(parse(lexedList));
                    return ifNode;
                } else if (token.equals("{")) {
                    TreeNode progN = new TreeNode("{");
                    //IMPLEMENT PROGN
                    return progN;
                }
            }
        }

        return curNode;
    }

    public static LinkedList<String> interpreter(TreeNode tree) {

        Stack<TreeNode> postStack = new Stack<>();
        Stack<String> popOrder = new Stack<>();
        LinkedList<String> res = new LinkedList<>();

        // Uses DFS
        postStack.push(tree);
        while (!postStack.isEmpty()) {
            TreeNode cur = postStack.pop();
            popOrder.push(cur.data);
            if (!cur.children.isEmpty()) {
                for (int i = cur.children.size() - 1; i >= 0; i--) {
                    postStack.push(cur.children.get(i));
                }
            }
        }

        while (!popOrder.isEmpty()) {
            res.add(popOrder.pop());
        }

        return res;
    }
}