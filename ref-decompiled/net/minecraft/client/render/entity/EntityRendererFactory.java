/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.entity.Entity
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface EntityRendererFactory<T extends Entity> {
    public EntityRenderer<T, ?> create(Context var1);
}

