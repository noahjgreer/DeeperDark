/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractMinecartEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MinecartEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.MinecartEntityRenderState
 *  net.minecraft.entity.vehicle.AbstractMinecartEntity
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityRenderer
extends AbstractMinecartEntityRenderer<AbstractMinecartEntity, MinecartEntityRenderState> {
    public MinecartEntityRenderer(EntityRendererFactory.Context context, EntityModelLayer entityModelLayer) {
        super(context, entityModelLayer);
    }

    public MinecartEntityRenderState createRenderState() {
        return new MinecartEntityRenderState();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

