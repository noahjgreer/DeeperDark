/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.FishingBobberEntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.FishingBobberEntityState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.FishingBobberEntity
 *  net.minecraft.item.FishingRodItem
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer<FishingBobberEntity, FishingBobberEntityState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER = RenderLayers.entityCutout((Identifier)TEXTURE);
    private static final double field_33632 = 960.0;

    public FishingBobberEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public boolean shouldRender(FishingBobberEntity fishingBobberEntity, Frustum frustum, double d, double e, double f) {
        return super.shouldRender((Entity)fishingBobberEntity, frustum, d, e, f) && fishingBobberEntity.getPlayerOwner() != null;
    }

    public void render(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)cameraRenderState.orientation);
        orderedRenderCommandQueue.submitCustom(matrixStack, LAYER, (matricesEntry, vertexConsumer) -> {
            FishingBobberEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)fishingBobberEntityState.light, (float)0.0f, (int)0, (int)0, (int)1);
            FishingBobberEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)fishingBobberEntityState.light, (float)1.0f, (int)0, (int)1, (int)1);
            FishingBobberEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)fishingBobberEntityState.light, (float)1.0f, (int)1, (int)1, (int)0);
            FishingBobberEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (int)fishingBobberEntityState.light, (float)0.0f, (int)1, (int)0, (int)0);
        });
        matrixStack.pop();
        float f = (float)fishingBobberEntityState.pos.x;
        float g = (float)fishingBobberEntityState.pos.y;
        float h = (float)fishingBobberEntityState.pos.z;
        float i = MinecraftClient.getInstance().getWindow().getMinimumLineWidth();
        orderedRenderCommandQueue.submitCustom(matrixStack, RenderLayers.lines(), (matricesEntry, vertexConsumer) -> {
            int j = 16;
            for (int k = 0; k < 16; ++k) {
                float l = FishingBobberEntityRenderer.percentage((int)k, (int)16);
                float m = FishingBobberEntityRenderer.percentage((int)(k + 1), (int)16);
                FishingBobberEntityRenderer.renderFishingLine((float)f, (float)g, (float)h, (VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)l, (float)m, (float)i);
                FishingBobberEntityRenderer.renderFishingLine((float)f, (float)g, (float)h, (VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)m, (float)l, (float)i);
            }
        });
        matrixStack.pop();
        super.render((EntityRenderState)fishingBobberEntityState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    public static Arm getArmHoldingRod(PlayerEntity player) {
        return player.getMainHandStack().getItem() instanceof FishingRodItem ? player.getMainArm() : player.getMainArm().getOpposite();
    }

    private Vec3d getHandPos(PlayerEntity player, float handRotation, float tickProgress) {
        int i;
        int n = i = FishingBobberEntityRenderer.getArmHoldingRod((PlayerEntity)player) == Arm.RIGHT ? 1 : -1;
        if (!this.dispatcher.gameOptions.getPerspective().isFirstPerson() || player != MinecraftClient.getInstance().player) {
            float f = MathHelper.lerp((float)tickProgress, (float)player.lastBodyYaw, (float)player.bodyYaw) * ((float)Math.PI / 180);
            double d = MathHelper.sin((double)f);
            double e = MathHelper.cos((double)f);
            float g = player.getScale();
            double h = (double)i * 0.35 * (double)g;
            double j = 0.8 * (double)g;
            float k = player.isInSneakingPose() ? -0.1875f : 0.0f;
            return player.getCameraPosVec(tickProgress).add(-e * h - d * j, (double)k - 0.45 * (double)g, -d * h + e * j);
        }
        double l = 960.0 / (double)((Integer)this.dispatcher.gameOptions.getFov().getValue()).intValue();
        Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)i * 0.525f, -0.1f).multiply(l).rotateY(handRotation * 0.5f).rotateX(-handRotation * 0.7f);
        return player.getCameraPosVec(tickProgress).add(vec3d);
    }

    private static float percentage(int value, int denominator) {
        return (float)value / (float)denominator;
    }

    private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(-1).texture((float)u, (float)v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd, float getMinimumLineWidth) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt((float)(i * i + j * j + k * k));
        buffer.vertex(matrices, f, g, h).color(-16777216).normal(matrices, i /= l, j /= l, k /= l).lineWidth(getMinimumLineWidth);
    }

    public FishingBobberEntityState createRenderState() {
        return new FishingBobberEntityState();
    }

    public void updateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityState fishingBobberEntityState, float f) {
        super.updateRenderState((Entity)fishingBobberEntity, (EntityRenderState)fishingBobberEntityState, f);
        PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
        if (playerEntity == null) {
            fishingBobberEntityState.pos = Vec3d.ZERO;
            return;
        }
        float g = playerEntity.getHandSwingProgress(f);
        float h = MathHelper.sin((double)(MathHelper.sqrt((float)g) * (float)Math.PI));
        Vec3d vec3d = this.getHandPos(playerEntity, h, f);
        Vec3d vec3d2 = fishingBobberEntity.getLerpedPos(f).add(0.0, 0.25, 0.0);
        fishingBobberEntityState.pos = vec3d.subtract(vec3d2);
    }

    protected boolean canBeCulled(FishingBobberEntity fishingBobberEntity) {
        return false;
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ boolean canBeCulled(Entity entity) {
        return this.canBeCulled((FishingBobberEntity)entity);
    }
}

