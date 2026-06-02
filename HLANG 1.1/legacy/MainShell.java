package legacy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class MainShell {
    public static void main(String[] args) {
        String n = "/add /mul 4 3 /add -8 /add /div 9 3 /mul 2 /add 9 10";
        HashMap<String, String> ram = new HashMap<>();
        ram.put("x", "5");
        ram.put("y", "5");
        RunCommand(n, ram);
    }

    static void RunCommand(String command, HashMap<String, String> ram) {
        String[] nar = command.split(" ");
        Stack<String> dataStack = new Stack<>();
        Stack<String> conditionOp = new Stack<>();
        for (int i = nar.length-1; i >= 0; i--) {
            switch (nar[i]) {
                case "/var":
                    String data = dataStack.pop();
                    String varName = dataStack.pop();
                    ram.put(varName, data);
                    break;
                case "/usevar":
                    dataStack.push(ram.get(dataStack.pop()));
                    break;
                case "/add":
                    int a = Integer.parseInt(dataStack.pop());
                    int b = Integer.parseInt(dataStack.pop());
                    dataStack.push((a+b)+"");
                    break;
                case "/sub":
                    a = Integer.parseInt(dataStack.pop());
                    b = Integer.parseInt(dataStack.pop());
                    dataStack.push((a-b)+"");
                    break;
                case "/mul":
                    a = Integer.parseInt(dataStack.pop());
                    b = Integer.parseInt(dataStack.pop());
                    dataStack.push((a*b)+"");
                    break;
                case "/div":
                    a = Integer.parseInt(dataStack.pop());
                    b = Integer.parseInt(dataStack.pop());
                    dataStack.push((a/b)+"");
                    break;
                case "/addf":
                    double af = Double.parseDouble(dataStack.pop());
                    double bf = Double.parseDouble(dataStack.pop());
                    dataStack.push((af+bf)+"");
                    break;
                case "/subf":
                    af = Double.parseDouble(dataStack.pop());
                    bf = Double.parseDouble(dataStack.pop());
                    dataStack.push((af-bf)+"");
                    break;
                case "/mulf":
                    af = Double.parseDouble(dataStack.pop());
                    bf = Double.parseDouble(dataStack.pop());
                    dataStack.push((af*bf)+"");
                    break;
                case "/divf":
                    af = Double.parseDouble(dataStack.pop());
                    bf = Double.parseDouble(dataStack.pop());
                    dataStack.push((af/bf)+"");
                    break;
                case "/mod":
                    a = Integer.parseInt(dataStack.pop());
                    b = Integer.parseInt(dataStack.pop());
                    dataStack.push((a%b)+"");
                    break;
                case "/if":
                    break;
                case "/while":
                    break;
                case "/for":
                    break;
                default:
                    dataStack.push(nar[i]);
                    break;
            }
        }

        while(!dataStack.isEmpty()){
            System.out.println(dataStack.pop());
        }
        
    }

}
