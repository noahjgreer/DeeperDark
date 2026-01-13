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
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class GustParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected GustParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.updateSprite(spriteProvider);
        this.maxAge = 12 + this.random.nextInt(4);
        this.scale = 1.0f;
        this.setBoundingBoxSpacing(1.0f, 1.0f);
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        this.updateSprite(this.spriteProvider);
    }

    @Environment(value=EnvType.CLIENT)
    public static class SmallGustFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider field_50230;

        public SmallGustFactory(SpriteProvider spriteProvider) {
            this.field_50230 = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            GustParticle particle = new GustParticle(clientWorld, d, e, f, this.field_50230);
            ((Particle)particle).scale(0.15f);
            return particle;
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
            return new GustParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
