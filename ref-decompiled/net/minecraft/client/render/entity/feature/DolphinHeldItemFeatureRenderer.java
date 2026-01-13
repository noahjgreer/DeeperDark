/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.DolphinHeldItemFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.DolphinEntityModel
 *  net.minecraft.client.render.entity.state.DolphinEntityRenderState
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
import net.minecraft.client.render.entity.model.DolphinEntityModel;
import net.minecraft.client.render.entity.state.DolphinEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DolphinHeldItemFeatureRenderer
extends FeatureRenderer<DolphinEntityRenderState, DolphinEntityModel> {
    public DolphinHeldItemFeatureRenderer(FeatureRendererContext<DolphinEntityRenderState, DolphinEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, DolphinEntityRenderState dolphinEntityRenderState, float f, float g) {
        ItemRenderState itemRenderState = dolphinEntityRenderState.itemRenderState;
        if (itemRenderState.isEmpty()) {
            return;
        }
        matrixStack.push();
        float h = 1.0f;
        float j = -1.0f;
        float k = MathHelper.abs((float)dolphinEntityRenderState.pitch) / 60.0f;
        if (dolphinEntityRenderState.pitch < 0.0f) {
            matrixStack.translate(0.0f, 1.0f - k * 0.5f, -1.0f + k * 0.5f);
        } else {
            matrixStack.translate(0.0f, 1.0f + k * 0.8f, -1.0f + k * 0.2f);
        }
        itemRenderState.render(matrixStack, orderedRenderCommandQueue, i, OverlayTexture.DEFAULT_UV, dolphinEntityRenderState.outlineColor);
        matrixStack.pop();
    }
}

