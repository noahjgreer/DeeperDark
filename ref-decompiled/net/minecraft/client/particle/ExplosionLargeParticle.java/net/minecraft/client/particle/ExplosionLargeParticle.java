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
public class ExplosionLargeParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected ExplosionLargeParticle(ClientWorld world, double x, double y, double z, double velocityX, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        float f;
        this.maxAge = 6 + this.random.nextInt(4);
        this.red = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.green = f;
        this.blue = f;
        this.scale = 2.0f * (1.0f - (float)velocityX * 0.5f);
        this.spriteProvider = spriteProvider;
        this.updateSprite(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
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
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
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
            return new ExplosionLargeParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}
