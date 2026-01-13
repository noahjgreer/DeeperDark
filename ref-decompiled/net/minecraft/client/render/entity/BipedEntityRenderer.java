/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.ElytraFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer$HeadTransformation
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.state.ArmedEntityRenderState
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.SwingAnimationComponent
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.SwingAnimationType
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.SwingAnimationType;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class BipedEntityRenderer<T extends MobEntity, S extends BipedEntityRenderState, M extends BipedEntityModel<S>>
extends AgeableMobEntityRenderer<T, S, M> {
    public BipedEntityRenderer(EntityRendererFactory.Context context, M model, float shadowRadius) {
        this(context, model, model, shadowRadius);
    }

    public BipedEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale) {
        this(context, model, babyModel, scale, HeadFeatureRenderer.HeadTransformation.DEFAULT);
    }

    public BipedEntityRenderer(EntityRendererFactory.Context context, M model, M babyModel, float scale, HeadFeatureRenderer.HeadTransformation headTransformation) {
        super(context, model, babyModel, scale);
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getPlayerSkinCache(), headTransformation));
        this.addFeature((FeatureRenderer)new ElytraFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this));
    }

    protected BipedEntityModel.ArmPose getArmPose(T entity, Arm arm) {
        ItemStack itemStack = entity.getStackInArm(arm);
        SwingAnimationComponent swingAnimationComponent = (SwingAnimationComponent)itemStack.get(DataComponentTypes.SWING_ANIMATION);
        if (swingAnimationComponent != null && swingAnimationComponent.type() == SwingAnimationType.STAB && ((MobEntity)entity).handSwinging) {
            return BipedEntityModel.ArmPose.SPEAR;
        }
        if (itemStack.isIn(ItemTags.SPEARS)) {
            return BipedEntityModel.ArmPose.SPEAR;
        }
        return BipedEntityModel.ArmPose.EMPTY;
    }

    public void updateRenderState(T mobEntity, S bipedEntityRenderState, float f) {
        super.updateRenderState(mobEntity, bipedEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(mobEntity, bipedEntityRenderState, (float)f, (ItemModelManager)this.itemModelResolver);
        ((BipedEntityRenderState)bipedEntityRenderState).leftArmPose = this.getArmPose(mobEntity, Arm.LEFT);
        ((BipedEntityRenderState)bipedEntityRenderState).rightArmPose = this.getArmPose(mobEntity, Arm.RIGHT);
    }

    public static void updateBipedRenderState(LivingEntity entity, BipedEntityRenderState state, float tickProgress, ItemModelManager itemModelResolver) {
        ArmedEntityRenderState.updateRenderState((LivingEntity)entity, (ArmedEntityRenderState)state, (ItemModelManager)itemModelResolver, (float)tickProgress);
        state.isInSneakingPose = entity.isInSneakingPose();
        state.isGliding = entity.isGliding();
        state.isSwimming = entity.isInSwimmingPose();
        state.hasVehicle = entity.hasVehicle();
        state.limbAmplitudeInverse = 1.0f;
        if (state.isGliding) {
            state.limbAmplitudeInverse = (float)entity.getVelocity().lengthSquared();
            state.limbAmplitudeInverse /= 0.2f;
            state.limbAmplitudeInverse *= state.limbAmplitudeInverse * state.limbAmplitudeInverse;
        }
        if (state.limbAmplitudeInverse < 1.0f) {
            state.limbAmplitudeInverse = 1.0f;
        }
        state.leaningPitch = entity.getLeaningPitch(tickProgress);
        state.preferredArm = BipedEntityRenderer.getPreferredArm((LivingEntity)entity);
        state.activeHand = entity.getActiveHand();
        state.crossbowPullTime = CrossbowItem.getPullTime((ItemStack)entity.getActiveItem(), (LivingEntity)entity);
        state.itemUseTime = entity.getItemUseTime(tickProgress);
        state.isUsingItem = entity.isUsingItem();
        state.leftWingPitch = entity.elytraFlightController.leftWingPitch(tickProgress);
        state.leftWingYaw = entity.elytraFlightController.leftWingYaw(tickProgress);
        state.leftWingRoll = entity.elytraFlightController.leftWingRoll(tickProgress);
        state.equippedHeadStack = BipedEntityRenderer.getEquippedStack((LivingEntity)entity, (EquipmentSlot)EquipmentSlot.HEAD);
        state.equippedChestStack = BipedEntityRenderer.getEquippedStack((LivingEntity)entity, (EquipmentSlot)EquipmentSlot.CHEST);
        state.equippedLegsStack = BipedEntityRenderer.getEquippedStack((LivingEntity)entity, (EquipmentSlot)EquipmentSlot.LEGS);
        state.equippedFeetStack = BipedEntityRenderer.getEquippedStack((LivingEntity)entity, (EquipmentSlot)EquipmentSlot.FEET);
    }

    private static ItemStack getEquippedStack(LivingEntity entity, EquipmentSlot slot) {
        ItemStack itemStack = entity.getEquippedStack(slot);
        return ArmorFeatureRenderer.hasModel((ItemStack)itemStack, (EquipmentSlot)slot) ? itemStack.copy() : ItemStack.EMPTY;
    }

    private static Arm getPreferredArm(LivingEntity entity) {
        Arm arm = entity.getMainArm();
        return entity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
    }
}

