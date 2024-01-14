package com.asrian.thDanmakuCraft.world.item;

import com.asrian.thDanmakuCraft.THDanmakuCraftCore;
import com.asrian.thDanmakuCraft.world.entity.EntityTHObjectContainer;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THBullet;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THCurvedLaser;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
        /**
        for (int j = 0; j< THBullet.BULLET_STYLE.class.getEnumConstants().length; j++) {
            for (int i = 0; i < 16; i++) {
                THBullet a = (THBullet) new THBullet(container,THBullet.BULLET_STYLE.getStyleByIndex(j),THBullet.BULLET_COLOR.getColorByIndex(i + 1))
                        .initPosition(container.position().add(i, 0.0d, j*2))
                        .shoot(
                                0.0f,
                                Vec3.ZERO
                        );
                a.setLifetime(3600);
                a.colli = true;
                a._blend = THRenderType.BLEND.NONE;
            }
        }*/
        for(int i=0;i<8;i++) {
            THCurvedLaser laser = (THCurvedLaser) new THCurvedLaser(container, THBullet.BULLET_COLOR.COLOR_DEEP_PURPLE, 180, 0.5f).initPosition(container.position()).shoot(new Vec3(0.0f, 0, 0));
            laser.setLifetime(1200);
            laser.injectScript(
                    "var Mth = Java.type('net.minecraft.util.Mth');" +
                            "var Vec2 = Java.type('net.minecraft.world.phys.Vec2');" +
                            "function onTick(object){" +
                            "   object.setVelocity(0.2,new Vec2(0.0," + i + "*360/8+60*Mth.cos(object.getTimer()*0.3)),true,true);" +
                            "}");
        }
        level.addFreshEntity(container);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
