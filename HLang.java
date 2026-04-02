import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class HLang {
    public static void main(String[] args) {
        Lexer Help = new Lexer();
        String n = "/var x /add /mul 2 5 /sub /div 18 3 7.";
        HashMap<String, String> varList = new HashMap<>();
        compile(n, varList);
        System.out.println(varList.get("x"));
    }

    public static LinkedList<String> splitPeriods(String n) {
        String cur = "";
        String temp = "";
        LinkedList<String> res = new LinkedList<>();
        for (int i = 0; i < n.length(); i++) {
            if (n.charAt(i) == '.') {
                temp = cur + ".";
                if (temp.contains("\\.")) {
                    cur = temp;
                } else {
                    res.add(cur);
                    cur = "";
                }
            } else {
                cur += n.charAt(i);
            }
        }
        return res;
    }

    public static void compile(String n, HashMap<String, String> varList) {
        Lexer lex = new Lexer();
        LinkedList<String> arr = splitPeriods(n);
        for (int i = 0; i < arr.size(); i++) {
            String cur = arr.get(i);
            LinkedList<String> sn = Lexer.interpreter(lex.parse(lex.tokenize(cur)));
            Stack<String> valStack = new Stack<>();
            for (int j = 0; j < sn.size(); j++) {
                System.out.println(valStack.toString());
                switch (sn.get(j)) {
                    case "add":
                        int first = Integer.parseInt(valStack.pop());
                        int second = Integer.parseInt(valStack.pop());
                        valStack.push(""+(first+second));
                        break;
                    case "sub":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push(""+(first-second));
                        break;
                    case "mul":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push(""+(first*second));
                        break;
                    case "div":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push(""+(first/second));
                        break;
                    case "var":
                        String f = valStack.pop();
                        String s = valStack.pop();
                        varList.put(f, s);
                        break;
                    default:
                        valStack.push(sn.get(j));
                        break;
                }
            }
        }
    }
}
