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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/armorstand/wood.png");
    private final ArmorStandArmorEntityModel mainModel = (ArmorStandArmorEntityModel)this.getModel();
    private final ArmorStandArmorEntityModel smallModel;

    public ArmorStandEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND)), 0.0f);
        this.smallModel = new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL));
        this.addFeature(new ArmorFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel, ArmorStandArmorEntityModel>(this, EquipmentModelData.mapToEntityModel(EntityModelLayers.ARMOR_STAND_EQUIPMENT, context.getEntityModels(), ArmorStandArmorEntityModel::new), EquipmentModelData.mapToEntityModel(EntityModelLayers.SMALL_ARMOR_STAND_EQUIPMENT, context.getEntityModels(), ArmorStandArmorEntityModel::new), context.getEquipmentRenderer()));
        this.addFeature(new HeldItemFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this));
        this.addFeature(new ElytraFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature(new HeadFeatureRenderer<ArmorStandEntityRenderState, ArmorStandArmorEntityModel>(this, context.getEntityModels(), context.getPlayerSkinCache()));
    }

    @Override
    public Identifier getTexture(ArmorStandEntityRenderState armorStandEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public ArmorStandEntityRenderState createRenderState() {
        return new ArmorStandEntityRenderState();
    }

    @Override
    public void updateRenderState(ArmorStandEntity armorStandEntity, ArmorStandEntityRenderState armorStandEntityRenderState, float f) {
        super.updateRenderState(armorStandEntity, armorStandEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState(armorStandEntity, armorStandEntityRenderState, f, this.itemModelResolver);
        armorStandEntityRenderState.yaw = MathHelper.lerpAngleDegrees(f, armorStandEntity.lastYaw, armorStandEntity.getYaw());
        armorStandEntityRenderState.marker = armorStandEntity.isMarker();
        armorStandEntityRenderState.small = armorStandEntity.isSmall();
        armorStandEntityRenderState.showArms = armorStandEntity.shouldShowArms();
        armorStandEntityRenderState.showBasePlate = armorStandEntity.shouldShowBasePlate();
        armorStandEntityRenderState.bodyRotation = armorStandEntity.getBodyRotation();
        armorStandEntityRenderState.headRotation = armorStandEntity.getHeadRotation();
        armorStandEntityRenderState.leftArmRotation = armorStandEntity.getLeftArmRotation();
        armorStandEntityRenderState.rightArmRotation = armorStandEntity.getRightArmRotation();
        armorStandEntityRenderState.leftLegRotation = armorStandEntity.getLeftLegRotation();
        armorStandEntityRenderState.rightLegRotation = armorStandEntity.getRightLegRotation();
        armorStandEntityRenderState.timeSinceLastHit = (float)(armorStandEntity.getEntityWorld().getTime() - armorStandEntity.lastHitTime) + f;
    }

    @Override
    public void render(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        this.model = armorStandEntityRenderState.small ? this.smallModel : this.mainModel;
        super.render(armorStandEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    @Override
    protected void setupTransforms(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, float f, float g) {
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        if (armorStandEntityRenderState.timeSinceLastHit < 5.0f) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(armorStandEntityRenderState.timeSinceLastHit / 1.5f * (float)Math.PI) * 3.0f));
        }
    }

    @Override
    protected boolean hasLabel(ArmorStandEntity armorStandEntity, double d) {
        return armorStandEntity.isCustomNameVisible();
    }

    @Override
    protected @Nullable RenderLayer getRenderLayer(ArmorStandEntityRenderState armorStandEntityRenderState, boolean bl, boolean bl2, boolean bl3) {
        if (!armorStandEntityRenderState.marker) {
            return super.getRenderLayer(armorStandEntityRenderState, bl, bl2, bl3);
        }
        Identifier identifier = this.getTexture(armorStandEntityRenderState);
        if (bl2) {
            return RenderLayers.entityTranslucent(identifier, false);
        }
        if (bl) {
            return RenderLayers.entityCutoutNoCull(identifier, false);
        }
        return null;
    }

    @Override
    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ArmorStandEntityRenderState)state);
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
