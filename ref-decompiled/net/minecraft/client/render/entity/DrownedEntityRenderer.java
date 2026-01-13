/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.DrownedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ZombieBaseEntityRenderer
 *  net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.model.DrownedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.ZombieEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ZombieEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.mob.DrownedEntity
 *  net.minecraft.entity.mob.ZombieEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieBaseEntityRenderer;
import net.minecraft.client.render.entity.feature.DrownedOverlayFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ZombieEntity;
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
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/zombie/drowned.png");

    public DrownedEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (ZombieEntityModel)new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED)), (ZombieEntityModel)new DrownedEntityModel(context.getPart(EntityModelLayers.DROWNED_BABY)), EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.DROWNED_EQUIPMENT, (LoadedEntityModels)context.getEntityModels(), DrownedEntityModel::new), EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.DROWNED_BABY_EQUIPMENT, (LoadedEntityModels)context.getEntityModels(), DrownedEntityModel::new));
        this.addFeature((FeatureRenderer)new DrownedOverlayFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    public ZombieEntityRenderState createRenderState() {
        return new ZombieEntityRenderState();
    }

    public Identifier getTexture(ZombieEntityRenderState zombieEntityRenderState) {
        return TEXTURE;
    }

    protected void setupTransforms(ZombieEntityRenderState zombieEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)zombieEntityRenderState, matrixStack, f, g);
        float h = zombieEntityRenderState.leaningPitch;
        if (h > 0.0f) {
            float i = -10.0f - zombieEntityRenderState.pitch;
            float j = MathHelper.lerp((float)h, (float)0.0f, (float)i);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(j), 0.0f, zombieEntityRenderState.height / 2.0f / g, 0.0f);
        }
    }

    protected BipedEntityModel.ArmPose getArmPose(DrownedEntity drownedEntity, Arm arm) {
        ItemStack itemStack = drownedEntity.getStackInArm(arm);
        if (drownedEntity.getMainArm() == arm && drownedEntity.isAttacking() && itemStack.isOf(Items.TRIDENT)) {
            return BipedEntityModel.ArmPose.THROW_TRIDENT;
        }
        return super.getArmPose((ZombieEntity)drownedEntity, arm);
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ZombieEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

