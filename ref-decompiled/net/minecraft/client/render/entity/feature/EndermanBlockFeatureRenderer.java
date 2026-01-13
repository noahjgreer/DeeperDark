/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.EndermanBlockFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EndermanEntityModel
 *  net.minecraft.client.render.entity.state.EndermanEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.render.entity.state.EndermanEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class EndermanBlockFeatureRenderer
extends FeatureRenderer<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> {
    public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntityRenderState, EndermanEntityModel<EndermanEntityRenderState>> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, EndermanEntityRenderState endermanEntityRenderState, float f, float g) {
        BlockState blockState = endermanEntityRenderState.carriedBlock;
        if (blockState == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0f, 0.6875f, -0.75f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(20.0f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));
        matrixStack.translate(0.25f, 0.1875f, 0.25f);
        float h = 0.5f;
        matrixStack.scale(-0.5f, -0.5f, 0.5f);
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
        orderedRenderCommandQueue.submitBlock(matrixStack, blockState, i, OverlayTexture.DEFAULT_UV, endermanEntityRenderState.outlineColor);
        matrixStack.pop();
    }
}

