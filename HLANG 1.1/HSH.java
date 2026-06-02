import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class HSH {
    public static void main(String[] args) throws FileNotFoundException {
        HashMap<String, LinkedList<String>> varList = new HashMap<>();
        Stack<String> valStack = new Stack<>();
        HashMap<String, HLangFunct> functionList = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        boolean quit = false;
        while(!quit){
            System.out.print("/: ");
            String input = sc.nextLine();
            quit = runCommand(input, varList, valStack, functionList);
        }
    }

    private static boolean runCommand(String cmd, HashMap<String, LinkedList<String>> varList, Stack<String> valStack, HashMap<String, HLangFunct> functionList) throws FileNotFoundException {
        String[] cmdSpl = cmd.split(" ");
        Queue<String> commandQueue = new LinkedList<>();
        switch (cmdSpl[0]) {
            //Exits the terminal
            case "quit":
                return true;
            //Directly Runs Script without saving
            case "hlang":
                HLang.runScript(cmdSpl[1]);
                break;
            //Runs the script and saves local variables
            case "load":
                Scanner fc = new Scanner(new File(cmdSpl[1] + ".hlang"));
                while (fc.hasNextLine()) {
                    commandQueue.add(fc.nextLine());
                }
                while (!commandQueue.isEmpty()) {
                    String line = commandQueue.remove();
                    while (line.charAt(line.length() - 1) != '.' && !commandQueue.isEmpty()) {
                        line += commandQueue.remove();
                        // System.out.println(line);
                    }
                    HLang.compile(line, varList, valStack, functionList);
                }
                break;
            case "showstack":
                System.out.println("Command Stack: " + valStack);
                break;
            case "showvars":
                System.out.println("Local Variables: ");
                for(String n : varList.keySet()){
                    System.out.println(n + " = " + varList.get(n));
                }
                break;
            case "showfun":
                System.out.println("Local Variables: ");
                for(String n : functionList.keySet()){
                    System.out.println(n + " -> " + Arrays.toString(functionList.get(n).getParam()));
                }
                break;
            //Allows for the compiling of commands
            default:
                HLang.compile("/"+cmd, varList, valStack, functionList);
                break;
        }
        return false;
    }
}