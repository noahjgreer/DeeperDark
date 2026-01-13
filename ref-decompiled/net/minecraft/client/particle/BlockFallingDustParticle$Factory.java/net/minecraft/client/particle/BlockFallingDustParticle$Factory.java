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
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockFallingDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class BlockFallingDustParticle.Factory
implements ParticleFactory<BlockStateParticleEffect> {
    private final SpriteProvider spriteProvider;

    public BlockFallingDustParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public @Nullable Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        BlockState blockState = blockStateParticleEffect.getBlockState();
        if (!blockState.isAir() && blockState.getRenderType() == BlockRenderType.INVISIBLE) {
            return null;
        }
        BlockPos blockPos = BlockPos.ofFloored(d, e, f);
        int j = MinecraftClient.getInstance().getBlockColors().getParticleColor(blockState, clientWorld, blockPos);
        if (blockState.getBlock() instanceof FallingBlock) {
            j = ((FallingBlock)blockState.getBlock()).getColor(blockState, clientWorld, blockPos);
        }
        float k = (float)(j >> 16 & 0xFF) / 255.0f;
        float l = (float)(j >> 8 & 0xFF) / 255.0f;
        float m = (float)(j & 0xFF) / 255.0f;
        return new BlockFallingDustParticle(clientWorld, d, e, f, k, l, m, this.spriteProvider);
    }
}
