import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class test {

    static ScriptEngineManager manager = new ScriptEngineManager();
    public static void main(String [] args) throws Exception {
        manager.registerEngineName("test",new NashornScriptEngineFactory());
        ScriptEngine engine = manager.getEngineByName("js");
        String script1 = "function main(){ print('helloooooooooooooo');}";
        System.out.print(engine);
        engine.eval(script1);

        Invocable ivn = (Invocable) engine;

        ((Invocable) engine).invokeFunction("main");
    }
}
