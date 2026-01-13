/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.ArrowEntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.ProjectileEntityRenderer
 *  net.minecraft.client.render.entity.state.ArrowEntityRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ProjectileEntityRenderState
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class ArrowEntityRenderer
extends ProjectileEntityRenderer<ArrowEntity, ArrowEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/entity/projectiles/arrow.png");
    public static final Identifier TIPPED_TEXTURE = Identifier.ofVanilla((String)"textures/entity/projectiles/tipped_arrow.png");

    public ArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    protected Identifier getTexture(ArrowEntityRenderState arrowEntityRenderState) {
        return arrowEntityRenderState.tipped ? TIPPED_TEXTURE : TEXTURE;
    }

    public ArrowEntityRenderState createRenderState() {
        return new ArrowEntityRenderState();
    }

    public void updateRenderState(ArrowEntity arrowEntity, ArrowEntityRenderState arrowEntityRenderState, float f) {
        super.updateRenderState((PersistentProjectileEntity)arrowEntity, (ProjectileEntityRenderState)arrowEntityRenderState, f);
        arrowEntityRenderState.tipped = arrowEntity.getColor() > 0;
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

