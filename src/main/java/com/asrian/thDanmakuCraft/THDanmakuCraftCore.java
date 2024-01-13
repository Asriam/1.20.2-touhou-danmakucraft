package com.asrian.thDanmakuCraft;

import com.asrian.thDanmakuCraft.init.*;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(THDanmakuCraftCore.MODID)
public class THDanmakuCraftCore
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "thdanmakucraft";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String ENGINE_NAME = new ResourceLocation(MODID,"js").toString();
    private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static ScriptEngine scriptEngine;

    public static final ScriptEngine getEngine(){
        return scriptEngine;
    };


    public THDanmakuCraftCore()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        scriptEngineManager.registerEngineName(ENGINE_NAME,new NashornScriptEngineFactory());
        scriptEngine = scriptEngineManager.getEngineByName(ENGINE_NAME);
        BlockInit.BLOCKS.register(modEventBus);
        ItemInit.ITEMS.register(modEventBus);
        EntityInit.ENTITIES.register(modEventBus);
        CreativeModeTabInit.CREATIVE_MODE_TABS.register(modEventBus);
        THObjectInit.TH_OBJECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }
}
