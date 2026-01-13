/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EvokerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.EvokerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
class EvokerEntityRenderer.1
extends HeldItemFeatureRenderer<EvokerEntityRenderState, IllagerEntityModel<EvokerEntityRenderState>> {
    EvokerEntityRenderer.1(EvokerEntityRenderer evokerEntityRenderer, FeatureRendererContext featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, EvokerEntityRenderState evokerEntityRenderState, float f, float g) {
        if (evokerEntityRenderState.spellcasting) {
            super.render(matrixStack, orderedRenderCommandQueue, i, evokerEntityRenderState, f, g);
        }
    }
}
