import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class HLangFunct {
    private String[] param;
    private LinkedList<String> commands;
    
    public HLangFunct(String[] param, String command){
        this.param = param;
        String[] cmd = command.split(",");
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
        String str = paramList.substring(1, paramList.length()-1);
        String[] input = str.split(" ");
        for(int i = 0; i < input.length; i++){
            param[i] = param[i].substring(0, param[i].length()-1);
            //System.out.println(param[i] + input[i] + ".");
            HLang.compile(param[i] + input[i] + ".", varList, valStack, functionList);
        }
        
        for(int i = 0; i < commands.size(); i++){
            HLang.compile(commands.get(i), varList,  valStack, functionList);
        }
    }
}
