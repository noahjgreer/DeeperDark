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
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.state.IllusionerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
class IllusionerEntityRenderer.1
extends HeldItemFeatureRenderer<IllusionerEntityRenderState, IllagerEntityModel<IllusionerEntityRenderState>> {
    IllusionerEntityRenderer.1(IllusionerEntityRenderer illusionerEntityRenderer, FeatureRendererContext featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, IllusionerEntityRenderState illusionerEntityRenderState, float f, float g) {
        if (illusionerEntityRenderState.spellcasting || illusionerEntityRenderState.attacking) {
            super.render(matrixStack, orderedRenderCommandQueue, i, illusionerEntityRenderState, f, g);
        }
    }
}
