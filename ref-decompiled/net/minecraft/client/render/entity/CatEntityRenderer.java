/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AgeableMobEntityRenderer
 *  net.minecraft.client.render.entity.CatEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.CatEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.CatEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.CatEntity
 *  net.minecraft.entity.passive.CatVariant
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.CatCollarFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.CatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.CatEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class CatEntityRenderer
extends AgeableMobEntityRenderer<CatEntity, CatEntityRenderState, CatEntityModel> {
    public CatEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new CatEntityModel(context.getPart(EntityModelLayers.CAT)), (EntityModel)new CatEntityModel(context.getPart(EntityModelLayers.CAT_BABY)), 0.4f);
        this.addFeature((FeatureRenderer)new CatCollarFeatureRenderer((FeatureRendererContext)this, context.getEntityModels()));
    }

    public Identifier getTexture(CatEntityRenderState catEntityRenderState) {
        return catEntityRenderState.texture;
    }

    public CatEntityRenderState createRenderState() {
        return new CatEntityRenderState();
    }

    public void updateRenderState(CatEntity catEntity, CatEntityRenderState catEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)catEntity, (LivingEntityRenderState)catEntityRenderState, f);
        catEntityRenderState.texture = ((CatVariant)catEntity.getVariant().value()).assetInfo().texturePath();
        catEntityRenderState.inSneakingPose = catEntity.isInSneakingPose();
        catEntityRenderState.sprinting = catEntity.isSprinting();
        catEntityRenderState.inSittingPose = catEntity.isInSittingPose();
        catEntityRenderState.sleepAnimationProgress = catEntity.getSleepAnimationProgress(f);
        catEntityRenderState.tailCurlAnimationProgress = catEntity.getTailCurlAnimationProgress(f);
        catEntityRenderState.headDownAnimationProgress = catEntity.getHeadDownAnimationProgress(f);
        catEntityRenderState.nearSleepingPlayer = catEntity.isNearSleepingPlayer();
        catEntityRenderState.collarColor = catEntity.isTamed() ? catEntity.getCollarColor() : null;
    }

    protected void setupTransforms(CatEntityRenderState catEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)catEntityRenderState, matrixStack, f, g);
        float h = catEntityRenderState.sleepAnimationProgress;
        if (h > 0.0f) {
            matrixStack.translate(0.4f * h, 0.15f * h, 0.1f * h);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerpAngleDegrees((float)h, (float)0.0f, (float)90.0f)));
            if (catEntityRenderState.nearSleepingPlayer) {
                matrixStack.translate(0.15f * h, 0.0f, 0.0f);
            }
        }
    }

    public /* synthetic */ Identifier getTexture(LivingEntityRenderState state) {
        return this.getTexture((CatEntityRenderState)state);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

