import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

public class HLang {
    public static void runScript(String filepath) throws FileNotFoundException {
        Scanner fc = new Scanner(new File(filepath + ".hlang"));
        Queue<String> commandQueue = new LinkedList<>();
        HashMap<String, LinkedList<String>> varList = new HashMap<>();
        HashMap<String, HLangFunct> functionList = new HashMap<>();
        Stack<String> s = new Stack<>();
        while (fc.hasNextLine()) {
            commandQueue.add(fc.nextLine());
        }
        while (!commandQueue.isEmpty()) {
            String line = commandQueue.remove();
            while (line.charAt(line.length() - 1) != '.' && !commandQueue.isEmpty()) {
                line += commandQueue.remove();
                // System.out.println(line);
            }
            compile(line, varList, s, functionList);
        }
    }

    public static LinkedList<String> splitPeriods(String n) {
        String cur = "";
        LinkedList<String> res = new LinkedList<>();
        boolean inQuotes = false;

        for (int i = 0; i < n.length(); i++) {
            char c = n.charAt(i);
            // System.out.println(cur);
            if (c == '"') {
                inQuotes = !inQuotes; // toggle inside quotes
                cur += "\"";
            } else if (c == '.' && !inQuotes) {
                // Split here since we're outside quotes
                res.add(cur);
                cur = "";
            } else {
                cur += c;
            }
        }

        // Add any remaining text
        if (!cur.isEmpty()) {
            res.add(cur);
        }

        return res;
    }

