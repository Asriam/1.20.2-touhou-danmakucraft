package com.asrian.thDanmakuCraft.client.renderer.entity;

import com.asrian.thDanmakuCraft.world.entity.EntityTHObjectContainer;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THCurvedLaser;
import com.asrian.thDanmakuCraft.world.entity.danmaku.THObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class EntityTHObjectContainerRenderer extends EntityRenderer<EntityTHObjectContainer> {
    public final EntityRenderDispatcher dispatcher;
    public Frustum frustum;

    public EntityTHObjectContainerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = super.entityRenderDispatcher;
    }

    @Override
    public void render(EntityTHObjectContainer entity, float rotationX, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedOverlay){
        super.render(entity, rotationX, partialTicks, poseStack, bufferSource, combinedOverlay);
        renderContainerBound(entity, poseStack, bufferSource.getBuffer(RenderType.lines()));
        poseStack.popPose();
        poseStack.pushPose();
        Vec3 cameraPosition = this.dispatcher.camera.getPosition();
        double d2 = cameraPosition.x;
        double d3 = cameraPosition.y;
        double d0 = cameraPosition.z;
        poseStack.translate(-d2, -d3, -d0);

        for(THObject object:entity.getObjectManager().getTHObjects()){
            if (object != null && (object instanceof THCurvedLaser || this.shouldRenderTHObject(object, this.frustum, cameraPosition.x, cameraPosition.y, cameraPosition.z))) {
                poseStack.pushPose();
                object.onRender(this, partialTicks, poseStack, bufferSource, combinedOverlay);
                if (this.dispatcher.shouldRenderHitBoxes() && object.colli) {
                    Vec3 offsetPos = object.getOffsetPosition(partialTicks);
                    if(object instanceof THCurvedLaser){
                        renderTHCurvedLaserHitBoxes((THCurvedLaser) object, poseStack, bufferSource.getBuffer(RenderType.lines()),partialTicks,this.frustum,cameraPosition);
                        //renderTHObjectsHitBox(object, poseStack, bufferSource.getBuffer(RenderType.lines()));
                    }else {
                        poseStack.translate(offsetPos.x(), offsetPos.y(), offsetPos.z());
                        renderTHObjectsHitBox(object, poseStack, bufferSource.getBuffer(RenderType.lines()));
                    }
                }
                poseStack.popPose();
            }
        }
    }

    private static void renderTHObjectsHitBox(THObject object, PoseStack poseStack, VertexConsumer vertexConsumer) {
        AABB aabb = object.getBoundingBox().move(-object.getX(), -object.getY(), -object.getZ());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb, 0.0F, 1.0F, 1.0F, 1.0F);

        Vector3f rotation = object.getRotation();
        Vec3 vec31 = Vec3.directionFromRotation(rotation.x()*Mth.RAD_TO_DEG,rotation.y()*Mth.RAD_TO_DEG);
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float)vec31.x, (float)vec31.y, (float)vec31.z).endVertex();
        vertexConsumer.vertex(matrix4f, (float)(vec31.x * 2.0D), (float)((vec31.y * 2.0D)), (float)(vec31.z * 2.0D)).color(0, 0, 255, 255).normal(matrix3f, (float)vec31.x, (float)vec31.y, (float)vec31.z).endVertex();
    }

    private static void renderTHCurvedLaserHitBoxes(THCurvedLaser laser, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, Frustum frustum, Vec3 cameraPosition){
        List<THCurvedLaser.Node> nodes = laser.nodeManager.getNodes();
        for(THCurvedLaser.Node node: nodes){
            Vec3 pos = node.getPosition();
            AABB aabb = node.getBoundingBoxForCulling().inflate(0.5D);
            if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
                aabb = new AABB(pos.x() - 2.0D, pos.y() - 2.0D, pos.z() - 2.0D, pos.x() + 2.0D, pos.y() + 2.0D, pos.z() + 2.0D);
            }

            if (frustum.isVisible(aabb)) {
                poseStack.pushPose();
                AABB aabb2 = node.getBoundingBox().move(-pos.x(), -pos.y(), -pos.z());
                Vec3 offsetPos = node.getOffsetPosition(partialTicks);
                poseStack.translate(offsetPos.x(), offsetPos.y(), offsetPos.z());
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb2, 0.0F, 1.0F, 1.0F, 1.0F);
                poseStack.popPose();
            }
        }
    }

    private static void renderContainerBound(EntityTHObjectContainer entity, PoseStack poseStack, VertexConsumer vertexConsumer) {
        AABB aabb = entity.getAabb().move(-entity.getX(), -entity.getY(), -entity.getZ());
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, aabb, 0.0F, 0.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean shouldRender(EntityTHObjectContainer entity, Frustum frustum, double camX, double camY, double camZ) {
        this.frustum = frustum;
        return true;
    }

    public boolean shouldRenderTHObject(THObject object, Frustum frustum, double camX, double camY, double camZ) {
        if (!object.shouldRender(camX,camY,camZ)) {
            return false;
        } else if (object.noCulling) {
            return true;
        } else {
            AABB aabb = object.getBoundingBoxForCulling().inflate(0.5D);
            if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
                aabb = new AABB(object.getX() - 2.0D, object.getY() - 2.0D, object.getZ() - 2.0D, object.getX() + 2.0D, object.getY() + 2.0D, object.getZ() + 2.0D);
            }

            return frustum.isVisible(aabb);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityTHObjectContainer p_114482_) {
        return null;
    }
}
