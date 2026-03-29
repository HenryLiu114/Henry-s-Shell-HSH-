import java.util.Arrays;

public class HLang {
    public static void main(String[] args) {
        Lexer Help = new Lexer();
        System.out.println(Arrays.toString(Help.tokenize("/var x /mul 45 /div 4 3.").toArray()));
    }
}
