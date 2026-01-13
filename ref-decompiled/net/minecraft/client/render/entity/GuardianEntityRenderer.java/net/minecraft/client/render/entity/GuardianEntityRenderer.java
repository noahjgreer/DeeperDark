/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

@Environment(value=EnvType.CLIENT)
public class GuardianEntityRenderer
extends MobEntityRenderer<GuardianEntity, GuardianEntityRenderState, GuardianEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/guardian.png");
    private static final Identifier EXPLOSION_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/guardian_beam.png");
    private static final RenderLayer LAYER = RenderLayers.entityCutoutNoCull(EXPLOSION_BEAM_TEXTURE);

    public GuardianEntityRenderer(EntityRendererFactory.Context context) {
        this(context, 0.5f, EntityModelLayers.GUARDIAN);
    }

    protected GuardianEntityRenderer(EntityRendererFactory.Context ctx, float shadowRadius, EntityModelLayer layer) {
        super(ctx, new GuardianEntityModel(ctx.getPart(layer)), shadowRadius);
    }

    @Override
    public boolean shouldRender(GuardianEntity guardianEntity, Frustum frustum, double d, double e, double f) {
        LivingEntity livingEntity;
        if (super.shouldRender(guardianEntity, frustum, d, e, f)) {
            return true;
        }
        if (guardianEntity.hasBeamTarget() && (livingEntity = guardianEntity.getBeamTarget()) != null) {
            Vec3d vec3d = this.fromLerpedPosition(livingEntity, (double)livingEntity.getHeight() * 0.5, 1.0f);
            Vec3d vec3d2 = this.fromLerpedPosition(guardianEntity, guardianEntity.getStandingEyeHeight(), 1.0f);
            return frustum.isVisible(new Box(vec3d2.x, vec3d2.y, vec3d2.z, vec3d.x, vec3d.y, vec3d.z));
        }
        return false;
    }

    private Vec3d fromLerpedPosition(LivingEntity entity, double yOffset, float delta) {
        double d = MathHelper.lerp((double)delta, entity.lastRenderX, entity.getX());
        double e = MathHelper.lerp((double)delta, entity.lastRenderY, entity.getY()) + yOffset;
        double f = MathHelper.lerp((double)delta, entity.lastRenderZ, entity.getZ());
        return new Vec3d(d, e, f);
    }

    @Override
    public void render(GuardianEntityRenderState guardianEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        super.render(guardianEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
        Vec3d vec3d = guardianEntityRenderState.beamTargetPos;
        if (vec3d != null) {
            float f = guardianEntityRenderState.beamTicks * 0.5f % 1.0f;
            matrixStack.push();
            matrixStack.translate(0.0f, guardianEntityRenderState.standingEyeHeight, 0.0f);
            GuardianEntityRenderer.renderBeam(matrixStack, orderedRenderCommandQueue, vec3d.subtract(guardianEntityRenderState.cameraPosVec), guardianEntityRenderState.beamTicks, guardianEntityRenderState.beamProgress, f);
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
        float q = MathHelper.cos(j + 2.3561945f) * 0.282f;
        float r = MathHelper.sin(j + 2.3561945f) * 0.282f;
        float s = MathHelper.cos(j + 0.7853982f) * 0.282f;
        float t = MathHelper.sin(j + 0.7853982f) * 0.282f;
        float u = MathHelper.cos(j + 3.926991f) * 0.282f;
        float v = MathHelper.sin(j + 3.926991f) * 0.282f;
        float w = MathHelper.cos(j + 5.4977875f) * 0.282f;
        float x = MathHelper.sin(j + 5.4977875f) * 0.282f;
        float y = MathHelper.cos(j + (float)Math.PI) * 0.2f;
        float z = MathHelper.sin(j + (float)Math.PI) * 0.2f;
        float aa = MathHelper.cos(j + 0.0f) * 0.2f;
        float ab = MathHelper.sin(j + 0.0f) * 0.2f;
        float ac = MathHelper.cos(j + 1.5707964f) * 0.2f;
        float ad = MathHelper.sin(j + 1.5707964f) * 0.2f;
        float ae = MathHelper.cos(j + 4.712389f) * 0.2f;
        float af = MathHelper.sin(j + 4.712389f) * 0.2f;
        float ag = g;
        float ah = 0.0f;
        float ai = 0.4999f;
        float aj = -1.0f + f;
        float ak = aj + g * 2.5f;
        queue.submitCustom(matrices, LAYER, (matricesEntry, vertexConsumer) -> {
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, y, ag, z, l, m, n, 0.4999f, ak);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, y, 0.0f, z, l, m, n, 0.4999f, aj);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, aa, 0.0f, ab, l, m, n, 0.0f, aj);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, aa, ag, ab, l, m, n, 0.0f, ak);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, ac, ag, ad, l, m, n, 0.4999f, ak);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, ac, 0.0f, ad, l, m, n, 0.4999f, aj);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, ae, 0.0f, af, l, m, n, 0.0f, aj);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, ae, ag, af, l, m, n, 0.0f, ak);
            float ac = MathHelper.floor(beamTicks) % 2 == 0 ? 0.5f : 0.0f;
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, q, ag, r, l, m, n, 0.5f, ac + 0.5f);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, s, ag, t, l, m, n, 1.0f, ac + 0.5f);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, w, ag, x, l, m, n, 1.0f, ac);
            GuardianEntityRenderer.vertex(vertexConsumer, matricesEntry, u, ag, v, l, m, n, 0.5f, ac);
        });
    }

    private static void vertex(VertexConsumer vertexConsumer, MatrixStack.Entry matrix, float x, float y, float z, int red, int green, int blue, float u, float v) {
        vertexConsumer.vertex(matrix, x, y, z).color(red, green, blue, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(matrix, 0.0f, 1.0f, 0.0f);
    }

    @Override
    public Identifier getTexture(GuardianEntityRenderState guardianEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public GuardianEntityRenderState createRenderState() {
        return new GuardianEntityRenderState();
    }

    @Override
    public void updateRenderState(GuardianEntity guardianEntity, GuardianEntityRenderState guardianEntityRenderState, float f) {
        super.updateRenderState(guardianEntity, guardianEntityRenderState, f);
        guardianEntityRenderState.spikesExtension = guardianEntity.getSpikesExtension(f);
        guardianEntityRenderState.tailAngle = guardianEntity.getTailAngle(f);
        guardianEntityRenderState.cameraPosVec = guardianEntity.getCameraPosVec(f);
        Entity entity = GuardianEntityRenderer.getBeamTarget(guardianEntity);
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

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((GuardianEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
