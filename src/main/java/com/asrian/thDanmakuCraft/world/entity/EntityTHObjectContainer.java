package com.asrian.thDanmakuCraft.world.entity;

import com.asrian.thDanmakuCraft.init.EntityInit;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THObject;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THObjectManager;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THTask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.apache.commons.compress.utils.Lists;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class EntityTHObjectContainer extends Entity implements IEntityAdditionalSpawnData, IScript {

    private @Nullable Entity user;
    private @Nullable Entity target;
    private @Nullable UUID userUUID;
    private @Nullable UUID targetUUID;

    private final ScriptManager scriptManager;
    private final THObjectManager objectManager;
    private int timer = 0;
    public AABB aabb = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    public AABB bound = new AABB(-60.0D,-60.0D,-60.0D,60.0D,60.0D,60.0D);
    public boolean positionBinding = false;
    private int maxObjectAmount = 1000;
    private boolean autoRemove = true;
    private int autoRemoveLife = 60;
    public final THTask task = new THTask();

    public EntityTHObjectContainer(EntityType<? extends EntityTHObjectContainer> type, Level level) {
        super(type, level);
        this.objectManager = new THObjectManager(this);
        this.scriptManager = new ScriptManager();
        this.noCulling = true;
    }

    public EntityTHObjectContainer(@Nullable LivingEntity user, Level level, Vec3 pos) {
        this(EntityInit.ENTITY_THDANMAKU_CONTAINER.get(), level);
        this.user = user;
        this.setPos(pos);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        /*
        if(this.user == null && this.userUUID != null){
            THDanmakuCraftCore.LOGGER.info("fffffffffffffffffffffffffffffffff load user " + ((ServerLevel) this.level()).getEntity(this.userUUID) + " " + this.userUUID);
            this.setUser(((ServerLevel) this.level()).getPlayerByUUID(this.userUUID));
        }*/
    }

    public int getMaxObjectAmount() {
        return maxObjectAmount;
    }

    public void setMaxObjectAmount(int maxObjectAmount) {
        this.maxObjectAmount = maxObjectAmount;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void loadUserAndTarget(){
        if(this.user == null && this.userUUID != null) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            this.user = serverLevel.getEntity(this.userUUID);
        }

        if(this.target == null && this.targetUUID != null) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            this.target = serverLevel.getEntity(this.targetUUID);
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.task.tick();
        if(this.positionBinding && this.user != null){
            this.setPos(this.user.position());
        }
        this.setBound(this.position(),this.bound);
        this.loadUserAndTarget();
        /*
        if(this.objectManager.isEmpty() && false) {
            for (int j = 0; j< THBullet.BULLET_STYLE.class.getEnumConstants().length; j++) {
                for (int i = 0; i < 16; i++) {
                    THBullet a = (THBullet) new THBullet(this,THBullet.BULLET_STYLE.getStyleByIndex(j),THBullet.BULLET_COLOR.getColorByIndex(i + 1))
                    .initPosition(this.position().add(i, 0.0d, j*2))
                    .shoot(
                            0.0f,
                            Vec3.ZERO
                    );
                    a.setLifetime(3600);
                    a.colli = true;
                    a._blend = THRenderType.BLEND.NONE;
                }
            }
        }

        if((this.timer+2)%1==0) {
            Vec3 pos = this.position();
            Vec3 rotation = Vec3.directionFromRotation(0.0f,0.0f);
            Vec2 rotate = new Vec2(Mth.DEG_TO_RAD*((float) Math.pow(this.timer*0.1f,2)+360.0f/5),-Mth.DEG_TO_RAD*((float) Math.pow(this.timer*0.1f,2)+360.0f/5));

            Vec3 angle = rotation.xRot(Mth.DEG_TO_RAD*90.0f).normalize().xRot(rotate.x).yRot(rotate.y);
            THBullet.BULLET_STYLE style = THBullet.BULLET_STYLE.grain_a;
            THBullet danmaku = (THBullet) new THBullet(this,style,THBullet.BULLET_COLOR.COLOR_PURPLE).initPosition(pos).shoot(
                    0.2f,
                    angle
            );

            danmaku.setAcceleration(0.02f,angle);
            danmaku.setLifetime(120);

            int way = 4;
            for(int i=1;i<=2;i++){
                Vec3 angle2 = rotation.xRot(Mth.DEG_TO_RAD*90.0f-Mth.DEG_TO_RAD*60.0f*i).yRot(Mth.DEG_TO_RAD*(180.0f/way)*i);
                for(int j=0;j<way;j++) {
                    Vec3 angle3 = angle2.yRot(-Mth.DEG_TO_RAD * (360.0f/way)*j).normalize().xRot(rotate.x).yRot(rotate.y);
                    THBullet danmaku2 = (THBullet) new THBullet(this,style,
                            THBullet.BULLET_COLOR.COLOR_PURPLE).initPosition(pos).shoot(
                            0.2f,
                            angle3
                    );
                    danmaku2.setAcceleration(0.02f, angle3);
                    danmaku2.setLifetime(120);
                }
            }

            Vec3 angle3 = rotation.xRot(Mth.DEG_TO_RAD*90.0f- Mth.DEG_TO_RAD * 180.0f).normalize().xRot(rotate.x).yRot(rotate.y);
            THBullet danmaku3 = (THBullet) new THBullet(this,style,
                    THBullet.BULLET_COLOR.COLOR_PURPLE).initPosition(pos).shoot(
                    0.2f,
                    angle3
            );
            danmaku3.setAcceleration(0.02f, angle3);
            danmaku3.setLifetime(120);
        }*/

        this.updateObjects();
        this.timer++;


        if(this.autoRemove){
            if(this.objectManager.isEmpty() && --this.autoRemoveLife < 0){
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    public void updateObjects(){
        if(this.objectManager.isEmpty()){
            return;
        }

        List<THObject> removeList = Lists.newArrayList();
        for (THObject object: objectManager.getTHObjects()){
            if (object != null && !object.removeFlag){
                object.onTick();
            }else {
                removeList.add(object);
            }
        }
        for(THObject object:removeList){
            object.onRemove();
            this.objectManager.removeTHObject(object);
            //object.remove();
        }
    }

    public final void setBound(AABB boundingBox) {
        this.aabb = boundingBox;
    }

    public final void setBound(Vec3 pos, Vec3 size) {
        setBound(new AABB(
                pos.x - size.x / 2, pos.y - size.y / 2, pos.z - size.z / 2,
                pos.x + size.x / 2, pos.y + size.y / 2, pos.z + size.z / 2
        ));
    }

    public final void setBound(Vec3 pos, AABB aabb) {
        setBound(new AABB(
                pos.x + aabb.minX, pos.y + aabb.minY, pos.z + aabb.minZ,
                pos.x + aabb.maxX, pos.y + aabb.maxY, pos.z + aabb.maxZ
        ));
    }

    public final AABB getAabb(){
        return this.aabb;
    }

    public THObjectManager getObjectManager(){
        return this.objectManager;
    }

    public void setUser(@Nullable Entity entity){
        this.user = entity;
        this.userUUID = entity != null ? entity.getUUID() : null;
    }

    @Nullable
    public Entity getUser(){
        return this.user;
    }

    public void setTarget(@Nullable Entity target) {
        this.target = target;
        this.userUUID = target != null ? target.getUUID() : null;
    }

    @Nullable
    public Entity getTarget() {
        return target;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.user != null ? this.user.getId() : 0);
        buffer.writeVarInt(this.target != null ? this.target.getId() : 0);
        buffer.writeInt(this.maxObjectAmount);
        buffer.writeInt(this.timer);
        buffer.writeBoolean(this.positionBinding);
        this.objectManager.writeData(buffer);
        this.scriptManager.writeData(buffer);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        Entity user = this.level().getEntity(additionalData.readVarInt());
        this.setUser(user);
        Entity target = this.level().getEntity(additionalData.readVarInt());
        this.setTarget(target);
        this.maxObjectAmount = additionalData.readInt();
        this.timer = additionalData.readInt();
        this.positionBinding = additionalData.readBoolean();
        this.objectManager.readData(additionalData);
        this.scriptManager.readData(additionalData);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Timer",this.timer);
        compoundTag.putInt("MaxObjectAmount",this.maxObjectAmount);
        compoundTag.putBoolean("PositionBinding",this.positionBinding);
        compoundTag.put("object_storage", this.objectManager.save());
        compoundTag.put("script",this.scriptManager.save(new CompoundTag()));
        String user = this.user != null ? this.user.getUUID().toString() : "";
        String target = this.target != null ? this.target.getUUID().toString() : "";
        compoundTag.putString("UserUUID",user);
        compoundTag.putString("TargetUUID",target);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.timer = compoundTag.getInt("Timer");
        this.maxObjectAmount = compoundTag.getInt("MaxObjectAmount");
        this.positionBinding = compoundTag.getBoolean("PositionBinding");
        this.objectManager.load(compoundTag.getCompound("object_storage"));
        this.scriptManager.load(compoundTag.getCompound("script"));
        String userUUID = compoundTag.getString("UserUUID");
        String targetUUID = compoundTag.getString("TargetUUID");
        this.userUUID = !userUUID.equals("") ? UUID.fromString(userUUID) : null;
        this.targetUUID = !targetUUID.equals("") ? UUID.fromString(targetUUID) : null;
    }

    @Override
    public ScriptManager getScriptManager() {
        return this.scriptManager;
    }

    /*
    @Override
    public boolean isMultipartEntity() {
        return true;
    }*/
}
