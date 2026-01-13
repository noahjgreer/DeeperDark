/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockDustParticle
extends BillboardParticle {
    private final BillboardParticle.RenderType renderType;
    private final BlockPos blockPos;
    private final float sampleU;
    private final float sampleV;

    public BlockDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state) {
        this(world, x, y, z, velocityX, velocityY, velocityZ, state, BlockPos.ofFloored(x, y, z));
    }

    public BlockDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, BlockState state, BlockPos blockPos) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(state));
        this.blockPos = blockPos;
        this.gravityStrength = 1.0f;
        this.red = 0.6f;
        this.green = 0.6f;
        this.blue = 0.6f;
        if (!state.isOf(Blocks.GRASS_BLOCK)) {
            int i = MinecraftClient.getInstance().getBlockColors().getColor(state, world, blockPos, 0);
            this.red *= (float)(i >> 16 & 0xFF) / 255.0f;
            this.green *= (float)(i >> 8 & 0xFF) / 255.0f;
            this.blue *= (float)(i & 0xFF) / 255.0f;
        }
        this.scale /= 2.0f;
        this.sampleU = this.random.nextFloat() * 3.0f;
        this.sampleV = this.random.nextFloat() * 3.0f;
        this.renderType = this.sprite.getAtlasId().equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE) ? BillboardParticle.RenderType.BLOCK_ATLAS_TRANSLUCENT : BillboardParticle.RenderType.ITEM_ATLAS_TRANSLUCENT;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return this.renderType;
    }

    @Override
    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0f) / 4.0f);
    }

    @Override
    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0f);
    }

    @Override
    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0f);
    }

    @Override
    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0f) / 4.0f);
    }

    @Override
    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        if (i == 0 && this.world.isChunkLoaded(this.blockPos)) {
            return WorldRenderer.getLightmapCoordinates(this.world, this.blockPos);
        }
        return i;
    }

    static @Nullable BlockDustParticle create(BlockStateParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        BlockState blockState = parameters.getBlockState();
        if (blockState.isAir() || blockState.isOf(Blocks.MOVING_PISTON) || !blockState.hasBlockBreakParticles()) {
            return null;
        }
        return new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, blockState);
    }

    @Environment(value=EnvType.CLIENT)
    public static class CrumbleFactory
    implements ParticleFactory<BlockStateParticleEffect> {
        @Override
        public @Nullable Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            BlockDustParticle particle = BlockDustParticle.create(blockStateParticleEffect, clientWorld, d, e, f, g, h, i);
            if (particle != null) {
                particle.setVelocity(0.0, 0.0, 0.0);
                particle.setMaxAge(random.nextInt(10) + 1);
            }
            return particle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DustPillarFactory
    implements ParticleFactory<BlockStateParticleEffect> {
        @Override
        public @Nullable Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            BlockDustParticle particle = BlockDustParticle.create(blockStateParticleEffect, clientWorld, d, e, f, g, h, i);
            if (particle != null) {
                particle.setVelocity(random.nextGaussian() / 30.0, h + random.nextGaussian() / 2.0, random.nextGaussian() / 30.0);
                particle.setMaxAge(random.nextInt(20) + 20);
            }
            return particle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        @Override
        public @Nullable Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return BlockDustParticle.create(blockStateParticleEffect, clientWorld, d, e, f, g, h, i);
        }
    }
}
