import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

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

        /*
        int[] intArray = {1,2,3,4,5,6,7};
        intArray[1] = 0;

         */

        List<Integer> intArray = Lists.newArrayList();
        intArray.add(null);
        intArray.add(1,1);

        System.out.print(intArray);

        Vec3 calculatedPos_1[] = new Vec3[12];
        System.out.print(calculatedPos_1[1]);
        calculatedPos_1[11] = new Vec3(0.0d,0.0d,0.0d);
    }
}
