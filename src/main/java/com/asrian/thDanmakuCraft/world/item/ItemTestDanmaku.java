package com.asrian.thDanmakuCraft.world.item;

import com.asrian.thDanmakuCraft.THDanmakuCraftCore;
import com.asrian.thDanmakuCraft.world.entity.EntityTHObjectContainer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemTestDanmaku extends Item {
    public ItemTestDanmaku(Properties properties) {
        super(properties);
        this.getDescriptionId();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand p_43144_) {
        ItemStack itemstack = player.getItemInHand(p_43144_);
        EntityTHObjectContainer container = new EntityTHObjectContainer(player,level,player.position());
        container.task.add(()->{
            THDanmakuCraftCore.LOGGER.info("fffffffffffff");

        },10);

        /*
        for(int i=0;i<1;i++) {
            THCurvedLaser laser = (THCurvedLaser) new THCurvedLaser(container, THBullet.BULLET_COLOR.COLOR_DEEP_PURPLE, 180, 0.5f).initPosition(container.position()).shoot(new Vec3(0.0f, 0, 0));
            laser.setLifetime(1200);
            laser.injectScript(
                    "var Mth = Java.type('net.minecraft.util.Mth');" +
                    "var Vec2 = Java.type('net.minecraft.world.phys.Vec2');" +
                    "var Vec3 = Java.type('net.minecraft.world.phys.Vec3');" +
                    "var THObject = Java.type('com.asrian.thDanmakuCraft.world.entity.danmaku.THObject');" +
                    "var THBullet = Java.type('com.asrian.thDanmakuCraft.world.entity.danmaku.THBullet');" +
                    "var THCurvedLaser = Java.type('com.asrian.thDanmakuCraft.world.entity.danmaku.THCurvedLaser');" +
                    "function onTick(object){" +
                    "   object.setVelocity(0.2,new Vec2(0.0," + i + "*360/16+60*Mth.cos(object.getTimer()*0.3)),true,true);" +
                    //"   new THCurvedLaser(object.getContainer(), THBullet.BULLET_COLOR.COLOR_DEEP_PURPLE, 3, 2.0).initPosition(object.getContainer().position()).shoot(new Vec3(0.0, 0, 0)).setVelocity(0.2,new Vec2(90,0.0),true,true);" +
                    "}");
        }*/

        level.addFreshEntity(container);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
