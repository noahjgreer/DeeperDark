/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/skeleton/wither_skeleton.png");

    public WitherSkeletonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.WITHER_SKELETON, EntityModelLayers.WITHER_SKELETON_EQUIPMENT);
    }

    @Override
    public Identifier getTexture(SkeletonEntityRenderState skeletonEntityRenderState) {
        return TEXTURE;
    }

    @Override
    public SkeletonEntityRenderState createRenderState() {
        return new SkeletonEntityRenderState();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
