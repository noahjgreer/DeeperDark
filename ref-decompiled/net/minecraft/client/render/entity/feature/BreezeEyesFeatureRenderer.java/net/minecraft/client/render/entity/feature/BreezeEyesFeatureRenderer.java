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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BreezeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.BreezeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BreezeEyesFeatureRenderer
extends FeatureRenderer<BreezeEntityRenderState, BreezeEntityModel> {
    private static final RenderLayer TEXTURE = RenderLayers.entityTranslucentEmissiveNoOutline(Identifier.ofVanilla("textures/entity/breeze/breeze_eyes.png"));
    private final BreezeEntityModel breezeModel;

    public BreezeEyesFeatureRenderer(FeatureRendererContext<BreezeEntityRenderState, BreezeEntityModel> context, LoadedEntityModels entityModels) {
        super(context);
        this.breezeModel = new BreezeEntityModel(entityModels.getModelPart(EntityModelLayers.BREEZE_EYES));
    }

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, BreezeEntityRenderState breezeEntityRenderState, float f, float g) {
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel(this.breezeModel, breezeEntityRenderState, matrixStack, TEXTURE, i, OverlayTexture.DEFAULT_UV, -1, null, breezeEntityRenderState.outlineColor, null);
    }
}
