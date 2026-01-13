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
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class SuspendParticle
extends BillboardParticle {
    SuspendParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        float j;
        this.red = j = this.random.nextFloat() * 0.1f + 0.2f;
        this.green = j;
        this.blue = j;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.5f;
        this.velocityX *= (double)0.02f;
        this.velocityY *= (double)0.02f;
        this.velocityZ *= (double)0.02f;
        this.maxAge = (int)(20.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().offset(dx, dy, dz));
        this.repositionFromBoundingBox();
    }

    @Override
    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= 0.99;
        this.velocityY *= 0.99;
        this.velocityZ *= 0.99;
    }

    @Environment(value=EnvType.CLIENT)
    public static class EggCrackFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public EggCrackFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            SuspendParticle suspendParticle = new SuspendParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
            suspendParticle.setColor(1.0f, 1.0f, 1.0f);
            return suspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class DolphinFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public DolphinFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            SuspendParticle suspendParticle = new SuspendParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
            suspendParticle.setColor(0.3f, 0.5f, 1.0f);
            suspendParticle.setAlpha(1.0f - random.nextFloat() * 0.7f);
            suspendParticle.setMaxAge(suspendParticle.getMaxAge() / 2);
            return suspendParticle;
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
            SuspendParticle suspendParticle = new SuspendParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
            suspendParticle.setColor(1.0f, 1.0f, 1.0f);
            suspendParticle.setMaxAge(3 + clientWorld.getRandom().nextInt(5));
            return suspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class HappyVillagerFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public HappyVillagerFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            SuspendParticle suspendParticle = new SuspendParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
            suspendParticle.setColor(1.0f, 1.0f, 1.0f);
            return suspendParticle;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class MyceliumFactory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public MyceliumFactory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return new SuspendParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
        }
    }
}
