import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.io.*;
import java.util.Scanner;

public class HLang {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner fc = new Scanner(new File("codeTest.hlang"));
        Queue<String> commandQueue = new LinkedList<>();
        HashMap<String, LinkedList<String>> varList = new HashMap<>();
        while(fc.hasNextLine()){
            commandQueue.add(fc.nextLine());
        }
        while(!commandQueue.isEmpty()){
            compile(commandQueue.remove(), varList);
        }
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

    public static void compile(String n, HashMap<String, LinkedList<String>> varList) {
        Lexer lex = new Lexer();
        LinkedList<String> arr = splitPeriods(n);
        for (int i = 0; i < arr.size(); i++) {
            String cur = arr.get(i);
            LinkedList<String> sn = Lexer.interpreter(lex.parse(lex.tokenize(cur)));
            Stack<String> valStack = new Stack<>();
            for (int j = 0; j < sn.size(); j++) {
                switch (sn.get(j)) {
                    case "add":
                        int first;
                        int second;
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
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
                        String f;
                        String s;
                        f = valStack.pop();
                        s = valStack.pop();
                        LinkedList<String> l = new LinkedList<>();
                        if(s.charAt(0) == '(' && s.charAt(s.length()-1) == ')'){
                            s = s.substring(1, s.length()-1);
                            String[] sarg = s.split(" ");
                            for(int k = 0; k < sarg.length; k++){
                                l.add(sarg[k]);
                            }
                        }
                        else{
                            l.add(s);
                        }
                        varList.put(f, l);
                        break;
                    case "varat":
                        String varname = valStack.pop();
                        int index = Integer.parseInt(valStack.pop());
                        valStack.push(varList.get(varname).get(index));
                        break;
                    case "free":
                        varname = valStack.pop();
                        varList.remove(varname);
                        break;
                    case "usevar":
                        varname = valStack.pop();
                        valStack.push(varList.get(varname).get(0));
                        break;
                    case "print":
                        String val = valStack.pop();
                        System.out.println(val);
                        break;
                    case "concat":
                        f = valStack.pop();
                        s = valStack.pop();
                        valStack.push(f+s);
                        break;
                    default:
                        valStack.push(sn.get(j));
                        break;
                }
            }
        }
    }
}
