import com.asrian.thDanmakuCraft.THDanmakuCraftCore;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
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

        System.out.print("aaaaaaaaaaaaaa\n");

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

        //System.out.print(new ResourceLocation(THDanmakuCraftCore.MODID,"scripts/lua/test.lua"));
        System.out.print("\n");
        System.out.print(Math.PI);
        System.out.print("\n");
        System.out .print(Mth.PI);
        System.out.print("\n");
        System.out.print(Mth.sin(1.0f));
        System.out.print("\n");
        System.out.print(Math.sin(1.0f));

        engine.put("asd.asd", test.class);
        System.out.print(engine.get("asd.asd"));

        Globals globals = JsePlatform.standardGlobals();
        LuaValue chunk = globals.loadfile("assets/"+ THDanmakuCraftCore.MODID+"/lua/test.lua");
        chunk.call();
        globals.get("fff").invoke();
    }
}
