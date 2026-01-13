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
import net.minecraft.client.render.entity.VindicatorEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllagerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
class VindicatorEntityRenderer.1
extends HeldItemFeatureRenderer<IllagerEntityRenderState, IllagerEntityModel<IllagerEntityRenderState>> {
    VindicatorEntityRenderer.1(VindicatorEntityRenderer vindicatorEntityRenderer, FeatureRendererContext featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, IllagerEntityRenderState illagerEntityRenderState, float f, float g) {
        if (illagerEntityRenderState.attacking) {
            super.render(matrixStack, orderedRenderCommandQueue, i, illagerEntityRenderState, f, g);
        }
    }
}
