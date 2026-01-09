package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends EntityRenderer {
   public static final Identifier CRYSTAL_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal_beam.png");
   private static final Identifier EXPLOSION_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_exploding.png");
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon.png");
   private static final Identifier EYE_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderLayer DRAGON_CUTOUT;
   private static final RenderLayer DRAGON_DECAL;
   private static final RenderLayer DRAGON_EYES;
   private static final RenderLayer CRYSTAL_BEAM_LAYER;
   private static final float HALF_SQRT_3;
   private final DragonEntityModel model;

   public EnderDragonEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
      this.shadowRadius = 0.5F;
      this.model = new DragonEntityModel(context.getPart(EntityModelLayers.ENDER_DRAGON));
   }

   public void render(EnderDragonEntityRenderState enderDragonEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      matrixStack.push();
      float f = enderDragonEntityRenderState.getLerpedFrame(7).yRot();
      float g = (float)(enderDragonEntityRenderState.getLerpedFrame(5).y() - enderDragonEntityRenderState.getLerpedFrame(10).y());
      matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
      matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * 10.0F));
      matrixStack.translate(0.0F, 0.0F, 1.0F);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      matrixStack.translate(0.0F, -1.501F, 0.0F);
      this.model.setAngles(enderDragonEntityRenderState);
      VertexConsumer vertexConsumer3;
      if (enderDragonEntityRenderState.ticksSinceDeath > 0.0F) {
         float h = enderDragonEntityRenderState.ticksSinceDeath / 200.0F;
         int j = ColorHelper.withAlpha(MathHelper.floor(h * 255.0F), -1);
         VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEXTURE));
         this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, j);
         VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(DRAGON_DECAL);
         this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.getUv(0.0F, enderDragonEntityRenderState.hurt));
      } else {
         vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_CUTOUT);
         this.model.render(matrixStack, vertexConsumer3, i, OverlayTexture.getUv(0.0F, enderDragonEntityRenderState.hurt));
      }

      vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_EYES);
      this.model.render(matrixStack, vertexConsumer3, i, OverlayTexture.DEFAULT_UV);
      if (enderDragonEntityRenderState.ticksSinceDeath > 0.0F) {
         float k = enderDragonEntityRenderState.ticksSinceDeath / 200.0F;
         matrixStack.push();
         matrixStack.translate(0.0F, -1.0F, -2.0F);
         renderDeathAnimation(matrixStack, k, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRays()));
         renderDeathAnimation(matrixStack, k, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRaysDepth()));
         matrixStack.pop();
      }

      matrixStack.pop();
      if (enderDragonEntityRenderState.crystalBeamPos != null) {
         renderCrystalBeam((float)enderDragonEntityRenderState.crystalBeamPos.x, (float)enderDragonEntityRenderState.crystalBeamPos.y, (float)enderDragonEntityRenderState.crystalBeamPos.z, enderDragonEntityRenderState.age, matrixStack, vertexConsumerProvider, i);
      }

      super.render(enderDragonEntityRenderState, matrixStack, vertexConsumerProvider, i);
   }

   private static void renderDeathAnimation(MatrixStack matrices, float animationProgress, VertexConsumer vertexCOnsumer) {
      matrices.push();
      float f = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
      int i = ColorHelper.fromFloats(1.0F - f, 1.0F, 1.0F, 1.0F);
      int j = 16711935;
      Random random = Random.create(432L);
      Vector3f vector3f = new Vector3f();
      Vector3f vector3f2 = new Vector3f();
      Vector3f vector3f3 = new Vector3f();
      Vector3f vector3f4 = new Vector3f();
      Quaternionf quaternionf = new Quaternionf();
      int k = MathHelper.floor((animationProgress + animationProgress * animationProgress) / 2.0F * 60.0F);

      for(int l = 0; l < k; ++l) {
         quaternionf.rotationXYZ(random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F).rotateXYZ(random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F + animationProgress * 1.5707964F);
         matrices.multiply(quaternionf);
         float g = random.nextFloat() * 20.0F + 5.0F + f * 10.0F;
         float h = random.nextFloat() * 2.0F + 1.0F + f * 2.0F;
         vector3f2.set(-HALF_SQRT_3 * h, g, -0.5F * h);
         vector3f3.set(HALF_SQRT_3 * h, g, -0.5F * h);
         vector3f4.set(0.0F, g, h);
         MatrixStack.Entry entry = matrices.peek();
         vertexCOnsumer.vertex(entry, vector3f).color(i);
         vertexCOnsumer.vertex(entry, vector3f2).color(16711935);
         vertexCOnsumer.vertex(entry, vector3f3).color(16711935);
         vertexCOnsumer.vertex(entry, vector3f).color(i);
         vertexCOnsumer.vertex(entry, vector3f3).color(16711935);
         vertexCOnsumer.vertex(entry, vector3f4).color(16711935);
         vertexCOnsumer.vertex(entry, vector3f).color(i);
         vertexCOnsumer.vertex(entry, vector3f4).color(16711935);
         vertexCOnsumer.vertex(entry, vector3f2).color(16711935);
      }

      matrices.pop();
   }

   public static void renderCrystalBeam(float dx, float dy, float dz, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      float f = MathHelper.sqrt(dx * dx + dz * dz);
      float g = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
      matrices.push();
      matrices.translate(0.0F, 2.0F, 0.0F);
      matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2((double)dz, (double)dx)) - 1.5707964F));
      matrices.multiply(RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2((double)f, (double)dy)) - 1.5707964F));
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CRYSTAL_BEAM_LAYER);
      float h = 0.0F - tickProgress * 0.01F;
      float i = g / 32.0F - tickProgress * 0.01F;
      int j = true;
      float k = 0.0F;
      float l = 0.75F;
      float m = 0.0F;
      MatrixStack.Entry entry = matrices.peek();

      for(int n = 1; n <= 8; ++n) {
         float o = MathHelper.sin((float)n * 6.2831855F / 8.0F) * 0.75F;
         float p = MathHelper.cos((float)n * 6.2831855F / 8.0F) * 0.75F;
         float q = (float)n / 8.0F;
         vertexConsumer.vertex(entry, k * 0.2F, l * 0.2F, 0.0F).color(-16777216).texture(m, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, -1.0F, 0.0F);
         vertexConsumer.vertex(entry, k, l, g).color(-1).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, -1.0F, 0.0F);
         vertexConsumer.vertex(entry, o, p, g).color(-1).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, -1.0F, 0.0F);
         vertexConsumer.vertex(entry, o * 0.2F, p * 0.2F, 0.0F).color(-16777216).texture(q, h).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, -1.0F, 0.0F);
         k = o;
         l = p;
         m = q;
      }

      matrices.pop();
   }

   public EnderDragonEntityRenderState createRenderState() {
      return new EnderDragonEntityRenderState();
   }

   public void updateRenderState(EnderDragonEntity enderDragonEntity, EnderDragonEntityRenderState enderDragonEntityRenderState, float f) {
      super.updateRenderState(enderDragonEntity, enderDragonEntityRenderState, f);
      enderDragonEntityRenderState.wingPosition = MathHelper.lerp(f, enderDragonEntity.lastWingPosition, enderDragonEntity.wingPosition);
      enderDragonEntityRenderState.ticksSinceDeath = enderDragonEntity.ticksSinceDeath > 0 ? (float)enderDragonEntity.ticksSinceDeath + f : 0.0F;
      enderDragonEntityRenderState.hurt = enderDragonEntity.hurtTime > 0;
      EndCrystalEntity endCrystalEntity = enderDragonEntity.connectedCrystal;
      if (endCrystalEntity != null) {
         Vec3d vec3d = endCrystalEntity.getLerpedPos(f).add(0.0, (double)EndCrystalEntityRenderer.getYOffset((float)endCrystalEntity.endCrystalAge + f), 0.0);
         enderDragonEntityRenderState.crystalBeamPos = vec3d.subtract(enderDragonEntity.getLerpedPos(f));
      } else {
         enderDragonEntityRenderState.crystalBeamPos = null;
      }

      Phase phase = enderDragonEntity.getPhaseManager().getCurrent();
      enderDragonEntityRenderState.inLandingOrTakeoffPhase = phase == PhaseType.LANDING || phase == PhaseType.TAKEOFF;
      enderDragonEntityRenderState.sittingOrHovering = phase.isSittingOrHovering();
      BlockPos blockPos = enderDragonEntity.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(enderDragonEntity.getFightOrigin()));
      enderDragonEntityRenderState.squaredDistanceFromOrigin = blockPos.getSquaredDistance(enderDragonEntity.getPos());
      enderDragonEntityRenderState.tickProgress = enderDragonEntity.isDead() ? 0.0F : f;
      enderDragonEntityRenderState.frameTracker.copyFrom(enderDragonEntity.frameTracker);
   }

   protected void appendHitboxes(EnderDragonEntity enderDragonEntity, ImmutableList.Builder builder, float f) {
      super.appendHitboxes(enderDragonEntity, builder, f);
      double d = -MathHelper.lerp((double)f, enderDragonEntity.lastRenderX, enderDragonEntity.getX());
      double e = -MathHelper.lerp((double)f, enderDragonEntity.lastRenderY, enderDragonEntity.getY());
      double g = -MathHelper.lerp((double)f, enderDragonEntity.lastRenderZ, enderDragonEntity.getZ());
      EnderDragonPart[] var10 = enderDragonEntity.getBodyParts();
      int var11 = var10.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         EnderDragonPart enderDragonPart = var10[var12];
         Box box = enderDragonPart.getBoundingBox();
         EntityHitbox entityHitbox = new EntityHitbox(box.minX - enderDragonPart.getX(), box.minY - enderDragonPart.getY(), box.minZ - enderDragonPart.getZ(), box.maxX - enderDragonPart.getX(), box.maxY - enderDragonPart.getY(), box.maxZ - enderDragonPart.getZ(), (float)(d + MathHelper.lerp((double)f, enderDragonPart.lastRenderX, enderDragonPart.getX())), (float)(e + MathHelper.lerp((double)f, enderDragonPart.lastRenderY, enderDragonPart.getY())), (float)(g + MathHelper.lerp((double)f, enderDragonPart.lastRenderZ, enderDragonPart.getZ())), 0.25F, 1.0F, 0.0F);
         builder.add(entityHitbox);
      }

   }

   protected boolean canBeCulled(EnderDragonEntity enderDragonEntity) {
      return false;
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected boolean canBeCulled(final Entity entity) {
      return this.canBeCulled((EnderDragonEntity)entity);
   }

   static {
      DRAGON_CUTOUT = RenderLayer.getEntityCutoutNoCull(TEXTURE);
      DRAGON_DECAL = RenderLayer.getEntityDecal(TEXTURE);
      DRAGON_EYES = RenderLayer.getEyes(EYE_TEXTURE);
      CRYSTAL_BEAM_LAYER = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
      HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
   }
}
