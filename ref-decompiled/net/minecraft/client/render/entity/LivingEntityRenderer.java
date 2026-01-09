package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer extends EntityRenderer implements FeatureRendererContext {
   private static final float field_32939 = 0.1F;
   protected EntityModel model;
   protected final ItemModelManager itemModelResolver;
   protected final List features = Lists.newArrayList();

   public LivingEntityRenderer(EntityRendererFactory.Context ctx, EntityModel model, float shadowRadius) {
      super(ctx);
      this.itemModelResolver = ctx.getItemModelManager();
      this.model = model;
      this.shadowRadius = shadowRadius;
   }

   protected final boolean addFeature(FeatureRenderer feature) {
      return this.features.add(feature);
   }

   public EntityModel getModel() {
      return this.model;
   }

   protected Box getBoundingBox(LivingEntity livingEntity) {
      Box box = super.getBoundingBox(livingEntity);
      if (livingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
         float f = 0.5F;
         return box.expand(0.5, 0.5, 0.5);
      } else {
         return box;
      }
   }

   public void render(LivingEntityRenderState livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      matrixStack.push();
      if (livingEntityRenderState.isInPose(EntityPose.SLEEPING)) {
         Direction direction = livingEntityRenderState.sleepingDirection;
         if (direction != null) {
            float f = livingEntityRenderState.standingEyeHeight - 0.1F;
            matrixStack.translate((float)(-direction.getOffsetX()) * f, 0.0F, (float)(-direction.getOffsetZ()) * f);
         }
      }

      float g = livingEntityRenderState.baseScale;
      matrixStack.scale(g, g, g);
      this.setupTransforms(livingEntityRenderState, matrixStack, livingEntityRenderState.bodyYaw, g);
      matrixStack.scale(-1.0F, -1.0F, 1.0F);
      this.scale(livingEntityRenderState, matrixStack);
      matrixStack.translate(0.0F, -1.501F, 0.0F);
      this.model.setAngles(livingEntityRenderState);
      boolean bl = this.isVisible(livingEntityRenderState);
      boolean bl2 = !bl && !livingEntityRenderState.invisibleToPlayer;
      RenderLayer renderLayer = this.getRenderLayer(livingEntityRenderState, bl, bl2, livingEntityRenderState.hasOutline);
      if (renderLayer != null) {
         VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
         int j = getOverlay(livingEntityRenderState, this.getAnimationCounter(livingEntityRenderState));
         int k = bl2 ? 654311423 : -1;
         int l = ColorHelper.mix(k, this.getMixColor(livingEntityRenderState));
         this.model.render(matrixStack, vertexConsumer, i, j, l);
      }

      if (this.shouldRenderFeatures(livingEntityRenderState)) {
         Iterator var15 = this.features.iterator();

         while(var15.hasNext()) {
            FeatureRenderer featureRenderer = (FeatureRenderer)var15.next();
            featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntityRenderState, livingEntityRenderState.relativeHeadYaw, livingEntityRenderState.pitch);
         }
      }

      matrixStack.pop();
      super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
   }

   protected boolean shouldRenderFeatures(LivingEntityRenderState state) {
      return true;
   }

   protected int getMixColor(LivingEntityRenderState state) {
      return -1;
   }

   public abstract Identifier getTexture(LivingEntityRenderState state);

   @Nullable
   protected RenderLayer getRenderLayer(LivingEntityRenderState state, boolean showBody, boolean translucent, boolean showOutline) {
      Identifier identifier = this.getTexture(state);
      if (translucent) {
         return RenderLayer.getItemEntityTranslucentCull(identifier);
      } else if (showBody) {
         return this.model.getLayer(identifier);
      } else {
         return showOutline ? RenderLayer.getOutline(identifier) : null;
      }
   }

   public static int getOverlay(LivingEntityRenderState state, float whiteOverlayProgress) {
      return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(state.hurt));
   }

   protected boolean isVisible(LivingEntityRenderState state) {
      return !state.invisible;
   }

   private static float getYaw(Direction direction) {
      switch (direction) {
         case SOUTH:
            return 90.0F;
         case WEST:
            return 0.0F;
         case NORTH:
            return 270.0F;
         case EAST:
            return 180.0F;
         default:
            return 0.0F;
      }
   }

   protected boolean isShaking(LivingEntityRenderState state) {
      return state.shaking;
   }

   protected void setupTransforms(LivingEntityRenderState state, MatrixStack matrices, float bodyYaw, float baseHeight) {
      if (this.isShaking(state)) {
         bodyYaw += (float)(Math.cos((double)((float)MathHelper.floor(state.age) * 3.25F)) * Math.PI * 0.4000000059604645);
      }

      if (!state.isInPose(EntityPose.SLEEPING)) {
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
      }

      if (state.deathTime > 0.0F) {
         float f = (state.deathTime - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * this.getLyingPositionRotationDegrees()));
      } else if (state.usingRiptide) {
         matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - state.pitch));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.age * -75.0F));
      } else if (state.isInPose(EntityPose.SLEEPING)) {
         Direction direction = state.sleepingDirection;
         float g = direction != null ? getYaw(direction) : bodyYaw;
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.getLyingPositionRotationDegrees()));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
      } else if (state.flipUpsideDown) {
         matrices.translate(0.0F, (state.height + 0.1F) / baseHeight, 0.0F);
         matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
      }

   }

   protected float getLyingPositionRotationDegrees() {
      return 90.0F;
   }

   protected float getAnimationCounter(LivingEntityRenderState state) {
      return 0.0F;
   }

   protected void scale(LivingEntityRenderState state, MatrixStack matrices) {
   }

   protected boolean hasLabel(LivingEntity livingEntity, double d) {
      if (livingEntity.isSneaky()) {
         float f = 32.0F;
         if (d >= 1024.0) {
            return false;
         }
      }

      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
      boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
      if (livingEntity != clientPlayerEntity) {
         AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
         AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
         if (abstractTeam != null) {
            AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
            switch (visibilityRule) {
               case ALWAYS:
                  return bl;
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
               case HIDE_FOR_OWN_TEAM:
                  return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
               default:
                  return true;
            }
         }
      }

      return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
   }

   public static boolean shouldFlipUpsideDown(LivingEntity entity) {
      if (entity instanceof PlayerEntity || entity.hasCustomName()) {
         String string = Formatting.strip(entity.getName().getString());
         if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
            boolean var10000;
            if (entity instanceof PlayerEntity) {
               PlayerEntity playerEntity = (PlayerEntity)entity;
               if (!playerEntity.isPartVisible(PlayerModelPart.CAPE)) {
                  var10000 = false;
                  return var10000;
               }
            }

            var10000 = true;
            return var10000;
         }
      }

      return false;
   }

   protected float getShadowRadius(LivingEntityRenderState livingEntityRenderState) {
      return super.getShadowRadius(livingEntityRenderState) * livingEntityRenderState.baseScale;
   }

   public void updateRenderState(LivingEntity livingEntity, LivingEntityRenderState livingEntityRenderState, float f) {
      super.updateRenderState(livingEntity, livingEntityRenderState, f);
      float g = MathHelper.lerpAngleDegrees(f, livingEntity.lastHeadYaw, livingEntity.headYaw);
      livingEntityRenderState.bodyYaw = clampBodyYaw(livingEntity, g, f);
      livingEntityRenderState.relativeHeadYaw = MathHelper.wrapDegrees(g - livingEntityRenderState.bodyYaw);
      livingEntityRenderState.pitch = livingEntity.getLerpedPitch(f);
      livingEntityRenderState.customName = livingEntity.getCustomName();
      livingEntityRenderState.flipUpsideDown = shouldFlipUpsideDown(livingEntity);
      if (livingEntityRenderState.flipUpsideDown) {
         livingEntityRenderState.pitch *= -1.0F;
         livingEntityRenderState.relativeHeadYaw *= -1.0F;
      }

      if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
         livingEntityRenderState.limbSwingAnimationProgress = livingEntity.limbAnimator.getAnimationProgress(f);
         livingEntityRenderState.limbSwingAmplitude = livingEntity.limbAnimator.getAmplitude(f);
      } else {
         livingEntityRenderState.limbSwingAnimationProgress = 0.0F;
         livingEntityRenderState.limbSwingAmplitude = 0.0F;
      }

      Entity var6 = livingEntity.getVehicle();
      if (var6 instanceof LivingEntity livingEntity2) {
         livingEntityRenderState.headItemAnimationProgress = livingEntity2.limbAnimator.getAnimationProgress(f);
      } else {
         livingEntityRenderState.headItemAnimationProgress = livingEntityRenderState.limbSwingAnimationProgress;
      }

      livingEntityRenderState.baseScale = livingEntity.getScale();
      livingEntityRenderState.ageScale = livingEntity.getScaleFactor();
      livingEntityRenderState.pose = livingEntity.getPose();
      livingEntityRenderState.sleepingDirection = livingEntity.getSleepingDirection();
      if (livingEntityRenderState.sleepingDirection != null) {
         livingEntityRenderState.standingEyeHeight = livingEntity.getEyeHeight(EntityPose.STANDING);
      }

      label48: {
         livingEntityRenderState.shaking = livingEntity.isFrozen();
         livingEntityRenderState.baby = livingEntity.isBaby();
         livingEntityRenderState.touchingWater = livingEntity.isTouchingWater();
         livingEntityRenderState.usingRiptide = livingEntity.isUsingRiptide();
         livingEntityRenderState.hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
         ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
         Item var8 = itemStack.getItem();
         if (var8 instanceof BlockItem blockItem) {
            Block var12 = blockItem.getBlock();
            if (var12 instanceof AbstractSkullBlock abstractSkullBlock) {
               livingEntityRenderState.wearingSkullType = abstractSkullBlock.getSkullType();
               livingEntityRenderState.wearingSkullProfile = (ProfileComponent)itemStack.get(DataComponentTypes.PROFILE);
               livingEntityRenderState.headItemRenderState.clear();
               break label48;
            }
         }

         livingEntityRenderState.wearingSkullType = null;
         livingEntityRenderState.wearingSkullProfile = null;
         if (!ArmorFeatureRenderer.hasModel(itemStack, EquipmentSlot.HEAD)) {
            this.itemModelResolver.updateForLivingEntity(livingEntityRenderState.headItemRenderState, itemStack, ItemDisplayContext.HEAD, livingEntity);
         } else {
            livingEntityRenderState.headItemRenderState.clear();
         }
      }

      livingEntityRenderState.deathTime = livingEntity.deathTime > 0 ? (float)livingEntity.deathTime + f : 0.0F;
      MinecraftClient minecraftClient = MinecraftClient.getInstance();
      livingEntityRenderState.invisibleToPlayer = livingEntityRenderState.invisible && livingEntity.isInvisibleTo(minecraftClient.player);
      livingEntityRenderState.hasOutline = minecraftClient.hasOutline(livingEntity);
   }

   protected void appendHitboxes(LivingEntity livingEntity, ImmutableList.Builder builder, float f) {
      Box box = livingEntity.getBoundingBox();
      float g = 0.01F;
      EntityHitbox entityHitbox = new EntityHitbox(box.minX - livingEntity.getX(), (double)(livingEntity.getStandingEyeHeight() - 0.01F), box.minZ - livingEntity.getZ(), box.maxX - livingEntity.getX(), (double)(livingEntity.getStandingEyeHeight() + 0.01F), box.maxZ - livingEntity.getZ(), 1.0F, 0.0F, 0.0F);
      builder.add(entityHitbox);
   }

   private static float clampBodyYaw(LivingEntity entity, float degrees, float tickProgress) {
      Entity var4 = entity.getVehicle();
      if (var4 instanceof LivingEntity livingEntity) {
         float f = MathHelper.lerpAngleDegrees(tickProgress, livingEntity.lastBodyYaw, livingEntity.bodyYaw);
         float g = 85.0F;
         float h = MathHelper.clamp(MathHelper.wrapDegrees(degrees - f), -85.0F, 85.0F);
         f = degrees - h;
         if (Math.abs(h) > 50.0F) {
            f += h * 0.2F;
         }

         return f;
      } else {
         return MathHelper.lerpAngleDegrees(tickProgress, entity.lastBodyYaw, entity.bodyYaw);
      }
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState state) {
      return this.getShadowRadius((LivingEntityRenderState)state);
   }
}
