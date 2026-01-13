/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.SlimeEntityRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.SlimeOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.SlimeEntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.SlimeEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.SlimeEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.SlimeEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer
extends FeatureRenderer<SlimeEntityRenderState, SlimeEntityModel> {
    private final SlimeEntityModel model;

    public SlimeOverlayFeatureRenderer(FeatureRendererContext<SlimeEntityRenderState, SlimeEntityModel> context, LoadedEntityModels loader) {
        super(context);
        this.model = new SlimeEntityModel(loader.getModelPart(EntityModelLayers.SLIME_OUTER));
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, SlimeEntityRenderState slimeEntityRenderState, float f, float g) {
        boolean bl;
        boolean bl2 = bl = slimeEntityRenderState.hasOutline() && slimeEntityRenderState.invisible;
        if (slimeEntityRenderState.invisible && !bl) {
            return;
        }
        int j = LivingEntityRenderer.getOverlay((LivingEntityRenderState)slimeEntityRenderState, (float)0.0f);
        if (bl) {
            orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.model, (Object)slimeEntityRenderState, matrixStack, RenderLayers.outlineNoCull((Identifier)SlimeEntityRenderer.TEXTURE), i, j, -1, null, slimeEntityRenderState.outlineColor, null);
        } else {
            orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.model, (Object)slimeEntityRenderState, matrixStack, RenderLayers.entityTranslucent((Identifier)SlimeEntityRenderer.TEXTURE), i, j, -1, null, slimeEntityRenderState.outlineColor, null);
        }
    }
}

