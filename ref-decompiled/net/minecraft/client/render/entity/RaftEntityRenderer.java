/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.entity.AbstractBoatEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.RaftEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.model.RaftEntityModel
 *  net.minecraft.client.render.entity.state.BoatEntityRenderState
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.RaftEntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class RaftEntityRenderer
extends AbstractBoatEntityRenderer {
    private final EntityModel<BoatEntityRenderState> model;
    private final Identifier texture;

    public RaftEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer layer) {
        super(context);
        this.texture = layer.id().withPath(path -> "textures/entity/" + path + ".png");
        this.model = new RaftEntityModel(context.getPart(layer));
    }

    protected EntityModel<BoatEntityRenderState> getModel() {
        return this.model;
    }

    protected RenderLayer getRenderLayer() {
        return this.model.getLayer(this.texture);
    }
}

