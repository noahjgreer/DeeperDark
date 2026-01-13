/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.WitherSkeletonEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.SkeletonEntityRenderState
 *  net.minecraft.entity.mob.WitherSkeletonEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.AbstractSkeletonEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.SkeletonEntityRenderState;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class WitherSkeletonEntityRenderer
extends AbstractSkeletonEntityRenderer<WitherSkeletonEntity, SkeletonEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/skeleton/wither_skeleton.png");

    public WitherSkeletonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_EQUIPMENT);
    }

    public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
        return TEXTURE;
    }

    public SkeletonEntityRenderState createRenderState() {
        return new SkeletonEntityRenderState();
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

