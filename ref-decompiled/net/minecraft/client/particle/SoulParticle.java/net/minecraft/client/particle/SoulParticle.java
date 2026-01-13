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
import net.minecraft.client.particle.AbstractSlowingParticle;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class SoulParticle
extends AbstractSlowingParticle {
    private final SpriteProvider spriteProvider;
    protected boolean sculk;

    SoulParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.scale(1.5f);
        this.updateSprite(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        if (this.sculk) {
            return 240;
        }
        return super.getBrightness(tint);
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SculkSoulFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public SculkSoulFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            SoulParticle soulParticle = new SoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0f);
            soulParticle.sculk = true;
            return soulParticle;
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
            SoulParticle soulParticle = new SoulParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
            soulParticle.setAlpha(1.0f);
            return soulParticle;
        }
    }
}
