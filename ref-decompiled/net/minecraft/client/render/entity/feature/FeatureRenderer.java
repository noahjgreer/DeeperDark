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
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
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
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class FeatureRenderer<S extends EntityRenderState, M extends EntityModel<? super S>> {
    private final FeatureRendererContext<S, M> context;

    public FeatureRenderer(FeatureRendererContext<S, M> context) {
        this.context = context;
    }

    protected static <S extends LivingEntityRenderState> void render(Model<? super S> model, Identifier texture, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, int color, int queueOrder) {
        if (!state.invisible) {
            FeatureRenderer.renderModel(model, (Identifier)texture, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, state, (int)color, (int)queueOrder);
        }
    }

    protected static <S extends LivingEntityRenderState> void renderModel(Model<? super S> model, Identifier texture, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, int color, int queueOrder) {
        queue.getBatchingQueue(queueOrder).submitModel(model, state, matrices, RenderLayers.entityCutoutNoCull((Identifier)texture), light, LivingEntityRenderer.getOverlay(state, (float)0.0f), color, null, state.outlineColor, null);
    }

    public M getContextModel() {
        return (M)this.context.getModel();
    }

    public abstract void render(MatrixStack var1, OrderedRenderCommandQueue var2, int var3, S var4, float var5, float var6);
}

