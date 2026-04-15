import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class HLangFunct {
    private String[] param;
    private LinkedList<String> commands;
    
    public HLangFunct(String[] param, String command){
        this.param = param;
        String[] cmd = command.split(";");
        for(int i = 0; i < cmd.length; i++){
            commands.add(cmd[i]);
        }
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }

    public LinkedList<String> getCommands() {
        return commands;
    }

    public void setCommands(LinkedList<String> commands) {
        this.commands = commands;
    }

    public HLangFunct(String[] param, LinkedList<String> commands){
        this.param = param;
        this.commands = commands;
    }

    public void useFunction(String paramList, HashMap<String, LinkedList<String>> varList, Stack<String> valStack, HashMap<String, HLangFunct> functionList){
        String str = "";
        if(paramList.contains("(") && paramList.contains("(")){
            str = paramList.substring(1, paramList.length()-1);
        }
        else{
            HLang.compile("/uselist " + paramList, varList, valStack, functionList);
            str = valStack.pop();
            //System.out.println(str);
            str = str.replace("(","");
            str = str.replace(")","");
            //System.out.println(str);
        }
        String[] input = str.split(" ");
        //System.out.println("Inputs: "+Arrays.toString(input));
        //System.out.println("Variables: "+Arrays.toString(param));
        for(int i = 0; i < input.length; i++){
            String temp = param[i].substring(0, param[i].length()-1);
            HLang.compile(temp + input[i] + ".", varList, valStack, functionList);
        }
        for(int i = 0; i < commands.size(); i++){
            HLang.compile(commands.get(i), varList,  valStack, functionList);
            //System.out.println("Executed: "+commands.get(i));
        }
    }
}
