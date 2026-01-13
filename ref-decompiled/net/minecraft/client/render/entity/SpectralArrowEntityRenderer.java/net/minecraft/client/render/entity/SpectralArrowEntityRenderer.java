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
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class SpectralArrowEntityRenderer
extends ProjectileEntityRenderer<SpectralArrowEntity, ProjectileEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/projectiles/spectral_arrow.png");

    public SpectralArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected Identifier getTexture(ProjectileEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }

    @Override
    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
