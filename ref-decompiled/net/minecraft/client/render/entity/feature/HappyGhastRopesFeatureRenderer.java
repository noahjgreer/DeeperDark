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
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.HappyGhastRopesFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.HappyGhastEntityModel
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.state.HappyGhastEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.registry.tag.ItemTags
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
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.HappyGhastEntityModel;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.HappyGhastEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class HappyGhastRopesFeatureRenderer<M extends HappyGhastEntityModel>
extends FeatureRenderer<HappyGhastEntityRenderState, M> {
    private final RenderLayer renderLayer;
    private final HappyGhastEntityModel model;
    private final HappyGhastEntityModel babyModel;

    public HappyGhastRopesFeatureRenderer(FeatureRendererContext<HappyGhastEntityRenderState, M> context, LoadedEntityModels loader, Identifier texture) {
        super(context);
        this.renderLayer = RenderLayers.entityCutoutNoCull((Identifier)texture);
        this.model = new HappyGhastEntityModel(loader.getModelPart(EntityModelLayers.HAPPY_GHAST_ROPES));
        this.babyModel = new HappyGhastEntityModel(loader.getModelPart(EntityModelLayers.HAPPY_GHAST_BABY_ROPES));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, HappyGhastEntityRenderState happyGhastEntityRenderState, float f, float g) {
        if (!happyGhastEntityRenderState.hasRopes || !happyGhastEntityRenderState.harnessStack.isIn(ItemTags.HARNESSES)) {
            return;
        }
        HappyGhastEntityModel happyGhastEntityModel = happyGhastEntityRenderState.baby ? this.babyModel : this.model;
        orderedRenderCommandQueue.submitModel((Model)happyGhastEntityModel, (Object)happyGhastEntityRenderState, matrixStack, this.renderLayer, i, OverlayTexture.DEFAULT_UV, happyGhastEntityRenderState.outlineColor, null);
    }
}

