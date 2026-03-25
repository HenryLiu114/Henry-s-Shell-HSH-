import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class HenryShell {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        HashMap<String, AllocationFile> tempFiles = new HashMap<>();
        Stack<String> commandStack = new Stack<>();
        boolean exit = false;
        String prevOutput = "";
        String currentTempFile = "";
        while (!exit) {
            String userInput = sc.nextLine();
            userInput = " " + userInput;
            String[] spl = userInput.split(" /");
            
            for (int i = 1; i < spl.length; i++) {
                commandStack.push(spl[i]);
            }

            while (!commandStack.isEmpty()) {
                String command = commandStack.pop();
                command += prevOutput;
                String[] att = command.split(" ");
                //Command finder
                switch (att[0]) {
                    case "allocate":
                        tempFiles.put(att[1], new AllocationFile(att[1]));
                        prevOutput = "";
                        break;
                    case "free":
                        tempFiles.remove(att[1]);
                        prevOutput = "";
                        break;
                    
                    case "load":
                        //TO-DO
                        break;
                    case "store":
                        PrintWriter pw = new PrintWriter("./saves/"+currentTempFile+".txt");
                        for(String s : tempFiles.get(currentTempFile).getIntAllos().keySet()){
                            pw.println(s+":"+tempFiles.get(currentTempFile).getIntAllos().get(s)+":int");
                        }
                        for(String s : tempFiles.get(currentTempFile).getFloAllos().keySet()){
                            pw.println(s+":"+tempFiles.get(currentTempFile).getFloAllos().get(s));
                        }
                        for(String s : tempFiles.get(currentTempFile).getStrAllos().keySet()){
                            pw.println(s+":"+tempFiles.get(currentTempFile).getStrAllos().get(s));
                        }
                        pw.close();
                        break;
                    case "delete":
                        //To-do
                        break;
                    case "lallo":
                        //To-do
                        break;
                    case "lstore":
                        //To-do
                        break;
                    case "callo":
                        if(tempFiles.containsKey(att[1])){
                            currentTempFile = att[1];
                        }
                        else{
                            System.out.println("No such file exists");
                        }
                        prevOutput = "";
                        break;
                    case "var":
                        //To-do
                        break;
                    case "add":
                        try {
                            prevOutput = " " + (Double.parseDouble(att[1]) + Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "sub":
                        try {
                            prevOutput = " " + (Double.parseDouble(att[1]) - Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "mul":
                        try {
                            prevOutput = " " + (Double.parseDouble(att[1]) * Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "div":
                        try {
                            prevOutput = " " + (Double.parseDouble(att[1]) / Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "mod":
                        try {
                            prevOutput = " " + (Integer.parseInt(att[1]) % Integer.parseInt(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "pow":
                        try {
                            prevOutput = " " + Math.pow(Double.parseDouble(att[1]), Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "root":
                        try {
                            prevOutput = " " + Math.pow(Double.parseDouble(att[1]), 1 / Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "intdiv":
                        try {
                            prevOutput = " " + (Integer.parseInt(att[1]) / Integer.parseInt(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "rand":
                        Random rand = new Random();
                        try {
                            prevOutput = " " + rand.nextInt(Integer.parseInt(att[1]), Integer.parseInt(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "randf":
                        Random randf = new Random();
                        try {
                            prevOutput = " " + randf.nextDouble(Double.parseDouble(att[1]), Double.parseDouble(att[2]));
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "toint":
                        try {
                            prevOutput = " " + (int)Double.parseDouble(att[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "tofloat":
                        try {
                            prevOutput = " " + Double.parseDouble(att[1]);
                        } catch (NumberFormatException e) {
                            System.out.println("Type Error! ");
                        }
                        break;
                    case "quit":
                        prevOutput = "";
                        exit = true;
                        commandStack.empty();
                        break;
                    default:
                        System.out.println("Command not found");
                        commandStack.empty();
                        break;
                }

            }
            System.out.println(prevOutput);
        }
        sc.close();
    }
}