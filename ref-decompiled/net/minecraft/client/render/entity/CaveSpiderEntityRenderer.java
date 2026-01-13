/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.CaveSpiderEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.SpiderEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.mob.CaveSpiderEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class CaveSpiderEntityRenderer
extends SpiderEntityRenderer<CaveSpiderEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/spider/cave_spider.png");

    public CaveSpiderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.CAVE_SPIDER);
        this.shadowRadius = 0.56f;
    }

    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }
}

