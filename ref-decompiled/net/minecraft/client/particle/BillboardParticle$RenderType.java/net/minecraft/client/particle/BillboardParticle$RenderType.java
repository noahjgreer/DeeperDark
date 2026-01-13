/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record BillboardParticle.RenderType(boolean translucent, Identifier textureAtlasLocation, RenderPipeline pipeline) {
    public static final BillboardParticle.RenderType BLOCK_ATLAS_TRANSLUCENT = new BillboardParticle.RenderType(true, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, RenderPipelines.TRANSLUCENT_PARTICLE);
    public static final BillboardParticle.RenderType ITEM_ATLAS_TRANSLUCENT = new BillboardParticle.RenderType(true, SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE, RenderPipelines.TRANSLUCENT_PARTICLE);
    public static final BillboardParticle.RenderType PARTICLE_ATLAS_OPAQUE = new BillboardParticle.RenderType(false, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, RenderPipelines.OPAQUE_PARTICLE);
    public static final BillboardParticle.RenderType PARTICLE_ATLAS_TRANSLUCENT = new BillboardParticle.RenderType(true, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE, RenderPipelines.TRANSLUCENT_PARTICLE);
}
