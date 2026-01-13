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
import net.minecraft.client.particle.AnimatedParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class SquidInkParticle
extends AnimatedParticle {
    SquidInkParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int color, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider, 0.0f);
        this.velocityMultiplier = 0.92f;
        this.scale = 0.5f;
        this.setAlpha(1.0f);
        this.setColor(ColorHelper.getRedFloat(color), ColorHelper.getGreenFloat(color), ColorHelper.getBlueFloat(color));
        this.maxAge = (int)(this.scale * 12.0f / (this.random.nextFloat() * 0.8f + 0.2f));
        this.updateSprite(spriteProvider);
        this.collidesWithWorld = false;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.dead) {
            this.updateSprite(this.spriteProvider);
            if (this.age > this.maxAge / 2) {
                this.setAlpha(1.0f - ((float)this.age - (float)(this.maxAge / 2)) / (float)this.maxAge);
            }
            if (this.world.getBlockState(BlockPos.ofFloored(this.x, this.y, this.z)).isAir()) {
                this.velocityY -= (double)0.0074f;
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class GlowSquidInkFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public GlowSquidInkFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return new SquidInkParticle(clientWorld, d, e, f, g, h, i, ColorHelper.fromFloats(1.0f, 0.2f, 0.8f, 0.6f), this.spriteProvider);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return new SquidInkParticle(clientWorld, d, e, f, g, h, i, -16777216, this.spriteProvider);
        }
    }
}
