/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer
 *  net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer$AnimationAlphaAdjuster
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.ColorHelper
 */
package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.EmissiveFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(value=EnvType.CLIENT)
public class EmissiveFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    private final Function<S, Identifier> textureFunction;
    private final AnimationAlphaAdjuster<S> animationAlphaAdjuster;
    private final M model;
    private final Function<Identifier, RenderLayer> renderLayerFunction;
    private final boolean ignoresInvisibility;

    public EmissiveFeatureRenderer(FeatureRendererContext<S, M> context, Function<S, Identifier> textureFunction, AnimationAlphaAdjuster<S> animationAlphaAdjuster, M model, Function<Identifier, RenderLayer> renderLayerFunction, boolean ignoresInvisibility) {
        super(context);
        this.textureFunction = textureFunction;
        this.animationAlphaAdjuster = animationAlphaAdjuster;
        this.model = model;
        this.renderLayerFunction = renderLayerFunction;
        this.ignoresInvisibility = ignoresInvisibility;
    }

    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g) {
        if (((LivingEntityRenderState)livingEntityRenderState).invisible && !this.ignoresInvisibility) {
            return;
        }
        float h = this.animationAlphaAdjuster.apply(livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).age);
        if (h <= 1.0E-5f) {
            return;
        }
        int j = ColorHelper.getWhite((float)h);
        RenderLayer renderLayer = (RenderLayer)this.renderLayerFunction.apply((Identifier)this.textureFunction.apply(livingEntityRenderState));
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel((Model)this.model, livingEntityRenderState, matrixStack, renderLayer, i, LivingEntityRenderer.getOverlay(livingEntityRenderState, (float)0.0f), j, null, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, null);
    }
}

