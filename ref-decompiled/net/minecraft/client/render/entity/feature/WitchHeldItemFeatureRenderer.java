/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.feature.WitchHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.WitchEntityModel
 *  net.minecraft.client.render.entity.state.ItemHolderEntityRenderState
 *  net.minecraft.client.render.entity.state.WitchEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.VillagerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.ItemHolderEntityRenderState;
import net.minecraft.client.render.entity.state.WitchEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class WitchHeldItemFeatureRenderer
extends VillagerHeldItemFeatureRenderer<WitchEntityRenderState, WitchEntityModel> {
    public WitchHeldItemFeatureRenderer(FeatureRendererContext<WitchEntityRenderState, WitchEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    protected void applyTransforms(WitchEntityRenderState witchEntityRenderState, MatrixStack matrixStack) {
        if (witchEntityRenderState.holdingPotion) {
            ((WitchEntityModel)this.getContextModel()).getRootPart().applyTransform(matrixStack);
            ((WitchEntityModel)this.getContextModel()).applyTransform(matrixStack);
            ((WitchEntityModel)this.getContextModel()).getNose().applyTransform(matrixStack);
            matrixStack.translate(0.0625f, 0.25f, 0.0f);
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(140.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(10.0f));
            matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
            return;
        }
        super.applyTransforms((ItemHolderEntityRenderState)witchEntityRenderState, matrixStack);
    }
}

