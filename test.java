public class test {
    public static void main(String[] args) {
        String n = "/{/var x /add 5 4, /print /usevar x}";
        String n2 = "/print \"Hello World, Henry ate a Dinosaur.\".";
        Lexer help = new Lexer();
        System.out.println(help.tokenize(n));
        System.out.println(help.tokenize(n2));
    }
}
