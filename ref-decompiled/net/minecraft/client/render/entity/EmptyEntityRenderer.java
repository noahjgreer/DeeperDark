/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EmptyEntityRenderer
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.entity.Entity
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class EmptyEntityRenderer<T extends Entity>
extends EntityRenderer<T, EntityRenderState> {
    public EmptyEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }
}

