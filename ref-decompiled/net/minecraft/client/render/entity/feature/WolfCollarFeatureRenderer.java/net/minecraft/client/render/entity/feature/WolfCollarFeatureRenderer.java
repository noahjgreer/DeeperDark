/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WolfCollarFeatureRenderer
extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla("textures/entity/wolf/wolf_collar.png");

    public WolfCollarFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, WolfEntityRenderState wolfEntityRenderState, float f, float g) {
        DyeColor dyeColor = wolfEntityRenderState.collarColor;
        if (dyeColor == null || wolfEntityRenderState.invisible) {
            return;
        }
        int j = dyeColor.getEntityColor();
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel(this.getContextModel(), wolfEntityRenderState, matrixStack, RenderLayers.entityCutoutNoCull(SKIN), i, OverlayTexture.DEFAULT_UV, j, (Sprite)null, wolfEntityRenderState.outlineColor, (ModelCommandRenderer.CrumblingOverlayCommand)null);
    }
}
