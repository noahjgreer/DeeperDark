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
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockFallingDustParticle
extends BillboardParticle {
    private final float rotationSpeed;
    private final SpriteProvider spriteProvider;

    BlockFallingDustParticle(ClientWorld world, double x, double y, double z, float red, float green, float blue, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.red = red;
        this.green = green;
        this.blue = blue;
        float f = 0.9f;
        this.scale *= 0.67499995f;
        int i = (int)(32.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * 0.9f, 1.0f);
        this.updateSprite(spriteProvider);
        this.rotationSpeed = (this.random.nextFloat() - 0.5f) * 0.1f;
        this.zRotation = this.random.nextFloat() * ((float)Math.PI * 2);
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f, 0.0f, 1.0f);
    }

    @Override
    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.updateSprite(this.spriteProvider);
        this.lastZRotation = this.zRotation;
        this.zRotation += (float)Math.PI * this.rotationSpeed * 2.0f;
        if (this.onGround) {
            this.zRotation = 0.0f;
            this.lastZRotation = 0.0f;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityY -= (double)0.003f;
        this.velocityY = Math.max(this.velocityY, (double)-0.14f);
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<BlockStateParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
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
}
