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
public class FishingParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    FishingParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.velocityX *= (double)0.3f;
        this.velocityY = this.random.nextFloat() * 0.2f + 0.1f;
        this.velocityZ *= (double)0.3f;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.maxAge = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.updateSprite(spriteProvider);
        this.gravityStrength = 0.0f;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        int i = 60 - this.maxAge;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.98f;
        this.velocityY *= (double)0.98f;
        this.velocityZ *= (double)0.98f;
        float f = (float)i * 0.001f;
        this.setBoundingBoxSpacing(f, f);
        this.setSprite(this.spriteProvider.getSprite(i % 4, 4));
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
            return new FishingParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }
    }
}
