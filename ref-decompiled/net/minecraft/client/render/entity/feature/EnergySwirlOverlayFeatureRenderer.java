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
 *  net.minecraft.client.render.entity.feature.EnergySwirlOverlayFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
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
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public abstract class EnergySwirlOverlayFeatureRenderer<S extends EntityRenderState, M extends EntityModel<S>>
extends FeatureRenderer<S, M> {
    public EnergySwirlOverlayFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext) {
        super(featureRendererContext);
    }

    public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, S state, float limbAngle, float limbDistance) {
        if (!this.shouldRender(state)) {
            return;
        }
        float f = ((EntityRenderState)state).age;
        EntityModel entityModel = this.getEnergySwirlModel();
        queue.getBatchingQueue(1).submitModel((Model)entityModel, state, matrices, RenderLayers.energySwirl((Identifier)this.getEnergySwirlTexture(), (float)(this.getEnergySwirlX(f) % 1.0f), (float)(f * 0.01f % 1.0f)), light, OverlayTexture.DEFAULT_UV, -8355712, null, ((EntityRenderState)state).outlineColor, null);
    }

    protected abstract boolean shouldRender(S var1);

    protected abstract float getEnergySwirlX(float var1);

    protected abstract Identifier getEnergySwirlTexture();

    protected abstract M getEnergySwirlModel();
}

