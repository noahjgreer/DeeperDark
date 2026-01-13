/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class BlockMarkerParticle
extends BillboardParticle {
    private final BillboardParticle.RenderType renderType;

    BlockMarkerParticle(ClientWorld world, double x, double y, double z, BlockState state) {
        super(world, x, y, z, MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
        this.gravityStrength = 0.0f;
        this.maxAge = 80;
        this.collidesWithWorld = false;
        this.renderType = this.sprite.getAtlasId().equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE) ? BillboardParticle.RenderType.BLOCK_ATLAS_TRANSLUCENT : BillboardParticle.RenderType.ITEM_ATLAS_TRANSLUCENT;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return this.renderType;
    }

    @Override
    public float getSize(float tickProgress) {
        return 0.5f;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        @Override
        public Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return new BlockMarkerParticle(clientWorld, d, e, f, blockStateParticleEffect.getBlockState());
        }
    }
}
