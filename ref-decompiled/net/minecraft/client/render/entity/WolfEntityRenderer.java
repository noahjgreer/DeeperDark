/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.WolfEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.WolfEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.WolfEntityRenderState
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.WolfEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.WolfArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class WolfEntityRenderer
extends AgeableMobEntityRenderer<WolfEntity, WolfEntityRenderState, WolfEntityModel> {
    public WolfEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new WolfEntityModel(context.getPart(EntityModelLayers.WOLF)), (EntityModel)new WolfEntityModel(context.getPart(EntityModelLayers.WOLF_BABY)), 0.5f);
        this.addFeature((FeatureRenderer)new WolfArmorFeatureRenderer((FeatureRendererContext)this, context.getEntityModels(), context.getEquipmentRenderer()));
        this.addFeature((FeatureRenderer)new WolfCollarFeatureRenderer((FeatureRendererContext)this));
    }

    protected int getMixColor(WolfEntityRenderState wolfEntityRenderState) {
        float f = wolfEntityRenderState.furWetBrightnessMultiplier;
        if (f == 1.0f) {
            return -1;
        }
        return ColorHelper.fromFloats((float)1.0f, (float)f, (float)f, (float)f);
    }

    public Identifier getTexture(WolfEntityRenderState wolfEntityRenderState) {
        return wolfEntityRenderState.texture;
    }

    public WolfEntityRenderState createRenderState() {
        return new WolfEntityRenderState();
    }

    public void updateRenderState(WolfEntity wolfEntity, WolfEntityRenderState wolfEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)wolfEntity, (LivingEntityRenderState)wolfEntityRenderState, f);
        wolfEntityRenderState.angerTime = wolfEntity.hasAngerTime();
        wolfEntityRenderState.inSittingPose = wolfEntity.isInSittingPose();
        wolfEntityRenderState.tailAngle = wolfEntity.getTailAngle();
        wolfEntityRenderState.begAnimationProgress = wolfEntity.getBegAnimationProgress(f);
        wolfEntityRenderState.shakeProgress = wolfEntity.getShakeProgress(f);
        wolfEntityRenderState.texture = wolfEntity.getTextureId();
        wolfEntityRenderState.furWetBrightnessMultiplier = wolfEntity.getFurWetBrightnessMultiplier(f);
        wolfEntityRenderState.collarColor = wolfEntity.isTamed() ? wolfEntity.getCollarColor() : null;
        wolfEntityRenderState.bodyArmor = wolfEntity.getBodyArmor().copy();
    }

    protected /* synthetic */ int getMixColor(LivingEntityRenderState state) {
        return this.getMixColor((WolfEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

