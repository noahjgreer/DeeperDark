/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.feature.BreezeWindFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.BreezeEntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.BreezeEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
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
public class BreezeWindFeatureRenderer
extends FeatureRenderer<BreezeEntityRenderState, BreezeEntityModel> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/breeze/breeze_wind.png");
    private final BreezeEntityModel model;

    public BreezeWindFeatureRenderer(FeatureRendererContext<BreezeEntityRenderState, BreezeEntityModel> context, LoadedEntityModels entityModels) {
        super(context);
        this.model = new BreezeEntityModel(entityModels.getModelPart(EntityModelLayers.BREEZE_WIND));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, BreezeEntityRenderState breezeEntityRenderState, float f, float g) {
        RenderLayer renderLayer = RenderLayers.breezeWind((Identifier)TEXTURE, (float)(this.getXOffset(breezeEntityRenderState.age) % 1.0f), (float)0.0f);
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.model, (Object)breezeEntityRenderState, matrixStack, renderLayer, i, OverlayTexture.DEFAULT_UV, -1, null, breezeEntityRenderState.outlineColor, null);
    }

    private float getXOffset(float tickProgress) {
        return tickProgress * 0.02f;
    }
}

