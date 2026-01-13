/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.SwingAnimationType;

@Environment(value=EnvType.CLIENT)
public abstract class ZombieBaseEntityRenderer<T extends ZombieEntity, S extends ZombieEntityRenderState, M extends ZombieEntityModel<S>>
extends BipedEntityRenderer<T, S, M> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/zombie.png");

    protected ZombieBaseEntityRenderer(EntityRendererFactory.Context context, M mainModel, M babyMainModel, EquipmentModelData<M> adultModel, EquipmentModelData<M> babyModel) {
        super(context, mainModel, babyMainModel, 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, adultModel, babyModel, context.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(S zombieEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public void updateRenderState(T zombieEntity, S zombieEntityRenderState, float f) {
        super.updateRenderState(zombieEntity, zombieEntityRenderState, f);
        ((ZombieEntityRenderState)zombieEntityRenderState).attacking = ((MobEntity)zombieEntity).isAttacking();
        ((ZombieEntityRenderState)zombieEntityRenderState).convertingInWater = ((ZombieEntity)zombieEntity).isConvertingInWater();
    }

    @Override
    protected boolean isShaking(S zombieEntityRenderState) {
        return super.isShaking(zombieEntityRenderState) || ((ZombieEntityRenderState)zombieEntityRenderState).convertingInWater;
    }

    @Override
    protected BipedEntityModel.ArmPose getArmPose(T zombieEntity, Arm arm) {
        SwingAnimationComponent swingAnimationComponent = ((LivingEntity)zombieEntity).getStackInArm(arm.getOpposite()).get(DataComponentTypes.SWING_ANIMATION);
        if (swingAnimationComponent != null && swingAnimationComponent.type() == SwingAnimationType.STAB) {
            return BipedEntityModel.ArmPose.SPEAR;
        }
        return super.getArmPose(zombieEntity, arm);
    }

    @Override
    protected /* synthetic */ boolean isShaking(LivingEntityRenderState state) {
        return this.isShaking((S)((ZombieEntityRenderState)state));
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((S)((ZombieEntityRenderState)state));
    }
}
