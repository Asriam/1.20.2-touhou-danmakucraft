package com.asrian.thDanmakuCraft.world.entity.danmaku;

import com.asrian.thDanmakuCraft.THDanmakuCraftCore;
import com.asrian.thDanmakuCraft.client.renderer.THObjectRenderHelper;
import com.asrian.thDanmakuCraft.client.renderer.THRenderType;
import com.asrian.thDanmakuCraft.client.renderer.entity.EntityTHObjectContainerRenderer;
import com.asrian.thDanmakuCraft.init.THObjectInit;
import com.asrian.thDanmakuCraft.world.entity.EntityTHObjectContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.util.List;
import java.util.function.Predicate;

public class THCurvedLaser extends THObject {

    public THBullet.BULLET_COLOR laserColor;
    public final NodeManager nodeManager;
    public int nodeMount;
    public float width;
    public boolean shouldUpdateNodes = true;

    public THCurvedLaser(THObjectType<THCurvedLaser> type, EntityTHObjectContainer container) {
        super(type, container);
        this.nodeManager = new NodeManager(this);
        this.laserColor = THBullet.BULLET_COLOR.COLOR_BLUE;
    }

    public THCurvedLaser(EntityTHObjectContainer container,THBullet.BULLET_COLOR laserColor, int nodeMount, float width){
        this(THObjectInit.TH_CURVED_LASER.get(),container);
        this.laserColor = laserColor;
        this.nodeMount = nodeMount;
        this.nodeManager.initNodeList(nodeMount);
        this.width = width;
    };

    @Override
    public void writeData(FriendlyByteBuf buffer) {
        super.writeData(buffer);
        buffer.writeFloat(this.width);
        buffer.writeEnum(this.laserColor);
        this.nodeManager.writeData(buffer);
    }

    @Override
    public void readData(FriendlyByteBuf buffer){
        super.readData(buffer);
        this.width = buffer.readFloat();
        this.laserColor = buffer.readEnum(THBullet.BULLET_COLOR.class);
        this.nodeManager.readData(buffer);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag nbt = super.save(tag);
        tag.putFloat("Width",this.width);
        tag.putInt("LaserColor",this.laserColor.ordinal());
        this.nodeManager.save(tag);
        return nbt;
    }

    @Override
    public void load(CompoundTag tag){
        super.load(tag);
        this.width = tag.getFloat("Width");
        this.laserColor = THBullet.BULLET_COLOR.class.getEnumConstants()[tag.getInt("LaserColor")];
        this.nodeManager.load(tag);
    }

