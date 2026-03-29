import java.util.LinkedList;

public class Lexer {
    public enum TokenType {
        CommandStarter,
        BinaryOperator,
        OpenParth, ClosedParth,
        Var,
        Identifier,
        Integer,
        End;
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
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == ')' || c == '.' || c == '/') {
                if (!curString.isEmpty())
                    res.add(curString);
                curString = "";
                res.add(c + "");
            } else if (c == ' ') {
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
        while (src.length() > 0 && i < spl.size()) {
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
                case "add":
                case "sub":
                case "mul":
                case "div":
                    res.add(tokens(spl.get(i), TokenType.BinaryOperator));
                    break;
                case "var":
                    res.add(tokens(spl.get(i), TokenType.Var));
                    break;
                case ".":
                    res.add(tokens(spl.get(i), TokenType.End));
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

}
