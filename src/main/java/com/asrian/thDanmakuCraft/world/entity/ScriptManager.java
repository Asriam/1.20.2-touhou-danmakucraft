package com.asrian.thDanmakuCraft.world.entity;

import com.asrian.thDanmakuCraft.THDanmakuCraftCore;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptManager extends AbstractScriptManager{

    private final ScriptEngine engine = THDanmakuCraftCore.getEngine();

    public ScriptManager(){

    }

    public Object invokeScript(String functionName, Object... args) throws ScriptException, NoSuchMethodException {
        engine.eval(this.script);
        Invocable invocable = (Invocable) engine;
        Object result = invocable.invokeFunction(functionName,args);
        return result;
    }
}
