import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class HLANG {
    private enum TokenType {
        // Command Types
        arithmetic,
        singleArith,
        logical,
        logicalnot,
        var,
        varname,
        vardec,
        conditional,
        ifstmt,
        prog,
        attribute,
        function,
        output,
        // Data Types
        integer,
        floating,
        str,
        bool
    }

    private static class Token<T> {
        TokenType type;
        T data;

        public Token(T value, TokenType type) {
            this.data = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return type + " : " + data.getClass().getSimpleName() + " " + data;
        }
    }

    private static LinkedList<Token<?>> Lexer(String cmd) throws Exception {
        boolean isString = false;
        boolean isFinished = false;
        int progCount = 0;
        boolean attribute = false;
        String cur = "";
        LinkedList<String> strList = new LinkedList<>();
        LinkedList<Token<?>> lexedList = new LinkedList<>();
        int i = 0;
        // Custom Split
        while (i < cmd.length() && !isFinished) {
            char curChar = cmd.charAt(i);
            // System.out.println("Cur: " + cur);
            if (isString) {
                if (curChar == '"') {
                    cur += curChar;
                    isString = !isString;
                } else {
                    cur += curChar;
                }
            } else if (progCount > 0) {
                cur += curChar;

                if (curChar == '{') {
                    progCount++;
                } else if (curChar == '}') {
                    progCount--;

                    if (progCount == 0) {
                        strList.add(cur);
                        cur = "";
                    }
                } else if (curChar == '"') {
                    isString = !isString;
                }
            } else if (attribute) {
                cur += curChar;
                if (curChar == ')') {
                    attribute = false;
                    strList.add(cur);
                    cur = "";
                } else if (curChar == '"') {
                    isString = !isString;
                }
            } else {
                switch (curChar) {
                    case ' ':
                        if (cur.length() != 0) {
                            strList.add(cur);
                        }
                        cur = "";
                        break;
                    case '{':
                        cur += curChar;
                        progCount++;
                        break;
                    case '(':
                        cur += curChar;
                        attribute = true;
                        break;
                    case '.':
                        if (cur.length() != 0) {
                            strList.add(cur);
                        }
                        isFinished = true;
                        break;
                    case '"':
                        cur += curChar;
                        isString = !isString;
                        break;
                    default:
                        cur += curChar;
                        break;
                }
            }
            i++;
        }

        for (i = 0; i < strList.size(); i++) {
            String curStr = strList.get(i);
            if (curStr.charAt(0) == '/') {
                switch (curStr) {
                    case "/add", "/sub", "/mul", "/div", "/mod", "/pow":
                        lexedList.add(new Token<String>(curStr, TokenType.arithmetic));
                        break;
                    case "/abs":
                        lexedList.add(new Token<String>(curStr, TokenType.singleArith));
                        break;
                    case "/print", "/println":
                        lexedList.add(new Token<String>(curStr, TokenType.output));
                        break;
                    case "/var":
                        lexedList.add(new Token<String>(curStr, TokenType.vardec));
                        break;
                    case "/and", "/or":
                        lexedList.add(new Token<String>(curStr, TokenType.logical));
                        break;
                    case "/gteq", "/gt", "/lteq", "/lt", "/eq", "/neq":
                        lexedList.add(new Token<String>(curStr, TokenType.conditional));
                        break;
                    case "/not":
                        lexedList.add(new Token<String>(curStr, TokenType.logicalnot));
                        break;
                    case "/if":
                        lexedList.add(new Token<String>(curStr, TokenType.ifstmt));
                        break;
                    default:
                        lexedList.add(new Token<String>(curStr, TokenType.var));
                        break;
                }
            } else if (curStr.charAt(0) == '"') {
                lexedList.add(new Token<String>(curStr.substring(1, curStr.length() - 1), TokenType.str));
            } else if (curStr.charAt(0) == '(') {
                lexedList.add(new Token<String>(curStr.substring(1, curStr.length() - 1), TokenType.attribute));
            } else if (curStr.charAt(0) == '{') {
                lexedList.add(new Token<String>(curStr.substring(1, curStr.length() - 1), TokenType.prog));
            } else {
                if (curStr.contains(".")) {
                    try {
                        lexedList.add(new Token<Double>(Double.parseDouble(curStr), TokenType.floating));
                    } catch (NumberFormatException e) {
                        throw new Exception("HLANG Lexer cannot determine that " + curStr + " is a float.");
                    }
                } else if (curStr.equals("T")) {
                    lexedList.add(new Token<Boolean>(true, TokenType.bool));
                } else if (curStr.equals("NIL")) {
                    lexedList.add(new Token<Boolean>(false, TokenType.bool));
                } else {
                    try {
                        lexedList.add(new Token<Integer>(Integer.parseInt(curStr), TokenType.integer));
                    } catch (NumberFormatException e) {
                        lexedList.add(new Token<String>(curStr, TokenType.varname));
                    }
                }
            }
        }
        return lexedList;
    }

    static class TreeNode {
        Token<?> data;
        LinkedList<TreeNode> children;

        TreeNode(Token<?> d) {
            data = d;
            children = new LinkedList<>();
        }
    }

    private static TreeNode Parser(LinkedList<Token<?>> lexedList) {
        if (lexedList.isEmpty()) {
            return null;
        }
        Token<?> cur = lexedList.removeFirst();

        TreeNode curNode = new TreeNode(cur);
        int childrenCount = 0;
        switch (cur.type) {
            case TokenType.arithmetic, TokenType.vardec, TokenType.logical, TokenType.conditional, TokenType.function:
                childrenCount = 2;
                break;
            case TokenType.singleArith, TokenType.logicalnot, TokenType.output:
                childrenCount = 1;
                break;
            case TokenType.ifstmt:
                childrenCount = 3;
                break;
            default:
                childrenCount = 0;
                break;
        }

        if (childrenCount != -1) {
            for (int i = 0; i < childrenCount; i++) {
                TreeNode child = Parser(lexedList);
                curNode.children.add(child);
            }
        }
        return curNode;
    }

    private static LinkedList<Token<?>> Interpreter(TreeNode tree) {

        Stack<TreeNode> postStack = new Stack<>();
        Stack<Token<?>> popOrder = new Stack<>();
        LinkedList<Token<?>> res = new LinkedList<>();

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

    private static void SingleLineCompiler(String cmd, HashMap<String, Token<?>> variables, Stack<Token<?>> ValStack)
            throws Exception {
        LinkedList<Token<?>> interpret = Interpreter(Parser(Lexer(cmd)));
        //System.out.println("Interpeted String: " + interpret);
        while (!interpret.isEmpty()) {
            Token<?> cur = interpret.remove();
            switch (cur.type) {
                case TokenType.integer, TokenType.floating, TokenType.str, TokenType.bool, TokenType.varname,
                        TokenType.prog, TokenType.attribute:
                    ValStack.push(cur);
                    break;
                case TokenType.arithmetic:
                    switch ((String) cur.data) {
                        case "/add":
                            Token<?> X2 = ValStack.pop();
                            Token<?> X1 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 + v2, TokenType.floating));
                            } else if (X1.type == TokenType.str) {
                                String v1 = (String) X1.data;
                                if (X2.type == TokenType.str) {
                                    String v2 = (String) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X2.type == TokenType.integer) {
                                    int v2 = (Integer) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X2.type == TokenType.floating) {
                                    double v2 = (Double) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X2.type == TokenType.bool) {
                                    boolean v2 = (Boolean) X2.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                }
                            } else if (X2.type == TokenType.str) {
                                String v2 = (String) X2.data;
                                if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                } else if (X1.type == TokenType.bool) {
                                    boolean v1 = (Boolean) X1.data;
                                    ValStack.push(new Token<>(v2 + v1, TokenType.str));
                                }
                            } else {
                                throw new Exception("Cannot Compile: Addition Type Error!");
                            }
                            break;
                        case "/sub":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 - v2, TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Subtraction Type Error!");
                            }
                            break;
                        case "/mul":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 * v2, TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Multiplication Type Error!");
                            }
                            break;
                        case "/div":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.integer));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(v1 / v2, TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Division Type Error!");
                            }
                            break;
                        case "/mod":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(v1 % v2, TokenType.integer));
                            } else {
                                throw new Exception("Cannot Compile: Modulus Type Error!");
                            }
                            break;
                        case "/pow":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.integer && X2.type == TokenType.integer) {
                                int v1 = (Integer) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));
                            } else if (X1.type == TokenType.integer && X2.type == TokenType.floating) {
                                int v1 = (Integer) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));
                            } else if (X1.type == TokenType.floating && X2.type == TokenType.integer) {
                                double v1 = (Double) X1.data;
                                int v2 = (Integer) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));

                            } else if (X1.type == TokenType.floating && X2.type == TokenType.floating) {
                                double v1 = (Double) X1.data;
                                double v2 = (Double) X2.data;

                                ValStack.push(new Token<>(Math.pow(v1, v2), TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Exponent Type Error!");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.singleArith:
                    switch ((String) cur.data) {
                        case "/abs":
                            Token<?> X = ValStack.pop();
                            if (X.type == TokenType.integer) {
                                int v = (Integer) X.data;
                                ValStack.push(new Token<>(Math.abs(v), TokenType.floating));
                            } else if (X.type == TokenType.floating) {
                                double v = (Double) X.data;
                                ValStack.push(new Token<>(Math.abs(v), TokenType.floating));
                            } else {
                                throw new Exception("Cannot Compile: Addition Type Error!");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.output:
                    switch ((String) cur.data) {
                        case "/print":
                            System.out.print(ValStack.pop().data);
                            break;
                        case "/println":
                            System.out.println(ValStack.pop().data);
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.vardec:
                    switch ((String) cur.data) {
                        case "/var":
                            Token<?> variable = ValStack.pop();
                            Token<?> dat = ValStack.pop();
                            if (variable.type == TokenType.varname) {
                                variables.put((String) variable.data, dat);
                            } else {
                                throw new Exception("Cannot Compile: Invaild Variable Name!");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.var:
                    String varName = ((String) cur.data).substring(1);
                    if (variables.containsKey(varName)) {
                        ValStack.push(variables.get(varName));
                    }
                    break;
                case TokenType.logical:
                    switch ((String) cur.data) {
                        case "/and":
                            Token<?> X2 = ValStack.pop();
                            Token<?> X1 = ValStack.pop();
                            if (X1.type == TokenType.bool && X2.type == TokenType.bool) {
                                ValStack.push(new Token<>(((Boolean) X1.data) && ((Boolean) X2.data), TokenType.bool));
                            } else {
                                throw new Exception("Not a bool type.");
                            }
                            break;
                        case "/or":
                            X2 = ValStack.pop();
                            X1 = ValStack.pop();
                            if (X1.type == TokenType.bool && X2.type == TokenType.bool) {
                                ValStack.push(new Token<>(((Boolean) X1.data) || ((Boolean) X2.data), TokenType.bool));
                            } else {
                                throw new Exception("Not a bool type.");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.logicalnot:
                    switch ((String) cur.data) {
                        case "/not":
                            Token<?> X = ValStack.pop();
                            if (X.type == TokenType.bool) {
                                ValStack.push(new Token<>(!((Boolean) X.data), TokenType.bool));
                            } else {
                                throw new Exception("Not a bool type.");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.conditional:
                    switch ((String) cur.data) {
                        case "/gteq":
                            Token<?> X1 = ValStack.pop();
                            Token<?> X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 >= v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 >= v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) >= 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/gt":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 > v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 > v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) > 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/lteq":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 <= v2) {

                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 <= v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) <= 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/lt":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 < v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 < v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) < 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/eq":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 == v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 == v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) == 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        case "/neq":
                            X1 = ValStack.pop();
                            X2 = ValStack.pop();

                            if (X1.type == X2.type) {
                                if (X1.type == TokenType.integer) {
                                    int v1 = (Integer) X1.data;
                                    int v2 = (Integer) X2.data;
                                    if (v1 != v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.floating) {
                                    double v1 = (Double) X1.data;
                                    double v2 = (Double) X2.data;
                                    if (v1 != v2) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else if (X1.type == TokenType.str) {
                                    String v1 = (String) X1.data;
                                    String v2 = (String) X2.data;
                                    if (v1.compareTo(v2) != 0) {
                                        ValStack.push(new Token<>(true, TokenType.bool));
                                    } else {
                                        ValStack.push(new Token<>(false, TokenType.bool));
                                    }
                                } else {
                                    throw new Exception("Cannot compare /gteq with bools.");
                                }
                            } else {
                                throw new Exception("Type mismatch. Can only compare two values of the same type.");
                            }
                            break;
                        default:
                            throw new Exception("Cannot Compile: Invaild Command!");
                    }
                    break;
                case TokenType.ifstmt:
                    Token<?> checkCond = ValStack.pop();
                    Token<?> trueCond = ValStack.pop();
                    Token<?> elseCond = ValStack.pop();
                    Compiler((String) checkCond.data, variables, ValStack);
                    Token<?> condition = ValStack.pop();
                    boolean cond = (Boolean) condition.data;
                    if (cond) {
                        Compiler((String) trueCond.data, variables, ValStack);
                    } else {
                        Compiler((String) elseCond.data, variables, ValStack);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static void Compiler(String cmd, HashMap<String, Token<?>> variables, Stack<Token<?>> ValStack)
            throws Exception {
        ArrayList<String> compiledLines = customSplit(cmd);
        //System.out.println("Split: " + compiledLines);
        for (int i = 0; i < compiledLines.size(); i++) {
            SingleLineCompiler(compiledLines.get(i) + ".", variables, ValStack);
        }
    }

    private static ArrayList<String> customSplit(String cmd) {
        ArrayList<String> res = new ArrayList<>();
        boolean ignoreProg = false;
        boolean ignoreAtt = false;
        boolean ignoreStr = false;
        String cur = "";
        for (int i = 0; i < cmd.length(); i++) {
            char curchar = cmd.charAt(i);
            if (curchar == '"') {
                ignoreStr = !ignoreStr;
                cur += curchar;
            } else if (curchar == '(') {
                ignoreAtt = true;
                cur += curchar;
            } else if (curchar == '{') {
                ignoreProg = true;
                cur += curchar;
            } else if (curchar == ')') {
                ignoreAtt = false;
                cur += curchar;
            } else if (curchar == '}') {
                ignoreProg = false;
                cur += curchar;
            } else if (curchar == '.') {
                if (ignoreProg || ignoreAtt || ignoreStr) {
                    cur += curchar;
                } else {
                    res.add(cur);
                    cur = "";
                }
            } else {
                cur += curchar;
            }
        }
        if (cur.length() > 0) {
            res.add(cur);
        }
        return res;
    }

    public static void main(String[] args) throws Exception {
        String cmd = "/var x 200. /var y 200. /if (/gteq /x /y.) {/if (/eq /x /y.) {/print \"X is equal to Y\".} {/print \"X is bigger than Y\".}} {/print \"Y is \". /print \" Bigger Than X\".}.";
        Compiler(cmd, new HashMap<>(), new Stack<>());
    }
}
