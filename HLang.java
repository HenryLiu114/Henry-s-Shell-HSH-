import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import java.io.*;
import java.util.Scanner;

public class HLang {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner fc = new Scanner(new File(args[0] + ".hlang"));
        Queue<String> commandQueue = new LinkedList<>();
        HashMap<String, LinkedList<String>> varList = new HashMap<>();
        Stack<String> s = new Stack<>();
        while (fc.hasNextLine()) {
            commandQueue.add(fc.nextLine());
        }
        while (!commandQueue.isEmpty()) {
            String line = commandQueue.remove();
            while (line.charAt(line.length() - 1) != '.' && !commandQueue.isEmpty()) {
                line += commandQueue.remove();
            }

            compile(line, varList, s);
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

    public static void compile(String n, HashMap<String, LinkedList<String>> varList, Stack<String> valStack) {
        Lexer lex = new Lexer();
        LinkedList<String> arr = splitPeriods(n);
        for (int i = 0; i < arr.size(); i++) {
            String cur = arr.get(i);
            LinkedList<String> sn = Lexer.interpreter(lex.parse(lex.tokenize(cur)));
            for (int j = 0; j < sn.size(); j++) {
                switch (sn.get(j)) {
                    case "add":
                        int first;
                        int second;
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push("" + (first + second));
                        break;
                    case "sub":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push("" + (first - second));
                        break;
                    case "mul":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push("" + (first * second));
                        break;
                    case "div":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push("" + (first / second));
                        break;
                    case "var":
                        String f;
                        String s;
                        f = valStack.pop();
                        s = valStack.pop();
                        LinkedList<String> l = new LinkedList<>();
                        if (s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')') {
                            s = s.substring(1, s.length() - 1);
                            String[] sarg = s.split(" ");
                            for (int k = 0; k < sarg.length; k++) {
                                l.add(sarg[k]);
                            }
                        } else {
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
                        valStack.push(f + s);
                        break;
                    case "eq":
                        f = valStack.pop();
                        s = valStack.pop();
                        try {
                            if (Integer.parseInt(f) == Integer.parseInt(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } catch (NumberFormatException e) {
                            if (f.equals(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        }
                        break;
                    case "lteq":
                        f = valStack.pop();
                        s = valStack.pop();
                        try {
                            if (Integer.parseInt(f) <= Integer.parseInt(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } catch (NumberFormatException e) {
                            if (f.compareTo(s) < 0 || f.equals(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        }
                        break;
                    case "lt":
                        f = valStack.pop();
                        s = valStack.pop();
                        try {
                            if (Integer.parseInt(f) < Integer.parseInt(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } catch (NumberFormatException e) {
                            if (f.compareTo(s) < 0) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        }
                        break;
                    case "gteq":
                        f = valStack.pop();
                        s = valStack.pop();
                        try {
                            if (Integer.parseInt(f) >= Integer.parseInt(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } catch (NumberFormatException e) {
                            if (f.compareTo(s) > 0 || f.equals(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        }
                        break;
                    case "gt":
                        f = valStack.pop();
                        s = valStack.pop();
                        try {
                            if (Integer.parseInt(f) > Integer.parseInt(s)) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } catch (NumberFormatException e) {
                            if (f.compareTo(s) > 0) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        }
                        break;
                    case "endif":
                        HashMap<String, Integer> commands = new HashMap<>();
                        commands.put("add", 2);
                        commands.put("sub", 2);
                        commands.put("mul", 2);
                        commands.put("div", 2);
                        commands.put("var", 2);
                        commands.put("allo", 2);
                        commands.put("free", 1);
                        commands.put("varat", 2);
                        commands.put("usevar", 1);
                        commands.put("print", 1);
                        commands.put("concat", 2);
                        commands.put("eq", 2);
                        commands.put("lteq", 2);
                        commands.put("lt", 2);
                        commands.put("gteq", 2);
                        commands.put("gt", 2);
                        commands.put("if", -1);

                        int k = j+1;
                        int b = 0;
                        //false, true, cond
                        String[] branch = new String[3];
                        while(!sn.get(k).equals("if") && k < sn.size()){
                            if(commands.containsKey(sn.get(k))){
                                branch[b] = "/"+sn.get(k);
                                for(int count = 0; count < commands.get(sn.get(k)); count++){
                                    branch[b] += " " + valStack.pop();
                                }
                                branch[b] += ".";
                                b++;
                            }
                            else{
                                valStack.push(sn.get(k));
                            }
                            k++;
                        }
                        compile(branch[2], varList, valStack);
                        String compare = valStack.pop();
                        if(compare.equals("T")){
                            compile(branch[1], varList, valStack);
                        }
                        else{
                            compile(branch[0], varList, valStack);
                        }
                        j = k;
                        break;
                    default:
                        valStack.push(sn.get(j));
                        break;
                }
            }
        }
    }
}
