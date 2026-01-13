/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.DragonEntityModel
 *  net.minecraft.client.render.entity.EndCrystalEntityRenderer
 *  net.minecraft.client.render.entity.EnderDragonEntityRenderer
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.EnderDragonEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.boss.dragon.EnderDragonEntity
 *  net.minecraft.entity.boss.dragon.phase.Phase
 *  net.minecraft.entity.boss.dragon.phase.PhaseType
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.world.gen.feature.EndPortalFeature
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.DragonEntityModel;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class EnderDragonEntityRenderer
extends EntityRenderer<EnderDragonEntity, EnderDragonEntityRenderState> {
    public static final Identifier CRYSTAL_BEAM_TEXTURE = Identifier.ofVanilla((String)"textures/entity/end_crystal/end_crystal_beam.png");
    private static final Identifier EXPLOSION_TEXTURE = Identifier.ofVanilla((String)"textures/entity/enderdragon/dragon_exploding.png");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/enderdragon/dragon.png");
    private static final Identifier EYE_TEXTURE = Identifier.ofVanilla((String)"textures/entity/enderdragon/dragon_eyes.png");
    private static final RenderLayer DRAGON_CUTOUT = RenderLayers.entityCutoutNoCull((Identifier)TEXTURE);
    private static final RenderLayer DRAGON_DECAL = RenderLayers.entityDecal((Identifier)TEXTURE);
    private static final RenderLayer DRAGON_EYES = RenderLayers.eyes((Identifier)EYE_TEXTURE);
    private static final RenderLayer CRYSTAL_BEAM_LAYER = RenderLayers.entitySmoothCutout((Identifier)CRYSTAL_BEAM_TEXTURE);
    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
    private final DragonEntityModel model;

    public EnderDragonEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
        this.model = new DragonEntityModel(context.getPart(EntityModelLayers.ENDER_DRAGON));
    }

    public void render(EnderDragonEntityRenderState enderDragonEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        float f = enderDragonEntityRenderState.getLerpedFrame(7).yRot();
        float g = (float)(enderDragonEntityRenderState.getLerpedFrame(5).y() - enderDragonEntityRenderState.getLerpedFrame(10).y());
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(g * 10.0f));
        matrixStack.translate(0.0f, 0.0f, 1.0f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        int i = OverlayTexture.getUv((float)0.0f, (boolean)enderDragonEntityRenderState.hurt);
        if (enderDragonEntityRenderState.ticksSinceDeath > 0.0f) {
            int j = ColorHelper.getWhite((float)(enderDragonEntityRenderState.ticksSinceDeath / 200.0f));
            orderedRenderCommandQueue.getBatchingQueue(0).submitModel((Model)this.model, (Object)enderDragonEntityRenderState, matrixStack, RenderLayers.entityAlpha((Identifier)EXPLOSION_TEXTURE), enderDragonEntityRenderState.light, OverlayTexture.DEFAULT_UV, j, null, enderDragonEntityRenderState.outlineColor, null);
            orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.model, (Object)enderDragonEntityRenderState, matrixStack, DRAGON_DECAL, enderDragonEntityRenderState.light, i, -1, null, enderDragonEntityRenderState.outlineColor, null);
        } else {
            orderedRenderCommandQueue.getBatchingQueue(0).submitModel((Model)this.model, (Object)enderDragonEntityRenderState, matrixStack, DRAGON_CUTOUT, enderDragonEntityRenderState.light, i, -1, null, enderDragonEntityRenderState.outlineColor, null);
        }
        orderedRenderCommandQueue.submitModel((Model)this.model, (Object)enderDragonEntityRenderState, matrixStack, DRAGON_EYES, enderDragonEntityRenderState.light, OverlayTexture.DEFAULT_UV, enderDragonEntityRenderState.outlineColor, null);
        if (enderDragonEntityRenderState.ticksSinceDeath > 0.0f) {
            float h = enderDragonEntityRenderState.ticksSinceDeath / 200.0f;
            matrixStack.push();
            matrixStack.translate(0.0f, -1.0f, -2.0f);
            EnderDragonEntityRenderer.renderDeathAnimation((MatrixStack)matrixStack, (float)h, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (RenderLayer)RenderLayers.dragonRays());
            EnderDragonEntityRenderer.renderDeathAnimation((MatrixStack)matrixStack, (float)h, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (RenderLayer)RenderLayers.dragonRaysDepth());
            matrixStack.pop();
        }
        matrixStack.pop();
        if (enderDragonEntityRenderState.crystalBeamPos != null) {
            EnderDragonEntityRenderer.renderCrystalBeam((float)((float)enderDragonEntityRenderState.crystalBeamPos.x), (float)((float)enderDragonEntityRenderState.crystalBeamPos.y), (float)((float)enderDragonEntityRenderState.crystalBeamPos.z), (float)enderDragonEntityRenderState.age, (MatrixStack)matrixStack, (OrderedRenderCommandQueue)orderedRenderCommandQueue, (int)enderDragonEntityRenderState.light);
        }
        super.render((EntityRenderState)enderDragonEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    private static void renderDeathAnimation(MatrixStack matrices, float animationProgress, OrderedRenderCommandQueue queue, RenderLayer renderLayer) {
        queue.submitCustom(matrices, renderLayer, (matricesEntry, vertexConsumer) -> {
            float g = Math.min(animationProgress > 0.8f ? (animationProgress - 0.8f) / 0.2f : 0.0f, 1.0f);
            int i = ColorHelper.fromFloats((float)(1.0f - g), (float)1.0f, (float)1.0f, (float)1.0f);
            int j = 0xFF00FF;
            Random random = Random.create((long)432L);
            Vector3f vector3f = new Vector3f();
            Vector3f vector3f2 = new Vector3f();
            Vector3f vector3f3 = new Vector3f();
            Vector3f vector3f4 = new Vector3f();
            Quaternionf quaternionf = new Quaternionf();
            int k = MathHelper.floor((float)((animationProgress + animationProgress * animationProgress) / 2.0f * 60.0f));
            for (int l = 0; l < k; ++l) {
                quaternionf.rotationXYZ(random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2)).rotateXYZ(random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2), random.nextFloat() * ((float)Math.PI * 2) + animationProgress * 1.5707964f);
                matricesEntry.rotate((Quaternionfc)quaternionf);
                float h = random.nextFloat() * 20.0f + 5.0f + g * 10.0f;
                float m = random.nextFloat() * 2.0f + 1.0f + g * 2.0f;
                vector3f2.set(-HALF_SQRT_3 * m, h, -0.5f * m);
                vector3f3.set(HALF_SQRT_3 * m, h, -0.5f * m);
                vector3f4.set(0.0f, h, m);
                vertexConsumer.vertex(matricesEntry, vector3f).color(i);
                vertexConsumer.vertex(matricesEntry, vector3f2).color(0xFF00FF);
                vertexConsumer.vertex(matricesEntry, vector3f3).color(0xFF00FF);
                vertexConsumer.vertex(matricesEntry, vector3f).color(i);
                vertexConsumer.vertex(matricesEntry, vector3f3).color(0xFF00FF);
                vertexConsumer.vertex(matricesEntry, vector3f4).color(0xFF00FF);
                vertexConsumer.vertex(matricesEntry, vector3f).color(i);
                vertexConsumer.vertex(matricesEntry, vector3f4).color(0xFF00FF);
                vertexConsumer.vertex(matricesEntry, vector3f2).color(0xFF00FF);
            }
        });
    }

    public static void renderCrystalBeam(float dx, float dy, float dz, float tickProgress, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        float f = MathHelper.sqrt((float)(dx * dx + dz * dz));
        float g = MathHelper.sqrt((float)(dx * dx + dy * dy + dz * dz));
        matrices.push();
        matrices.translate(0.0f, 2.0f, 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2(dz, dx)) - 1.5707964f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2(f, dy)) - 1.5707964f));
        float h = 0.0f - tickProgress * 0.01f;
        float i = g / 32.0f - tickProgress * 0.01f;
        queue.submitCustom(matrices, CRYSTAL_BEAM_LAYER, (matricesEntry, vertexConsumer) -> {
            int j = 8;
            float k = 0.0f;
            float l = 0.75f;
            float m = 0.0f;
            for (int n = 1; n <= 8; ++n) {
                float o = MathHelper.sin((double)((float)n * ((float)Math.PI * 2) / 8.0f)) * 0.75f;
                float p = MathHelper.cos((double)((float)n * ((float)Math.PI * 2) / 8.0f)) * 0.75f;
                float q = (float)n / 8.0f;
                vertexConsumer.vertex(matricesEntry, k * 0.2f, l * 0.2f, 0.0f).color(-16777216).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matricesEntry, 0.0f, -1.0f, 0.0f);
                vertexConsumer.vertex(matricesEntry, k, l, g).color(-1).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matricesEntry, 0.0f, -1.0f, 0.0f);
                vertexConsumer.vertex(matricesEntry, o, p, g).color(-1).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matricesEntry, 0.0f, -1.0f, 0.0f);
                vertexConsumer.vertex(matricesEntry, o * 0.2f, p * 0.2f, 0.0f).color(-16777216).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matricesEntry, 0.0f, -1.0f, 0.0f);
                k = o;
                l = p;
                m = q;
            }
        });
        matrices.pop();
    }

    public EnderDragonEntityRenderState createRenderState() {
        return new EnderDragonEntityRenderState();
    }

    public void updateRenderState(EnderDragonEntity enderDragonEntity, EnderDragonEntityRenderState enderDragonEntityRenderState, float f) {
        super.updateRenderState((Entity)enderDragonEntity, (EntityRenderState)enderDragonEntityRenderState, f);
        enderDragonEntityRenderState.wingPosition = MathHelper.lerp((float)f, (float)enderDragonEntity.lastWingPosition, (float)enderDragonEntity.wingPosition);
        enderDragonEntityRenderState.ticksSinceDeath = enderDragonEntity.ticksSinceDeath > 0 ? (float)enderDragonEntity.ticksSinceDeath + f : 0.0f;
        enderDragonEntityRenderState.hurt = enderDragonEntity.hurtTime > 0;
        EndCrystalEntity endCrystalEntity = enderDragonEntity.connectedCrystal;
        if (endCrystalEntity != null) {
            Vec3d vec3d = endCrystalEntity.getLerpedPos(f).add(0.0, (double)EndCrystalEntityRenderer.getYOffset((float)((float)endCrystalEntity.endCrystalAge + f)), 0.0);
            enderDragonEntityRenderState.crystalBeamPos = vec3d.subtract(enderDragonEntity.getLerpedPos(f));
        } else {
            enderDragonEntityRenderState.crystalBeamPos = null;
        }
        Phase phase = enderDragonEntity.getPhaseManager().getCurrent();
        enderDragonEntityRenderState.inLandingOrTakeoffPhase = phase == PhaseType.LANDING || phase == PhaseType.TAKEOFF;
        enderDragonEntityRenderState.sittingOrHovering = phase.isSittingOrHovering();
        BlockPos blockPos = enderDragonEntity.getEntityWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin((BlockPos)enderDragonEntity.getFightOrigin()));
        enderDragonEntityRenderState.squaredDistanceFromOrigin = blockPos.getSquaredDistance((Position)enderDragonEntity.getEntityPos());
        enderDragonEntityRenderState.tickProgress = enderDragonEntity.isDead() ? 0.0f : f;
        enderDragonEntityRenderState.frameTracker.copyFrom(enderDragonEntity.frameTracker);
    }

    protected boolean canBeCulled(EnderDragonEntity enderDragonEntity) {
        return false;
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ boolean canBeCulled(Entity entity) {
        return this.canBeCulled((EnderDragonEntity)entity);
    }
}