    public static void compile(String n, HashMap<String, LinkedList<String>> varList, Stack<String> valStack,
            HashMap<String, HLangFunct> functionList) {
        Lexer lex = new Lexer();
        LinkedList<String> arr = splitPeriods(n);
        for (int i = 0; i < arr.size(); i++) {
            String cur = arr.get(i);
            LinkedList<String> sn = Lexer.interpreter(lex.parse(lex.tokenize(cur)));
            // System.out.println(cur);
            // System.out.println(arr.toString());
            //System.out.println("Interpreted String: " + sn.toString());
            for (int j = 0; j < sn.size(); j++) {
                // System.out.println("Stack: " + valStack.toString());
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
                    case "mod":
                        first = Integer.parseInt(valStack.pop());
                        second = Integer.parseInt(valStack.pop());
                        valStack.push("" + (first % second));
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
                        f = f.replace("\"", "");
                        s = valStack.pop();
                        s = s.replace("\"", "");
                        valStack.push("\"" + f + s + "\"");
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
                        commands.put("endif", -1);
                        commands.put("}", -1);
                        commands.put("not", 1);
                        commands.put("and", 2);
                        commands.put("or", 2);

                        int k = j + 1;
                        int b = 0;

                        // false, true, cond
                        String[] branch = new String[3];
                        while (k < sn.size() && b < 3) {
                            // System.out.println("Current: " + sn.get(k));
                            // System.out.println("Branch: " + Arrays.toString(branch));
                            if (commands.containsKey(sn.get(k))) {
                                if (sn.get(k).equals("endif")) {
                                    int m = k + 1;
                                    String fullcmd = "/if";
                                    Stack<String> tempStack = new Stack<>();
                                    while (!sn.get(m).equals("if")) {
                                        tempStack.push(removeBadPeriods(sn.get(m)));
                                        m++;
                                    }
                                    while (!tempStack.isEmpty()) {
                                        if ((commands.containsKey(tempStack.peek()) || tempStack.peek().equals("{"))
                                                && !tempStack.peek().equals("}")) {
                                            fullcmd += " /" + tempStack.pop();
                                        } else {
                                            fullcmd += " " + tempStack.pop();
                                        }
                                    }
                                    fullcmd += " /endif.";
                                    branch[b] = fullcmd;
                                    b++;
                                    k = m;
                                } else if (sn.get(k).equals("}")) {
                                    int m = k + 1;
                                    String fullcmd = "";
                                    Stack<String> tempStack = new Stack<>();
                                    while (!sn.get(m).equals("{")) {
                                        tempStack.push(sn.get(m));
                                        m++;
                                    }
                                    while (!tempStack.isEmpty()) {
                                        fullcmd += tempStack.pop();
                                    }
                                    branch[b] = fullcmd;
                                    b++;
                                    k = m;
                                } else {
                                    branch[b] = "/" + sn.get(k);
                                    for (int count = 0; count < commands.get(sn.get(k)); count++) {
                                        branch[b] += " " + valStack.pop();
                                    }
                                    branch[b] += ".";
                                    b++;
                                }

                            } else {
                                valStack.push(sn.get(k));
                            }
                            k++;
                        }
                        //System.out.println("Branch: " + Arrays.toString(branch));
                        compile(branch[2], varList, valStack, functionList);
                        String compare = valStack.pop();
                        if (compare.equals("T")) {
                            //System.out.println("True Branch");
                            compile(branch[1], varList, valStack, functionList);

                        } else {
                            //System.out.println("False Branch");
                            compile(branch[0], varList, valStack, functionList);
                        }
                        j = k;

                        break;
                    case "and":
                        f = valStack.pop();
                        s = valStack.pop();
                        if ((f.equals("T") || f.equals("NIL")) && (s.equals("T") || s.equals("NIL"))) {
                            if (f.equals("T") && s.equals("T")) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } else {
                            valStack.push("NIL");
                        }
                        break;
                    case "or":
                        f = valStack.pop();
                        s = valStack.pop();
                        if ((f.equals("T") || f.equals("NIL")) && (s.equals("T") || s.equals("NIL"))) {
                            if (f.equals("T") || s.equals("T")) {
                                valStack.push("T");
                            } else {
                                valStack.push("NIL");
                            }
                        } else {
                            valStack.push("NIL");
                        }
                        break;
                    case "not":
                        f = valStack.pop();
                        if (f.equals("T") || f.equals("NIL")) {
                            if (f.equals("T")) {
                                valStack.push("NIL");
                            } else {
                                valStack.push("T");
                            }
                        } else {
                            valStack.push("NIL");
                        }
                        break;
                    case "endfun":
                        int funCount = j + 1;
                        LinkedList<String> fullcmd = new LinkedList<>();
                        LinkedList<String> params = new LinkedList<>();
                        String functionName;

                        if (sn.get(funCount).equals("]")) {
                            funCount++;
                            while (!sn.get(funCount).equals("[") && funCount < sn.size()) {
                                fullcmd.addFirst(sn.get(funCount));
                                funCount++;
                            }
                        } else {
                            fullcmd.add(sn.get(funCount));
                        }

                        funCount++;

                        if (sn.get(funCount).equals("]")) {
                            funCount++;
                            while (!sn.get(funCount).equals("[") && funCount < sn.size()) {
                                params.addFirst(sn.get(funCount));
                                funCount++;
                            }
                        } else {
                            fullcmd.add(sn.get(funCount));
                        }
                        funCount++;

                        functionName = sn.get(funCount);
                        funCount++;
                        String[] arrrr = params.toArray(new String[0]);
                        HLangFunct funct = new HLangFunct(arrrr, fullcmd);
                        // System.out.println("Params: " + Arrays.toString(funct.getParam()));
                        // System.out.println("Commands: "+ funct.getCommands().toString());
                        // System.out.println("Funct Name: "+ functionName);
                        functionList.put(functionName, funct);
                        break;
                    case "usefun":
                        int counter = j + 1;
                        String funcName = sn.get(counter);
                        counter++;
                        String lis = sn.get(counter);
                        counter++;
                        // System.out.println(funcName);
                        // System.out.println(functionList.containsKey(funcName));
                        functionList.get(funcName).useFunction(lis, varList, valStack, functionList);
                        j = counter;
                        break;
                    case "return":

                        break;
                    case "newlist":
                        String listName = valStack.pop();
                        varList.put(listName, new LinkedList<>());
                        break;
                    case "uselist":
                        listName = valStack.pop();
                        String use = "(";
                        LinkedList<String> listUse = varList.get(listName);
                        for (int lu = 0; lu < listUse.size() - 1; lu++) {
                            use += listUse.get(lu) + " ";
                        }
                        use += listUse.get(listUse.size() - 1) + ")";
                        valStack.push(use);
                        break;
                    case "car":
                        String headPop = valStack.pop();
                        String head = varList.get(headPop).get(0);
                        valStack.push(head);
                        break;
                    case "cdr":
                        String tailPop = valStack.pop();
                        varList.get(tailPop).removeFirst();
                        valStack.push(tailPop);
                        break;
                    case "consvar":
                        listName = valStack.pop();
                        String varName = valStack.pop();
                        if (varList.get(varName).size() == 1) {
                            varList.get(listName).add(varList.get(varName).get(0));
                        } else {
                            for (int ic = 0; i < varList.get(varName).size(); ic++) {
                                varList.get(listName).add(varList.get(varName).get(ic));
                            }
                        }
                        break;
                    case "split":
                        listName = valStack.pop();
                        String regex = valStack.pop();
                        String useVariable = varList.get(listName).get(0);
                        String[] split = useVariable.split(regex);
                        varList.get(listName).clear();
                        for (int bm = 0; bm < split.length; bm++) {
                            varList.get(listName).add(split[bm]);
                        }
                        break;
                    case "desplit":
                        listName = valStack.pop();
                        regex = valStack.pop();
                        LinkedList<String> temporary = varList.get(listName);
                        String output = "";
                        if (temporary.size() > 0) {
                            for (int bm = 0; bm < temporary.size() - 1; bm++) {
                                output += temporary.get(bm) + regex;
                            }
                            output += temporary.get(temporary.size() - 1);
                            varList.get(listName).clear();
                            varList.get(listName).add(output);
                        }
                        else{
                            varList.get(listName).add("?");
                        }
                        break;
                    case "null":
                        listName = valStack.pop();
                        if (varList.get(listName).size() == 1 && varList.get(listName).get(0).equals("?")) {
                            valStack.push("T");
                        } else {
                            valStack.push("NIL");
                        }
                        break;
                    default:
                        valStack.push(sn.get(j));
                        break;
                }
            }
        }
    }

    private static String removeBadPeriods(String s) {
        String ret = "";
        boolean inQuotes = false;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '"') {
                inQuotes = !inQuotes;
            } else if (s.charAt(i) == '.' && !inQuotes) {
                // literally do nothing
            } else {
                ret += s.charAt(i);
            }
        }

        return ret;
    }
}
