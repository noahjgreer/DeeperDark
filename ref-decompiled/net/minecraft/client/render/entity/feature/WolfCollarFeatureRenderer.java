/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer
 *  net.minecraft.client.render.entity.model.WolfEntityModel
 *  net.minecraft.client.render.entity.state.WolfEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WolfCollarFeatureRenderer
extends FeatureRenderer<WolfEntityRenderState, WolfEntityModel> {
    private static final Identifier SKIN = Identifier.ofVanilla((String)"textures/entity/wolf/wolf_collar.png");

    public WolfCollarFeatureRenderer(FeatureRendererContext<WolfEntityRenderState, WolfEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, WolfEntityRenderState wolfEntityRenderState, float f, float g) {
        DyeColor dyeColor = wolfEntityRenderState.collarColor;
        if (dyeColor == null || wolfEntityRenderState.invisible) {
            return;
        }
        int j = dyeColor.getEntityColor();
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.getContextModel(), (Object)wolfEntityRenderState, matrixStack, RenderLayers.entityCutoutNoCull((Identifier)SKIN), i, OverlayTexture.DEFAULT_UV, j, null, wolfEntityRenderState.outlineColor, null);
    }
}