    @Override
    public void onTick(){
        super.onTick();
        if(this.shouldUpdateNodes) {
            this.nodeManager.updateNode(this.getPosition());
        }
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void onRender(EntityTHObjectContainerRenderer renderer, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedOverlay) {
        if (this.color.a <= 0) {
            return;
        }

        poseStack.pushPose();
        Vec3 camPos = renderer.dispatcher.camera.getPosition();
        //THBullet.BULLET_FACES_CULL cull = THBullet.BULLET_FACES_CULL.getCullType(this,camPos.x,camPos.y,camPos.z);
        int edge = 6;
        Color indexColor = this.laserColor.getColor();
        Color color = Color(
                this.color.r * indexColor.r/255,
                this.color.g * indexColor.g/255,
                this.color.b * indexColor.b/255,
                (int) (this.color.a * 0.6)
        );

        Color coreColor = this.color;
        renderCurvedLaser(renderer, bufferSource.getBuffer(THRenderType.LIGHTNING),poseStack,this.nodeManager.getNodes(),this.width,this.width*0.5f,edge, color, coreColor,partialTicks,combinedOverlay,1.0f,0.95f);
        poseStack.popPose();
    }

    @OnlyIn(value = Dist.CLIENT)
    public static void renderCurvedLaser(EntityTHObjectContainerRenderer renderer, VertexConsumer vertexConsumer, PoseStack poseStack, List<Node> nodeList, float width, float coreWidth, int edge, Color laserColor, Color coreColor, float partialTicks, int combinedOverlay, float laserLength, float coreLength) {
        //if(nodeList.isEmpty() || nodeList.size() < 3){
        //    return;
        //}

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        //Vec3 cameraPosition = renderer.dispatcher.camera.getPosition();
        final float perAngle = Mth.DEG_TO_RAD * 360.0f/edge;

        int index = 0;
        for(Node node:nodeList){
            if(index >= nodeList.size()-1){
                break;
            }

            Node node2 = nodeList.get(index+1);
            final Vec3 pos1 = node.getOffsetPosition(partialTicks);
            final Vec3 pos2 = node2.getOffsetPosition(partialTicks);
            boolean flag = index >= nodeList.size()-2;
            Node node3 = !flag ? nodeList.get(index+2) : null;
            Vec2 aaa1 = THObject.VectorAngleToEulerRadAngle(pos1.vectorTo(pos2));
            Vec2 aaa2 = node3 != null ? THObject.VectorAngleToEulerRadAngle(pos2.vectorTo(node3.getOffsetPosition(partialTicks))) : aaa1;
            Vec2 angle1 = new Vec2(aaa1.x-Mth.DEG_TO_RAD*90.0f,aaa1.y);
            Vec2 angle2 = new Vec2(aaa2.x-Mth.DEG_TO_RAD*90.0f,aaa2.y);

            if(shouldRenderNode(node,node2,renderer.frustum)){
                for (int i = 0; i < edge; i++) {
                    //laser render
                    float width1 = circle((float) (index)/(nodeList.size()-1),laserLength);
                    float width2 = circle((float) (index+1)/(nodeList.size()-1),laserLength);
                    Vec3 posA = new Vec3(width1*width,0.0d,0.0d);
                    Vec3 posB = new Vec3(!(index>=nodeList.size()-2)? width2*width : 0.0f,0.0d,0.0d);
                    Vec3 calculatedPos1_1 = posA.yRot(i*perAngle)    .xRot(angle1.x).yRot(angle1.y).add(pos1);
                    Vec3 calculatedPos1_2 = posA.yRot((i+1)*perAngle).xRot(angle1.x).yRot(angle1.y).add(pos1);

                    Vec3 calculatedPos2_1 = posB.yRot(i*perAngle)    .xRot(angle2.x).yRot(angle2.y).add(pos2);
                    Vec3 calculatedPos2_2 = posB.yRot((i+1)*perAngle).xRot(angle2.x).yRot(angle2.y).add(pos2);

                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos1_1.toVector3f(),new Vector2f(0.0f,0.0f),laserColor);
                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos2_1.toVector3f(),new Vector2f(0.0f,0.0f),laserColor);
                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos2_2.toVector3f(),new Vector2f(0.0f,0.0f),laserColor);
                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos1_2.toVector3f(),new Vector2f(0.0f,0.0f),laserColor);
                    //core render
                    float width3 = circle((float) (index)/(nodeList.size()-1),coreLength);
                    float width4 = circle((float) (index+1)/(nodeList.size()-1),coreLength);
                    Vec3 posA1 = new Vec3(width3*coreWidth,0.0d,0.0d);
                    Vec3 posB2 = new Vec3(!(index>=nodeList.size()-2)? width4*coreWidth : 0.0f,0.0d,0.0d);
                    Vec3 calculatedPos3_1 = posA1.yRot(i*perAngle)    .xRot(angle1.x).yRot(angle1.y).add(pos1);
                    Vec3 calculatedPos3_2 = posA1.yRot((i+1)*perAngle).xRot(angle1.x).yRot(angle1.y).add(pos1);

                    Vec3 calculatedPos4_1 = posB2.yRot(i*perAngle)    .xRot(angle2.x).yRot(angle2.y).add(pos2);
                    Vec3 calculatedPos4_2 = posB2.yRot((i+1)*perAngle).xRot(angle2.x).yRot(angle2.y).add(pos2);

                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos3_1.toVector3f(),new Vector2f(0.0f,0.0f),coreColor);
                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos4_1.toVector3f(),new Vector2f(0.0f,0.0f),coreColor);
                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos4_2.toVector3f(),new Vector2f(0.0f,0.0f),coreColor);
                    THObjectRenderHelper.vertex(vertexConsumer,pose,normal,combinedOverlay,calculatedPos3_2.toVector3f(),new Vector2f(0.0f,0.0f),coreColor);
                }
            }
            index += 1;
        }
    }

    public static boolean shouldRenderNode(Node node1, Node node2, Frustum frustum) {
        AABB aabb1 = node1.getBoundingBoxForCulling().inflate(0.5D);
        AABB aabb2 = node2.getBoundingBoxForCulling().inflate(0.5D);
        if (aabb1.hasNaN() || aabb1.getSize() == 0.0D) {
            aabb1 = new AABB(node1.position.x - 2.0D, node1.position.y - 2.0D, node1.position.z - 2.0D, node1.position.x + 2.0D, node1.position.y + 2.0D, node1.position.z + 2.0D);
        }
        if (aabb2.hasNaN() || aabb2.getSize() == 0.0D) {
            aabb2 = new AABB(node2.position.x - 2.0D, node2.position.y - 2.0D, node2.position.z - 2.0D, node2.position.x + 2.0D, node2.position.y + 2.0D, node2.position.z + 2.0D);
        }

        return frustum.isVisible(aabb1) && frustum.isVisible(aabb2);
    }

    static float circle(float x, float width){
        float num = (x/width*2-1/width);
        return Mth.sqrt(1-num*num);
    }

    public static class NodeManager{
        private final List<Node> nodeList;
        private final THCurvedLaser laser;

        public NodeManager(THCurvedLaser laser) {
            this.nodeList = Lists.newArrayList();
            this.laser = laser;
        }

        public NodeManager initNodeList(int nodeMount){
            for(int i=0;i<nodeMount;i++){
                this.nodeList.add(new Node(laser.getPosition()));
            }
            return this;
        }

        public void updateNode(Vec3 pos){
            if(this.nodeList.isEmpty()){
                return;
            }
            for (int index = 0; index < this.nodeList.size(); index++){
                Node node = this.nodeList.get(index);
                Node lastNode = index > 0 ? this.nodeList.get(index-1) : null;
                if(index == 0){
                    node.updateNode(pos);
                }else {
                    node.updateNode(lastNode.lastPosition);
                }
            }
        }

        public void updateNodePos(int index, Vec3 pos){
            this.getNode(index).updateNode(pos);
        }

        public void updateAllNodePos(List<Vec3> posList){
            if(this.nodeList.isEmpty()){
                return;
            }

            int index = 0;
            for(Vec3 pos:posList){
                Node node = this.getNode(index);
                if (node != null){
                    node.updateNode(pos);
                }
                index++;
            }
        }

        public void updateAllNode(List<Node> nodeList){
            this.nodeList.clear();
            this.nodeList.addAll(nodeList);
        }

        public void writeData(FriendlyByteBuf buffer){
            buffer.writeInt(this.nodeList.size());
            for(Node node:this.nodeList){
                node.writeData(buffer);
            }
        }

        public void readData(FriendlyByteBuf buffer){
            int size = buffer.readInt();
            List<Node> nodes = Lists.newArrayList();
            for(short i=0;i<size;i++){
                Node node = new Node(laser.getPosition());
                node.readData(buffer);
                nodes.add(node);
            }
            //this.updateAllNode(nodes);
            this.nodeList.addAll(nodes);
        }

        public CompoundTag save(CompoundTag tag){
            int index = 0;
            CompoundTag list = new CompoundTag();
            for(Node node:this.nodeList) {
                list.put("node_"+index,node.save(new CompoundTag()));
                index++;
            }
            tag.put("nodes",list);
            return tag;
        }

        public void load(CompoundTag tag){
            CompoundTag listTag = tag.getCompound("nodes");
            int list_size = listTag.getAllKeys().size();
            List<Node> nodes = Lists.newArrayList();
            for(int i=0;i<list_size;i++){
                Node node = new Node(laser.getPosition());
                node.load(tag.getCompound("node_"+i));
                nodes.add(node);
            }
            this.updateAllNode(nodes);
        }

        public void clear(){
            this.nodeList.clear();
        }

        public void addNode(Node node){
            this.nodeList.add(node);
        }

        public void removeNode(Node node){
            this.nodeList.remove(node);
        }

        public void removeNode(int index){
            this.nodeList.remove(index);
        }

        public void removeIf(Predicate filter){
            this.nodeList.removeIf(filter);
        }

        public List<Node> getNodes(){
            return this.nodeList;
        }

        public Node getNode(int index){
            if(index > this.nodeList.size()-1 || index < 0){
                return null;
            }

            return this.nodeList.get(index);
        }
    }

    public static class Node{
        private Vec3 position;
        private Vec3 lastPosition;
        private AABB bb = INITIAL_AABB;
        private Vec3 size = new Vec3(0.5f,0.5f,0.5f);

        public Node(Vec3 pos){
            this.lastPosition =  pos;
            this.position = pos;
        }

        public void updateNode(Vec3 position){
            this.lastPosition = this.position;
            this.position = position;
            this.setBoundingBox(this.position,this.size);
        }

        public void setPosition(Vec3 position){
            this.lastPosition = position;
            this.position = position;
            this.setBoundingBox(this.position,this.size);
        }

        public Vec3 getPosition(){
            return this.position;
        }

        public void setBoundingBox(AABB boundingBox) {
            this.bb = boundingBox;
        }

        public final AABB getBoundingBox() {
            return this.bb;
        }

        public AABB getBoundingBoxForCulling() {
            return this.getBoundingBox();
        }

        public void setBoundingBox(Vec3 pos, Vec3 size) {
            this.setBoundingBox(new AABB(
                    pos.x - size.x / 2, pos.y - size.y / 2, pos.z - size.z / 2,
                    pos.x + size.x / 2, pos.y + size.y / 2, pos.z + size.z / 2
            ));
        }

        public Vec3 getOffsetPosition(float partialTicks){
            double x = Mth.lerp(partialTicks, this.lastPosition.x, this.position.x);
            double y = Mth.lerp(partialTicks, this.lastPosition.y, this.position.y);
            double z = Mth.lerp(partialTicks, this.lastPosition.z, this.position.z);
            return new Vec3(x, y, z);
        }

        public void writeData(FriendlyByteBuf byteBuf){
            byteBuf.writeVec3(this.position);
        }

        public void readData(FriendlyByteBuf byteBuf){
            Vec3 pos = byteBuf.readVec3();
            this.setPosition(pos);
            THDanmakuCraftCore.LOGGER.info(""+pos);
        }

        public CompoundTag save(CompoundTag tag){
            tag.put("Pos",newDoubleList(this.position.x,this.position.y,this.position.z));
            return tag;
        }

        public void load(CompoundTag tag){
            ListTag posTag = tag.getList("Pos", Tag.TAG_DOUBLE);
            this.setPosition(new Vec3(posTag.getDouble(0),posTag.getDouble(1),posTag.getDouble(2)));
        }
    }
}
