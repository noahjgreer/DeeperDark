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
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.GuardianEntityRenderer
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.GuardianEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.GuardianEntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.GuardianEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.GuardianEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.GuardianEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GuardianEntityRenderer
extends MobEntityRenderer<GuardianEntity, GuardianEntityRenderState, GuardianEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/guardian.png");
    private static final Identifier EXPLOSION_BEAM_TEXTURE = Identifier.ofVanilla((String)"textures/entity/guardian_beam.png");
    private static final RenderLayer LAYER = RenderLayers.entityCutoutNoCull((Identifier)EXPLOSION_BEAM_TEXTURE);

    public GuardianEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 0.5f, EntityModelLayers.GUARDIAN);
    }

    protected GuardianEntityRenderer(EntityRendererFactory.Context ctx, float shadowRadius, EntityModelLayer layer) {
        super(ctx, (EntityModel)new GuardianEntityModel(ctx.getPart(layer)), shadowRadius);
    }

    public boolean shouldRender(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
        LivingEntity livingEntity;
        if (super.shouldRender((Entity)guardianEntity, frustum, d, e, f)) {
            return true;
        }
        if (guardianEntity.hasBeamTarget() && (livingEntity = guardianEntity.getBeamTarget()) != null) {
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0f);
            Vec3d vec3d2 = this.fromLerpedPosition((LivingEntity)guardianEntity, (double)guardianEntity.getStandingEyeHeight(), 1.0f);
            return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
        }
        return false;
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = MathHelper.lerp((double)delta, (double)entity.lastRenderX, (double)entity.getX());
        double e = MathHelper.lerp((double)delta, (double)entity.lastRenderY, (double)entity.getY()) + yOffset;
        double f = MathHelper.lerp((double)delta, (double)entity.lastRenderZ, (double)entity.getZ());
        return new Vec3d(d, e, f);
    }

    public void render(GuardianEntityRenderState guardianEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        super.render((LivingEntityRenderState)guardianEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        Vec3d vec3d = guardianEntityRenderState.beamTargetPos;
        if (vec3d != null) {
            float f = guardianEntityRenderState.beamTicks * 0.5f % 1.0f;
            matrixStack.push();
            matrixStack.translate(0.0f, guardianEntityRenderState.standingEyeHeight, 0.0f);
            GuardianEntityRenderer.renderBeam((MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (Vec3d)vec3d.subtract(guardianEntityRenderState.cameraPosVec), (float)guardianEntityRenderState.beamTicks, (float)guardianEntityRenderState.beamProgress, (float)f);
            matrixStack.pop();
        }
    }

    private static void renderBeam(MatrixStack matrices, OrderedRenderCommandQueue queue, Vec3d offset, float beamTicks, float beamProgress, float f) {
        float g = (float)(offset.length() + 1.0);
        offset = offset.normalize();
        float h = (float)Math.acos(offset.y);
        float i = 1.5707964f - (float)Math.atan2(offset.z, offset.x);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(i * 57.295776f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(h * 57.295776f));
        float j = beamTicks * 0.05f * -1.5f;
        float k = beamProgress * beamProgress;
        int l = 64 + (int)(k * 191.0f);
        int m = 32 + (int)(k * 191.0f);
        int n = 128 - (int)(k * 64.0f);
        float o = 0.2f;
        float p = 0.282f;
        float q = MathHelper.cos((double)(j + 2.3561945f)) * 0.282f;
        float r = MathHelper.sin((double)(j + 2.3561945f)) * 0.282f;
        float s = MathHelper.cos((double)(j + 0.7853982f)) * 0.282f;
        float t = MathHelper.sin((double)(j + 0.7853982f)) * 0.282f;
        float u = MathHelper.cos((double)(j + 3.926991f)) * 0.282f;
        float v = MathHelper.sin((double)(j + 3.926991f)) * 0.282f;
        float w = MathHelper.cos((double)(j + 5.4977875f)) * 0.282f;
        float x = MathHelper.sin((double)(j + 5.4977875f)) * 0.282f;
        float y = MathHelper.cos((double)(j + (float)Math.PI)) * 0.2f;
        float z = MathHelper.sin((double)(j + (float)Math.PI)) * 0.2f;
        float aa = MathHelper.cos((double)(j + 0.0f)) * 0.2f;
        float ab = MathHelper.sin((double)(j + 0.0f)) * 0.2f;
        float ac = MathHelper.cos((double)(j + 1.5707964f)) * 0.2f;
        float ad = MathHelper.sin((double)(j + 1.5707964f)) * 0.2f;
        float ae = MathHelper.cos((double)(j + 4.712389f)) * 0.2f;
        float af = MathHelper.sin((double)(j + 4.712389f)) * 0.2f;
        float ag = g;
        float ah = 0.0f;
        float ai = 0.4999f;
        float aj = -1.0f + f;
        float ak = aj + g * 2.5f;
        queue.submitCustom(matrices, LAYER, (matricesEntry, vertexConsumer) -> {
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)y, (float)ag, (float)z, (int)l, (int)m, (int)n, (float)0.4999f, (float)ak);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)y, (float)0.0f, (float)z, (int)l, (int)m, (int)n, (float)0.4999f, (float)aj);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)aa, (float)0.0f, (float)ab, (int)l, (int)m, (int)n, (float)0.0f, (float)aj);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)aa, (float)ag, (float)ab, (int)l, (int)m, (int)n, (float)0.0f, (float)ak);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)ac, (float)ag, (float)ad, (int)l, (int)m, (int)n, (float)0.4999f, (float)ak);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)ac, (float)0.0f, (float)ad, (int)l, (int)m, (int)n, (float)0.4999f, (float)aj);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)ae, (float)0.0f, (float)af, (int)l, (int)m, (int)n, (float)0.0f, (float)aj);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)ae, (float)ag, (float)af, (int)l, (int)m, (int)n, (float)0.0f, (float)ak);
            float ac = MathHelper.floor((float)beamTicks) % 2 == 0 ? 0.5f : 0.0f;
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)q, (float)ag, (float)r, (int)l, (int)m, (int)n, (float)0.5f, (float)(ac + 0.5f));
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)s, (float)ag, (float)t, (int)l, (int)m, (int)n, (float)1.0f, (float)(ac + 0.5f));
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)w, (float)ag, (float)x, (int)l, (int)m, (int)n, (float)1.0f, (float)ac);
            GuardianEntityRenderer.vertex((VertexConsumer)vertexConsumer, (MatrixStack.Entry)matricesEntry, (float)u, (float)ag, (float)v, (int)l, (int)m, (int)n, (float)0.5f, (float)ac);
        });
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
        vertexConsumer.vertex(matrix, x, y, z).color(red, green, blue, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
        return TEXTURE;
    }

    public GuardianEntityRenderState createRenderState() {
        return new GuardianEntityRenderState();
    }

    public void updateRenderState(GuardianEntity guardianEntity, GuardianEntityRenderState guardianEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)guardianEntity, (LivingEntityRenderState)guardianEntityRenderState, f);
        guardianEntityRenderState.spikesExtension = guardianEntity.getSpikesExtension(f);
        guardianEntityRenderState.tailAngle = guardianEntity.getTailAngle(f);
        guardianEntityRenderState.cameraPosVec = guardianEntity.getCameraPosVec(f);
        Entity entity = GuardianEntityRenderer.getBeamTarget((GuardianEntity)guardianEntity);
        if (entity != null) {
            guardianEntityRenderState.rotationVec = guardianEntity.getRotationVec(f);
            guardianEntityRenderState.lookAtPos = entity.getCameraPosVec(f);
        } else {
            guardianEntityRenderState.rotationVec = null;
            guardianEntityRenderState.lookAtPos = null;
        }
        LivingEntity livingEntity = guardianEntity.getBeamTarget();
        if (livingEntity != null) {
            guardianEntityRenderState.beamProgress = guardianEntity.getBeamProgress(f);
            guardianEntityRenderState.beamTicks = guardianEntity.getBeamTicks() + f;
            guardianEntityRenderState.beamTargetPos = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, f);
        } else {
            guardianEntityRenderState.beamTargetPos = null;
        }
    }

    private static @Nullable Entity getBeamTarget(GuardianEntity guardian) {
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (guardian.hasBeamTarget()) {
            return guardian.getBeamTarget();
        }
        return entity;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GuardianEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

