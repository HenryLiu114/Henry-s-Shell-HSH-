package legacy;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Stream;

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
            
            
            //Implement DFS Here

            while (!commandStack.isEmpty()) {
                String command = commandStack.pop();
                command += prevOutput;

                String[] att = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // Command finder
                try {
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
                            Scanner fc = new Scanner(new File("./storage" + att[1]));
                            tempFiles.put(att[1], new AllocationFile(att[1]));
                            while (fc.hasNextLine()) {
                                String stoVar = fc.nextLine();
                                String stoVal = fc.nextLine();
                                String stoType = fc.nextLine();

                                if (stoType == "int") {
                                    tempFiles.get(att[1]).getIntAllos().put(stoVar, Integer.parseInt(stoVal));
                                } else if (stoType == "double") {
                                    tempFiles.get(att[1]).getFloAllos().put(stoVar, Double.parseDouble(stoVal));
                                } else {
                                    tempFiles.get(att[1]).getStrAllos().put(stoVar, stoVal);
                                }
                            }
                            prevOutput = "";
                            break;
                        case "store":
                            PrintWriter pw = new PrintWriter("./storage/" + currentTempFile + ".txt");
                            for (String s : tempFiles.get(currentTempFile).getIntAllos().keySet()) {
                                pw.println(s + "\n" + tempFiles.get(currentTempFile).getIntAllos().get(s) + "\nint");
                            }
                            for (String s : tempFiles.get(currentTempFile).getFloAllos().keySet()) {
                                pw.println(s + "\n" + tempFiles.get(currentTempFile).getFloAllos().get(s) + "\ndouble");
                            }
                            for (String s : tempFiles.get(currentTempFile).getStrAllos().keySet()) {
                                pw.println(s + "\n" + tempFiles.get(currentTempFile).getStrAllos().get(s) + "\nstring");
                            }
                            pw.close();
                            prevOutput = "";
                            break;
                        case "delete":
                            Path path = Paths.get("./storage/" + att[1] + ".txt");
                            try {
                                boolean deleted = Files.deleteIfExists(path);
                                if (deleted) {
                                    System.out.println("File deleted successfully.");
                                } else {
                                    System.out.println("File does not exist.");
                                }
                            } catch (IOException e) {
                                System.err.println("Failed to delete the file: " + e.getMessage());
                            }
                            break;
                        case "lallo":
                            for (String k : tempFiles.keySet()) {
                                System.out.println(k);
                            }
                            System.out.println("Currently Loaded File: " + currentTempFile);
                            break;
                        case "lstore":
                            try (Stream<Path> stream = Files.list(Paths.get("./storage"))) {
                                stream.filter(Files::isRegularFile)
                                        .forEach(System.out::println);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "callo":
                            if (tempFiles.containsKey(att[1])) {
                                currentTempFile = att[1];
                            } else {
                                System.out.println("No such file exists");
                            }
                            prevOutput = "";
                            break;
                        case "var":
                            try {
                                tempFiles.get(currentTempFile).getIntAllos().put(att[1], Integer.parseInt(att[2]));
                            } catch (NumberFormatException e) {
                                try {
                                    tempFiles.get(currentTempFile).getFloAllos().put(att[1],
                                            Double.parseDouble(att[2]));
                                } catch (NumberFormatException e1) {
                                    tempFiles.get(currentTempFile).getStrAllos().put(att[1], att[2]);
                                }
                            }
                            prevOutput = "";
                            break;
                        case "lvar":
                            for (String k : tempFiles.get(currentTempFile).getIntAllos().keySet()) {
                                System.out.println(
                                        "int " + k + " = " + tempFiles.get(currentTempFile).getIntAllos().get(k));
                            }

                            for (String k : tempFiles.get(currentTempFile).getFloAllos().keySet()) {
                                System.out.println(
                                        "double " + k + " = " + tempFiles.get(currentTempFile).getFloAllos().get(k));
                            }

                            for (String k : tempFiles.get(currentTempFile).getStrAllos().keySet()) {
                                System.out.println(
                                        "string " + k + " = " + tempFiles.get(currentTempFile).getStrAllos().get(k));
                            }
                            break;
                        case "svar":
                            if (att[1].equals("int")) {
                                try {
                                    tempFiles.get(currentTempFile).getIntAllos().replace(att[2],
                                            Integer.parseInt(att[3]));
                                } catch (NumberFormatException e) {
                                    System.out.println("Invaild Type! ");
                                }
                            } else if (att[1].equals("double")) {
                                try {
                                    tempFiles.get(currentTempFile).getFloAllos().replace(att[2],
                                            Double.parseDouble(att[3]));
                                } catch (NumberFormatException e) {
                                    System.out.println("Invaild Type! ");
                                }
                            } else if (att[1].equals("string")) {
                                try {
                                    tempFiles.get(currentTempFile).getStrAllos().replace(att[2], att[3]);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invaild Type! ");
                                }
                            } else {
                                System.out.println("Invaild Type! ");
                            }
                            prevOutput = "";
                            break;
                        case "rvar":
                            if (att[1].equals("int")) {
                                tempFiles.get(currentTempFile).getIntAllos().remove(att[2]);
                            } else if (att[1].equals("double")) {
                                tempFiles.get(currentTempFile).getFloAllos().remove(att[2]);
                            } else if (att[1].equals("string")) {
                                tempFiles.get(currentTempFile).getStrAllos().remove(att[2]);
                            } else {
                                System.out.println("Invaild Type! ");
                            }
                            prevOutput = "";
                            break;
                        case "intram":
                            for (String k : tempFiles.get(currentTempFile).getIntAllos().keySet()) {
                                System.out.println(
                                        "int " + k + " = " + tempFiles.get(currentTempFile).getIntAllos().get(k));
                            }
                            prevOutput = "";
                            break;
                        case "floatram":
                            for (String k : tempFiles.get(currentTempFile).getFloAllos().keySet()) {
                                System.out.println(
                                        "double " + k + " = " + tempFiles.get(currentTempFile).getFloAllos().get(k));
                            }
                            prevOutput = "";
                            break;
                        case "strram":
                            for (String k : tempFiles.get(currentTempFile).getStrAllos().keySet()) {
                                System.out.println(
                                        "string " + k + " = " + tempFiles.get(currentTempFile).getStrAllos().get(k));
                            }
                            prevOutput = "";
                            break;
                        case "defvar":
                            if (att[1] == "int") {
                                try {
                                    tempFiles.get(currentTempFile).getIntAllos().put(att[2], Integer.parseInt(att[3]));
                                } catch (NumberFormatException e) {
                                    System.out.println("Invaild Type! ");
                                }
                            } else if (att[1] == "double") {
                                try {
                                    tempFiles.get(currentTempFile).getFloAllos().put(att[2],
                                            Double.parseDouble(att[3]));
                                } catch (NumberFormatException e) {
                                    System.out.println("Invaild Type! ");
                                }
                            } else if (att[1] == "string") {
                                try {
                                    tempFiles.get(currentTempFile).getStrAllos().put(att[2], att[3]);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invaild Type! ");
                                }
                            } else {
                                System.out.println("Invaild Type! ");
                            }
                            prevOutput = "";
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
                                prevOutput = " "
                                        + randf.nextDouble(Double.parseDouble(att[1]), Double.parseDouble(att[2]));
                            } catch (NumberFormatException e) {
                                System.out.println("Type Error! ");
                            }
                            break;
                        case "toint":
                            try {
                                prevOutput = " " + (int) Double.parseDouble(att[1]);
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
                } catch (ArrayIndexOutOfBoundsException e) {
                }
                catch (IndexOutOfBoundsException e1){
                }

            }
            System.out.println(prevOutput);
        }
        sc.close();
    }
} 