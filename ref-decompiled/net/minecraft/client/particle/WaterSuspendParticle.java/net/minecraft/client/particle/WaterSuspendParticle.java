/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleGroup;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class WaterSuspendParticle
extends BillboardParticle {
    WaterSuspendParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e - 0.125, f, sprite);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0f;
        this.gravityStrength = 0.0f;
    }

    WaterSuspendParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e - 0.125, f, g, h, i, sprite);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.6f;
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0f;
        this.gravityStrength = 0.0f;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Environment(value=EnvType.CLIENT)
    public static class WarpedSporeFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public WarpedSporeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            double j = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, 0.0, j, 0.0, this.spriteProvider.getSprite(random));
            waterSuspendParticle.setColor(0.1f, 0.1f, 0.3f);
            waterSuspendParticle.setBoundingBoxSpacing(0.001f, 0.001f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CrimsonSporeFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public CrimsonSporeFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            double j = random.nextGaussian() * (double)1.0E-6f;
            double k = random.nextGaussian() * (double)1.0E-4f;
            double l = random.nextGaussian() * (double)1.0E-6f;
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, j, k, l, this.spriteProvider.getSprite(random));
            waterSuspendParticle.setColor(0.9f, 0.4f, 0.5f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class SporeBlossomAirFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public SporeBlossomAirFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(this, clientWorld, d, e, f, 0.0, -0.8f, 0.0, this.spriteProvider.getSprite(random)){

                @Override
                public Optional<ParticleGroup> getGroup() {
                    return Optional.of(ParticleGroup.SPORE_BLOSSOM_AIR);
                }
            };
            waterSuspendParticle.maxAge = MathHelper.nextBetween(random, 500, 1000);
            waterSuspendParticle.gravityStrength = 0.01f;
            waterSuspendParticle.setColor(0.32f, 0.5f, 0.22f);
            return waterSuspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class UnderwaterFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public UnderwaterFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, this.spriteProvider.getSprite(random));
            waterSuspendParticle.setColor(0.4f, 0.4f, 0.7f);
            return waterSuspendParticle;
        }
    }
}
