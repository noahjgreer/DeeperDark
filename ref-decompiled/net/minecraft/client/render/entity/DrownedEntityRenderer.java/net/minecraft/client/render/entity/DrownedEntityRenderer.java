/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class DrownedEntityRenderer
extends ZombieBaseEntityRenderer<DrownedEntity, ZombieEntityRenderState, DrownedEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/zombie/drowned.png");

    public DrownedEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED)), new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_BABY)), EquipmentModelData.mapToEntityModel(EntityModelLayers.DROWNED_EQUIPMENT, context.getEntityModels(), DrownedEntityModel::new), EquipmentModelData.mapToEntityModel(EntityModelLayers.DROWNED_BABY_EQUIPMENT, context.getEntityModels(), DrownedEntityModel::new));
        this.addFeature(new DrownedOverlayFeatureRenderer(this, context.getEntityModels()));
    }

    @Override
    public ZombieEntityRenderState createRenderState() {
        return new ZombieEntityRenderState();
    }

    @Override
    public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(ZombieEntityRenderState zombieEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms(zombieEntityRenderState, matrixStack, f, g);
        float h = zombieEntityRenderState.leaningPitch;
        if (h > 0.0f) {
            float i = -10.0f - zombieEntityRenderState.pitch;
            float j = MathHelper.lerp(h, 0.0f, i);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(j), 0.0f, zombieEntityRenderState.height / 2.0f / g, 0.0f);
        }
    }

    @Override
    protected BipedEntityModel.ArmPose getArmPose(DrownedEntity drownedEntity, Arm arm) {
        ItemStack itemStack = drownedEntity.getStackInArm(arm);
        if (drownedEntity.getMainArm() == arm && drownedEntity.isAttacking() && itemStack.isOf(Items.TRIDENT)) {
            return BipedEntityModel.ArmPose.THROW_TRIDENT;
        }
        return super.getArmPose(drownedEntity, arm);
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
