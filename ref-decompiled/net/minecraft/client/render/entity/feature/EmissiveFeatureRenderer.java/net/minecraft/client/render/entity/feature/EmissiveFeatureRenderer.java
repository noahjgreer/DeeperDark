/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.feature;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.texture.Sprite;
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

    @Override
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, S livingEntityRenderState, float f, float g) {
        if (((LivingEntityRenderState)livingEntityRenderState).invisible && !this.ignoresInvisibility) {
            return;
        }
        float h = this.animationAlphaAdjuster.apply(livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).age);
        if (h <= 1.0E-5f) {
            return;
        }
        int j = ColorHelper.getWhite(h);
        RenderLayer renderLayer = this.renderLayerFunction.apply(this.textureFunction.apply(livingEntityRenderState));
        orderedRenderCommandQueue.getBatchingQueue(1).submitModel(this.model, livingEntityRenderState, matrixStack, renderLayer, i, LivingEntityRenderer.getOverlay(livingEntityRenderState, 0.0f), j, (Sprite)null, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, (ModelCommandRenderer.CrumblingOverlayCommand)null);
    }

    @Environment(value=EnvType.CLIENT)
    public static interface AnimationAlphaAdjuster<S extends LivingEntityRenderState> {
        public float apply(S var1, float var2);
    }
}
