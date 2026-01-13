/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.network.ClientPlayerLikeEntity
 *  net.minecraft.client.network.ClientPlayerLikeState
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.PlayerEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.CapeFeatureRenderer
 *  net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer
 *  net.minecraft.client.render.entity.feature.ElytraFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer
 *  net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer
 *  net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.PlayerEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.SwingAnimationComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.PlayerLikeEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerModelPart
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.consume.UseAction
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.SwingAnimationType
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.network.ClientPlayerLikeState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer<AvatarlikeEntity extends PlayerLikeEntity>
extends LivingEntityRenderer<AvatarlikeEntity, PlayerEntityRenderState, PlayerEntityModel> {
    public PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
        super(ctx, (EntityModel)new PlayerEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5f);
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel((EquipmentModelData)(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER_EQUIPMENT), (LoadedEntityModels)ctx.getEntityModels(), root -> new PlayerEntityModel(root, slim)), ctx.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new PlayerHeldItemFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new StuckArrowsFeatureRenderer((LivingEntityRenderer)this, ctx));
        this.addFeature((FeatureRenderer)new Deadmau5FeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels()));
        this.addFeature((FeatureRenderer)new CapeFeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels(), ctx.getEquipmentModelLoader()));
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels(), ctx.getPlayerSkinCache()));
        this.addFeature((FeatureRenderer)new ElytraFeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels(), ctx.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new ShoulderParrotFeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels()));
        this.addFeature((FeatureRenderer)new TridentRiptideFeatureRenderer((FeatureRendererContext)this, ctx.getEntityModels()));
        this.addFeature((FeatureRenderer)new StuckStingersFeatureRenderer((LivingEntityRenderer)this, ctx));
    }

    protected boolean shouldRenderFeatures(PlayerEntityRenderState playerEntityRenderState) {
        return !playerEntityRenderState.spectator;
    }

    public Vec3d getPositionOffset(PlayerEntityRenderState playerEntityRenderState) {
        Vec3d vec3d = super.getPositionOffset((EntityRenderState)playerEntityRenderState);
        if (playerEntityRenderState.isInSneakingPose) {
            return vec3d.add(0.0, (double)(playerEntityRenderState.baseScale * -2.0f) / 16.0, 0.0);
        }
        return vec3d;
    }

    private static BipedEntityModel.ArmPose getArmPose(PlayerLikeEntity player, Arm arm) {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        ItemStack itemStack2 = player.getStackInHand(Hand.OFF_HAND);
        BipedEntityModel.ArmPose armPose = PlayerEntityRenderer.getArmPose((PlayerLikeEntity)player, (ItemStack)itemStack, (Hand)Hand.MAIN_HAND);
        BipedEntityModel.ArmPose armPose2 = PlayerEntityRenderer.getArmPose((PlayerLikeEntity)player, (ItemStack)itemStack2, (Hand)Hand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            BipedEntityModel.ArmPose armPose3 = armPose2 = itemStack2.isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
        }
        if (player.getMainArm() == arm) {
            return armPose;
        }
        return armPose2;
    }

    private static BipedEntityModel.ArmPose getArmPose(PlayerLikeEntity player, ItemStack stack, Hand hand) {
        SwingAnimationComponent swingAnimationComponent;
        if (stack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (!player.handSwinging && stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged((ItemStack)stack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
            UseAction useAction = stack.getUseAction();
            if (useAction == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (useAction == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAction == UseAction.TRIDENT) {
                return BipedEntityModel.ArmPose.THROW_TRIDENT;
            }
            if (useAction == UseAction.CROSSBOW) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAction == UseAction.SPYGLASS) {
                return BipedEntityModel.ArmPose.SPYGLASS;
            }
            if (useAction == UseAction.TOOT_HORN) {
                return BipedEntityModel.ArmPose.TOOT_HORN;
            }
            if (useAction == UseAction.BRUSH) {
                return BipedEntityModel.ArmPose.BRUSH;
            }
            if (useAction == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.SPEAR;
            }
        }
        if ((swingAnimationComponent = (SwingAnimationComponent)stack.get(DataComponentTypes.SWING_ANIMATION)) != null && swingAnimationComponent.type() == SwingAnimationType.STAB && player.handSwinging) {
            return BipedEntityModel.ArmPose.SPEAR;
        }
        if (stack.isIn(ItemTags.SPEARS)) {
            return BipedEntityModel.ArmPose.SPEAR;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    public Identifier getTexture(PlayerEntityRenderState playerEntityRenderState) {
        return playerEntityRenderState.skinTextures.body().texturePath();
    }

    protected void scale(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack) {
        float f = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    protected void renderLabelIfPresent(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        int i;
        matrixStack.push();
        int n = i = playerEntityRenderState.extraEars ? -10 : 0;
        if (playerEntityRenderState.playerName != null) {
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, i, playerEntityRenderState.playerName, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
            Objects.requireNonNull(this.getTextRenderer());
            matrixStack.translate(0.0f, 9.0f * 1.15f * 0.025f, 0.0f);
        }
        if (playerEntityRenderState.displayName != null) {
            orderedRenderCommandQueue.submitLabel(matrixStack, playerEntityRenderState.nameLabelPos, i, playerEntityRenderState.displayName, !playerEntityRenderState.sneaking, playerEntityRenderState.light, playerEntityRenderState.squaredDistanceToCamera, cameraRenderState);
        }
        matrixStack.pop();
    }

    public PlayerEntityRenderState createRenderState() {
        return new PlayerEntityRenderState();
    }

    public void updateRenderState(AvatarlikeEntity playerLikeEntity, PlayerEntityRenderState playerEntityRenderState, float f) {
        ItemStack itemStack;
        super.updateRenderState(playerLikeEntity, (LivingEntityRenderState)playerEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(playerLikeEntity, (BipedEntityRenderState)playerEntityRenderState, (float)f, (ItemModelManager)this.itemModelResolver);
        playerEntityRenderState.leftArmPose = PlayerEntityRenderer.getArmPose(playerLikeEntity, (Arm)Arm.LEFT);
        playerEntityRenderState.rightArmPose = PlayerEntityRenderer.getArmPose(playerLikeEntity, (Arm)Arm.RIGHT);
        playerEntityRenderState.skinTextures = ((ClientPlayerLikeEntity)playerLikeEntity).getSkin();
        playerEntityRenderState.stuckArrowCount = playerLikeEntity.getStuckArrowCount();
        playerEntityRenderState.stingerCount = playerLikeEntity.getStingerCount();
        playerEntityRenderState.spectator = playerLikeEntity.isSpectator();
        playerEntityRenderState.hatVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.HAT);
        playerEntityRenderState.jacketVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.JACKET);
        playerEntityRenderState.leftPantsLegVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
        playerEntityRenderState.rightPantsLegVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
        playerEntityRenderState.leftSleeveVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.LEFT_SLEEVE);
        playerEntityRenderState.rightSleeveVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE);
        playerEntityRenderState.capeVisible = playerLikeEntity.isModelPartVisible(PlayerModelPart.CAPE);
        this.updateGliding(playerLikeEntity, playerEntityRenderState, f);
        this.updateCape(playerLikeEntity, playerEntityRenderState, f);
        playerEntityRenderState.playerName = playerEntityRenderState.squaredDistanceToCamera < 100.0 ? ((ClientPlayerLikeEntity)playerLikeEntity).getMannequinName() : null;
        playerEntityRenderState.leftShoulderParrotVariant = ((ClientPlayerLikeEntity)playerLikeEntity).getShoulderParrotVariant(true);
        playerEntityRenderState.rightShoulderParrotVariant = ((ClientPlayerLikeEntity)playerLikeEntity).getShoulderParrotVariant(false);
        playerEntityRenderState.id = playerLikeEntity.getId();
        playerEntityRenderState.extraEars = ((ClientPlayerLikeEntity)playerLikeEntity).hasExtraEars();
        playerEntityRenderState.spyglassState.clear();
        if (playerEntityRenderState.isUsingItem && (itemStack = playerLikeEntity.getStackInHand(playerEntityRenderState.activeHand)).isOf(Items.SPYGLASS)) {
            this.itemModelResolver.updateForLivingEntity(playerEntityRenderState.spyglassState, itemStack, ItemDisplayContext.HEAD, playerLikeEntity);
        }
    }

    protected boolean hasLabel(AvatarlikeEntity playerLikeEntity, double d) {
        return super.hasLabel(playerLikeEntity, d) && (playerLikeEntity.shouldRenderName() || playerLikeEntity.hasCustomName() && playerLikeEntity == this.dispatcher.targetedEntity);
    }

    private void updateGliding(AvatarlikeEntity player, PlayerEntityRenderState state, float tickProgress) {
        state.glidingTicks = (float)player.getGlidingTicks() + tickProgress;
        Vec3d vec3d = player.getRotationVec(tickProgress);
        Vec3d vec3d2 = ((ClientPlayerLikeEntity)player).getState().getVelocity().lerp(player.getVelocity(), (double)tickProgress);
        if (vec3d2.horizontalLengthSquared() > (double)1.0E-5f && vec3d.horizontalLengthSquared() > (double)1.0E-5f) {
            state.applyFlyingRotation = true;
            double d = vec3d2.getHorizontal().normalize().dotProduct(vec3d.getHorizontal().normalize());
            double e = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
            state.flyingRotation = (float)(Math.signum(e) * Math.acos(Math.min(1.0, Math.abs(d))));
        } else {
            state.applyFlyingRotation = false;
            state.flyingRotation = 0.0f;
        }
    }

    private void updateCape(AvatarlikeEntity player, PlayerEntityRenderState state, float tickProgress) {
        ClientPlayerLikeState clientPlayerLikeState = ((ClientPlayerLikeEntity)player).getState();
        double d = clientPlayerLikeState.lerpX(tickProgress) - MathHelper.lerp((double)tickProgress, (double)((PlayerLikeEntity)player).lastX, (double)player.getX());
        double e = clientPlayerLikeState.lerpY(tickProgress) - MathHelper.lerp((double)tickProgress, (double)((PlayerLikeEntity)player).lastY, (double)player.getY());
        double f = clientPlayerLikeState.lerpZ(tickProgress) - MathHelper.lerp((double)tickProgress, (double)((PlayerLikeEntity)player).lastZ, (double)player.getZ());
        float g = MathHelper.lerpAngleDegrees((float)tickProgress, (float)((PlayerLikeEntity)player).lastBodyYaw, (float)((PlayerLikeEntity)player).bodyYaw);
        double h = MathHelper.sin((double)(g * ((float)Math.PI / 180)));
        double i = -MathHelper.cos((double)(g * ((float)Math.PI / 180)));
        state.field_53536 = (float)e * 10.0f;
        state.field_53536 = MathHelper.clamp((float)state.field_53536, (float)-6.0f, (float)32.0f);
        state.field_53537 = (float)(d * h + f * i) * 100.0f;
        state.field_53537 *= 1.0f - state.getGlidingProgress();
        state.field_53537 = MathHelper.clamp((float)state.field_53537, (float)0.0f, (float)150.0f);
        state.field_53538 = (float)(d * i - f * h) * 100.0f;
        state.field_53538 = MathHelper.clamp((float)state.field_53538, (float)-20.0f, (float)20.0f);
        float j = clientPlayerLikeState.lerpMovement(tickProgress);
        float k = clientPlayerLikeState.getLerpedDistanceMoved(tickProgress);
        state.field_53536 += MathHelper.sin((double)(k * 6.0f)) * 32.0f * j;
    }

    public void renderRightArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        this.renderArm(matrices, queue, light, skinTexture, ((PlayerEntityModel)this.model).rightArm, sleeveVisible);
    }

    public void renderLeftArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, boolean sleeveVisible) {
        this.renderArm(matrices, queue, light, skinTexture, ((PlayerEntityModel)this.model).leftArm, sleeveVisible);
    }

    private void renderArm(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, Identifier skinTexture, ModelPart arm, boolean sleeveVisible) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        arm.resetTransform();
        arm.visible = true;
        playerEntityModel.leftSleeve.visible = sleeveVisible;
        playerEntityModel.rightSleeve.visible = sleeveVisible;
        playerEntityModel.leftArm.roll = -0.1f;
        playerEntityModel.rightArm.roll = 0.1f;
        queue.submitModelPart(arm, matrices, RenderLayers.entityTranslucent((Identifier)skinTexture), light, OverlayTexture.DEFAULT_UV, null);
    }

    protected void setupTransforms(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, float f, float g) {
        float h = playerEntityRenderState.leaningPitch;
        float i = playerEntityRenderState.pitch;
        if (playerEntityRenderState.isGliding) {
            super.setupTransforms((LivingEntityRenderState)playerEntityRenderState, matrixStack, f, g);
            float j = playerEntityRenderState.getGlidingProgress();
            if (!playerEntityRenderState.usingRiptide) {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0f - i)));
            }
            if (playerEntityRenderState.applyFlyingRotation) {
                matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(playerEntityRenderState.flyingRotation));
            }
        } else if (h > 0.0f) {
            super.setupTransforms((LivingEntityRenderState)playerEntityRenderState, matrixStack, f, g);
            float j = playerEntityRenderState.touchingWater ? -90.0f - i : -90.0f;
            float k = MathHelper.lerp((float)h, (float)0.0f, (float)j);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(k));
            if (playerEntityRenderState.isSwimming) {
                matrixStack.translate(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupTransforms((LivingEntityRenderState)playerEntityRenderState, matrixStack, f, g);
        }
    }

    public boolean shouldFlipUpsideDown(AvatarlikeEntity playerLikeEntity) {
        if (playerLikeEntity.isModelPartVisible(PlayerModelPart.CAPE)) {
            if (playerLikeEntity instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)playerLikeEntity;
                return PlayerEntityRenderer.shouldFlipUpsideDown((PlayerEntity)playerEntity);
            }
            return super.shouldFlipUpsideDown(playerLikeEntity);
        }
        return false;
    }

    public static boolean shouldFlipUpsideDown(PlayerEntity player) {
        return PlayerEntityRenderer.shouldFlipUpsideDown((String)player.getGameProfile().name());
    }

    public /* synthetic */ boolean shouldFlipUpsideDown(LivingEntity entity) {
        return this.shouldFlipUpsideDown((PlayerLikeEntity)entity);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((PlayerEntityRenderState)state);
    }

    protected /* synthetic */ boolean shouldRenderFeatures(LivingEntityRenderState state) {
        return this.shouldRenderFeatures((PlayerEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }

    protected /* synthetic */ void renderLabelIfPresent(EntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        this.renderLabelIfPresent((PlayerEntityRenderState)state, matrices, queue, cameraRenderState);
    }

    public /* synthetic */ Vec3d getPositionOffset(EntityRenderState state) {
        return this.getPositionOffset((PlayerEntityRenderState)state);
    }
}

