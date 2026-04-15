import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Lexer {

    public enum TokenType {
        CommandStarter,
        BinaryOperator,
        OpenParth, ClosedParth,
        StringQuotes, StringOPs, CommandQuotes,
        Var, location, free, usevar, system,
        Identifier,
        Integer,
        Comparision,
        IfElse, EndOfCond,
        ProgNLB, ProgNRB, ProgNSplitter,
        Conditional,
        FunctionDefinition, FunctionUse, FunctionEnd, FunctionReturn, FunctionParam, FunctionSProgs, FunctionEProgs, FunctionSplitter,
        consvar, newlist, uselist;
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
            if ((c == '.' || c == '/' || c == ',' || c == ';' || c == '{' || c == '}' || c == '[' || c == ']') && isStr) {
                curString += c;
            } else if ((c == '.' || c == '/' || c == ',' || c == ';' || c == '{' || c == '}' || c == '[' || c == ']') && !isStr) {
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
                curString += "\"";
            } else if (c == ' ' && !isStr && curargs == 0) {
                if (!curString.isEmpty())
                    res.add(curString);
                curString = "";
            } else {
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
                case "[":
                    res.add(tokens(spl.get(i), TokenType.FunctionSProgs));
                    break;
                case "]":
                    res.add(tokens(spl.get(i), TokenType.FunctionEProgs));
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
                case "endif":
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
                case ";":
                    res.add(tokens(spl.get(i), TokenType.FunctionSplitter));
                    break;
                case "and":
                case "or":
                case "not":
                    res.add(tokens(spl.get(i), TokenType.Conditional));
                    break;
                case "defun":
                    res.add(tokens(spl.get(i), TokenType.FunctionDefinition));
                    break;
                case "usefun":
                    res.add(tokens(spl.get(i), TokenType.FunctionUse));
                    break;
                case "return":
                    res.add(tokens(spl.get(i), TokenType.FunctionReturn));
                    break;
                case "endfun":
                    res.add(tokens(spl.get(i), TokenType.FunctionEnd));
                    break;
                case "consvar":
                    res.add(tokens(spl.get(i), TokenType.consvar));
                break;
                case "newlist":
                    res.add(tokens(spl.get(i), TokenType.newlist));
                break;
                case "uselist":
                    res.add(tokens(spl.get(i), TokenType.uselist));
                break;
                case "mod":
                    res.add(tokens(spl.get(i), TokenType.BinaryOperator));
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
        commands.put("mod", 2);
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
        commands.put("and", 2);
        commands.put("or", 2);
        commands.put("not", 1);
        commands.put("usefun", -1);
        commands.put("defun", 4);
        commands.put("return", 1);
        commands.put("[", -1);
        commands.put("consvar", 2);
        commands.put("newlist", 1);
        commands.put("uselist", 1);
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
                } else if (token.equals("usefun")) {
                    TreeNode usefunNode = new TreeNode("usefun");

                    // function name
                    TreeNode funcName = parse(lexedList);
                    // parameters (this should handle the (...) part)
                    TreeNode params = parse(lexedList);

                    if (funcName == null || params == null) {
                        throw new RuntimeException("Invalid usefun syntax");
                    }

                    params.children.add(funcName);
                    params.children.add(usefunNode);

                    return params;
                } else if (token.equals("{")) {
                    TreeNode progN = new TreeNode("{");
                    String cmd = "";
                    while (!lexedList.isEmpty() && lexedList.peek().type != TokenType.ProgNRB) {
                        Token next = lexedList.peek();
                        // Treat comma as newline → just skip it
                        if (next.type == TokenType.ProgNSplitter) {
                            lexedList.removeFirst();
                            progN.children.add(new TreeNode(cmd + "."));
                            cmd = "";
                        } else {
                            if (next.type == TokenType.CommandStarter) {
                                lexedList.removeFirst();
                                cmd += "/";
                            } else {
                                cmd += lexedList.removeFirst().value + " ";
                            }
                        }
                    }
                    progN.children.add(new TreeNode(cmd + "."));
                    progN.children.add(new TreeNode("}"));
                    if (!lexedList.isEmpty()) {
                        lexedList.removeFirst(); // actually remove the '}'
                    }

                    return progN;
                }
                else if (token.equals("[")) {
                    TreeNode progN = new TreeNode("[");
                    String cmd = "";
                    while (!lexedList.isEmpty() && lexedList.peek().type != TokenType.FunctionEProgs) {
                        Token next = lexedList.peek();
                        // Treat comma as newline → just skip it
                        if (next.type == TokenType.FunctionSplitter) {
                            lexedList.removeFirst();
                            progN.children.add(new TreeNode(cmd + "."));
                            cmd = "";
                        } else {
                            if (next.type == TokenType.CommandStarter) {
                                lexedList.removeFirst();
                                cmd += "/";
                            } else {
                                cmd += lexedList.removeFirst().value + " ";
                            }
                        }
                    }
                    progN.children.add(new TreeNode(cmd + "."));
                    progN.children.add(new TreeNode("]"));
                    if (!lexedList.isEmpty()) {
                        lexedList.removeFirst(); // actually remove the '}'
                    }

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