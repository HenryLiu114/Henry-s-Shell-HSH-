public class test {
    public static void main(String[] args) {
        String n = "/{/var x /add 5 4, /print /usevar x}";
        Lexer help = new Lexer();
        System.out.println(Lexer.interpreter(help.parse(help.tokenize(n))).toString());
    }
}
