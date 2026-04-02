import java.util.Arrays;

public class HLang {
    public static void main(String[] args) {
        Lexer Help = new Lexer();
        String n = "/var x /mul 45 55. /div 4 3.";
        System.out.println(Arrays.toString(Help.tokenize(n).toArray()));
        Lexer.interpreter(Help.parse(Help.tokenize(n)));
    }
}
