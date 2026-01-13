/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.ArmorStandEntityRenderer
 *  net.minecraft.client.render.entity.BipedEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.ElytraFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel
 *  net.minecraft.client.render.entity.model.ArmorStandEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.EquipmentModelData
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.ArmorStandEntityRenderState
 *  net.minecraft.client.render.entity.state.BipedEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.client.render.entity.model.ArmorStandEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EquipmentModelData;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityRenderer
extends LivingEntityRenderer<ArmorStandEntity, ArmorStandEntityRenderState, ArmorStandArmorEntityModel> {
    public static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/armorstand/wood.png");
    private final ArmorStandArmorEntityModel mainModel = (ArmorStandArmorEntityModel)this.getModel();
    private final ArmorStandArmorEntityModel smallModel;

    public ArmorStandEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND)), 0.0f);
        this.smallModel = new ArmorStandEntityModel(context.getPart(EntityModelLayers.ARMOR_STAND_SMALL));
        this.addFeature((FeatureRenderer)new ArmorFeatureRenderer((FeatureRendererContext)this, EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.ARMOR_STAND_EQUIPMENT, (LoadedEntityModels)context.getEntityModels(), ArmorStandArmorEntityModel::new), EquipmentModelData.mapToEntityModel((EquipmentModelData)EntityModelLayers.SMALL_ARMOR_STAND_EQUIPMENT, (LoadedEntityModels)context.getEntityModels(), ArmorStandArmorEntityModel::new), context.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this));
        this.addFeature((FeatureRenderer)new ElytraFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getPlayerSkinCache()));
    }

    public Identifier getTexture(ArmorStandEntityRenderState armorStandEntityRenderState) {
        return TEXTURE;
    }

    public ArmorStandEntityRenderState createRenderState() {
        return new ArmorStandEntityRenderState();
    }

    public void updateRenderState(ArmorStandEntity armorStandEntity, ArmorStandEntityRenderState armorStandEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)armorStandEntity, (LivingEntityRenderState)armorStandEntityRenderState, f);
        BipedEntityRenderer.updateBipedRenderState((LivingEntity)armorStandEntity, (BipedEntityRenderState)armorStandEntityRenderState, (float)f, (ItemModelManager)this.itemModelResolver);
        armorStandEntityRenderState.yaw = MathHelper.lerpAngleDegrees((float)f, (float)armorStandEntity.lastYaw, (float)armorStandEntity.getYaw());
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

    public void render(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        this.model = armorStandEntityRenderState.small ? this.smallModel : this.mainModel;
        super.render((LivingEntityRenderState)armorStandEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    protected void setupTransforms(ArmorStandEntityRenderState armorStandEntityRenderState, MatrixStack matrixStack, float f, float g) {
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f));
        if (armorStandEntityRenderState.timeSinceLastHit < 5.0f) {
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin((double)(armorStandEntityRenderState.timeSinceLastHit / 1.5f * (float)Math.PI)) * 3.0f));
        }
    }

    protected boolean hasLabel(ArmorStandEntity armorStandEntity, double d) {
        return armorStandEntity.isCustomNameVisible();
    }

    protected @Nullable RenderLayer getRenderLayer(ArmorStandEntityRenderState armorStandEntityRenderState, boolean bl, boolean bl2, boolean bl3) {
        if (!armorStandEntityRenderState.marker) {
            return super.getRenderLayer((LivingEntityRenderState)armorStandEntityRenderState, bl, bl2, bl3);
        }
        Identifier identifier = this.getTexture(armorStandEntityRenderState);
        if (bl2) {
            return RenderLayers.entityTranslucent((Identifier)identifier, (boolean)false);
        }
        if (bl) {
            return RenderLayers.entityCutoutNoCull((Identifier)identifier, (boolean)false);
        }
        return null;
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((ArmorStandEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

