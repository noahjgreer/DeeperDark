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
import net.minecraft.particle.TrailParticleEffect;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class TrailParticle
extends BillboardParticle {
    private final Vec3d target;

    TrailParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Vec3d target, int color, Sprite sprite) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, sprite);
        color = ColorHelper.scaleRgb(color, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f, 0.875f + this.random.nextFloat() * 0.25f);
        this.red = (float)ColorHelper.getRed(color) / 255.0f;
        this.green = (float)ColorHelper.getGreen(color) / 255.0f;
        this.blue = (float)ColorHelper.getBlue(color) / 255.0f;
        this.scale = 0.26f;
        this.target = target;
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
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        int i = this.maxAge - this.age;
        double d = 1.0 / (double)i;
        this.x = MathHelper.lerp(d, this.x, this.target.getX());
        this.y = MathHelper.lerp(d, this.y, this.target.getY());
        this.z = MathHelper.lerp(d, this.z, this.target.getZ());
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<TrailParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(TrailParticleEffect trailParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            TrailParticle trailParticle = new TrailParticle(clientWorld, d, e, f, g, h, i, trailParticleEffect.target(), trailParticleEffect.color(), this.spriteProvider.getSprite(random));
            trailParticle.setMaxAge(trailParticleEffect.duration());
            return trailParticle;
        }
    }
}
