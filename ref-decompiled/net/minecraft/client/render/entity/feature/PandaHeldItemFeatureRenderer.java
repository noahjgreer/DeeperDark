/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.PandaHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.model.PandaEntityModel
 *  net.minecraft.client.render.entity.state.PandaEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PandaEntityModel;
import net.minecraft.client.render.entity.state.PandaEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PandaHeldItemFeatureRenderer
extends FeatureRenderer<PandaEntityRenderState, PandaEntityModel> {
    public PandaHeldItemFeatureRenderer(FeatureRendererContext<PandaEntityRenderState, PandaEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PandaEntityRenderState pandaEntityRenderState, float f, float g) {
        ItemRenderState itemRenderState = pandaEntityRenderState.itemRenderState;
        if (itemRenderState.isEmpty() || !pandaEntityRenderState.sitting || pandaEntityRenderState.scaredByThunderstorm) {
            return;
        }
        float h = -0.6f;
        float j = 1.4f;
        if (pandaEntityRenderState.eating) {
            h -= 0.2f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.6f)) + 0.2f;
            j -= 0.09f * MathHelper.sin((double)(pandaEntityRenderState.age * 0.6f));
        }
        matrixStack.push();
        matrixStack.translate(0.1f, j, h);
        itemRenderState.render(matrixStack, orderedRenderCommandQueue, i, OverlayTexture.DEFAULT_UV, pandaEntityRenderState.outlineColor);
        matrixStack.pop();
    }
}

