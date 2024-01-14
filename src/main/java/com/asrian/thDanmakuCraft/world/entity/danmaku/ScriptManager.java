package com.asrian.thDanmakuCraft.world.entity.danmaku;

import com.asrian.thDanmakuCraft.THDanmakuCraftCore;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.script.Invocable;
import javax.script.ScriptEngine;

public class ScriptManager{
    private String script = "";
    private boolean shouldExecuteScript;
    private final ScriptEngine engine = THDanmakuCraftCore.getEngine();

    public ScriptManager(){

    }

    public boolean hasScript(){
        return this.script != null && !this.script.equals("");
    }

    public void setScript(String script){
        this.script = script;
    }

    public void setShouldExecuteScript(boolean shouldExecuteScript) {
        this.shouldExecuteScript = shouldExecuteScript;
    }

    public void enableScript(){
        this.shouldExecuteScript = true;
    }

    public void disableScript(){
        this.shouldExecuteScript = false;
    }

    public ScriptEngine invokeScript(String functionName, Object... args) throws Exception{
        if(!this.shouldExecuteScript && !this.hasScript()){
            return null;
        }
        engine.eval(this.script);
        Invocable invocable = (Invocable) engine;
        invocable.invokeFunction(functionName,args);
        return engine;
    }

    public void writeData(FriendlyByteBuf buffer){
        buffer.writeBoolean(this.shouldExecuteScript);
        CompoundTag tag = new CompoundTag();
        tag.putString("script",this.script);
        buffer.writeNbt(tag);
    }

    public void readData(FriendlyByteBuf buffer){
        this.shouldExecuteScript = buffer.readBoolean();
        this.script = buffer.readNbt().getString("script");
    }

    public CompoundTag save(CompoundTag tag){
        tag.putBoolean("ShouldExecuteScript",this.shouldExecuteScript);
        tag.putString("Script",this.script);
        return tag;
    }

    public void load(CompoundTag tag){
        this.shouldExecuteScript = tag.getBoolean("ShouldExecuteScript");
        this.script = tag.getString("Script");
    }
}
