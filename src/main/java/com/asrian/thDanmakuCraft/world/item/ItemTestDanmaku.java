package com.asrian.thDanmakuCraft.world.item;

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

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand p_43144_) {
        ItemStack itemstack = player.getItemInHand(p_43144_);

        /*
        level.playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            Snowball snowball = new Snowball(level, player);
            snowball.setItem(itemstack);
            snowball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(snowball);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }*/

        /*
        Vec3 pos = player.position();
        for(int i=1;i<=36;i++){
            EntityExample entity = new EntityExample(level, new Vec3(
                    pos.x+10.0f * Mth.cos((2*Mth.PI)/36*i),
                    pos.y,
                    pos.z+10.0f * Mth.sin((2*Mth.PI)/36*i)
                )
            );
            level.addFreshEntity(entity);
        }*/

        EntityTHObjectContainer container = new EntityTHObjectContainer(player,level,player.position());
        /*
        if(true) {
            Vec3 pos = player.position();
            for (int i=0;i<8;i++) {

                Vec3 angle = new Vec3(0.1f, 0.0f, 0.0f).yRot(Mth.DEG_TO_RAD*((float) Math.pow(container.tickCount*0.4f,2)+360/5*i)).zRot(Mth.DEG_TO_RAD*90);
                THObject danmaku = THDanmakuCraftRegistryies.THOBJECT_TYPE
                        .getValue(THObjectInit.OBJECT_0.get().getKey())
                        .create(container)
                        .init(
                                pos,
                                0.2f,
                                angle
                        );
                danmaku.setAcceleration(0.01f,angle);

                objectList.add(danmaku);
                THDanmakuCraftCore.LOGGER.info("ADD"+danmaku);
                danmaku.lifetime = 60;
            }
        }*/

        level.addFreshEntity(container);

        //level.addFreshEntity(new EntityExample(level,player.position()));
        //container.addTHObject(new THDanmaku(container,player.position()));

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }
}
