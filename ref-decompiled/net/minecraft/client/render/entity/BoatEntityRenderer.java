/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.AbstractBoatEntityRenderer
 *  net.minecraft.client.render.entity.BoatEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.BoatEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.BoatEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Unit
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BoatEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

@Environment(value=EnvType.CLIENT)
public class BoatEntityRenderer
extends AbstractBoatEntityRenderer {
    private final Model.SinglePartModel waterMaskModel;
    private final Identifier texture;
    private final EntityModel<BoatEntityRenderState> model;

    public BoatEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
        super(ctx);
        this.texture = layer.id().withPath(path -> "textures/entity/" + path + ".png");
        this.waterMaskModel = new Model.SinglePartModel(ctx.getPart(EntityModelLayers.BOAT), id -> RenderLayers.waterMask());
        this.model = new BoatEntityModel(ctx.getPart(layer));
    }

    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.model;
    }

    protected RenderLayer getRenderLayer() {
        return this.model.getLayer(this.texture);
    }

    protected void renderWaterMask(BoatEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, int light) {
        if (!state.submergedInWater) {
            queue.submitModel((Model)this.waterMaskModel, (Object)Unit.INSTANCE, matrices, this.waterMaskModel.getLayer(this.texture), light, OverlayTexture.DEFAULT_UV, state.outlineColor, null);
        }
    }
}

