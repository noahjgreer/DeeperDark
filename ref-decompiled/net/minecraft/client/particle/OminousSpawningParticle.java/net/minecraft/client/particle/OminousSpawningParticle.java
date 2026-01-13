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
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class OminousSpawningParticle
extends BillboardParticle {
    private final double startX;
    private final double startY;
    private final double startZ;
    private final int fromColor;
    private final int toColor;

    OminousSpawningParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int fromColor, int toColor, Sprite sprite) {
        super(world, x, y, z, sprite);
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.velocityZ = velocityZ;
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.lastX = x + velocityX;
        this.lastY = y + velocityY;
        this.lastZ = z + velocityZ;
        this.x = this.lastX;
        this.y = this.lastY;
        this.z = this.lastZ;
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.2f);
        this.collidesWithWorld = false;
        this.maxAge = (int)(this.random.nextFloat() * 5.0f) + 25;
        this.fromColor = fromColor;
        this.toColor = toColor;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
    public void move(double dx, double dy, double dz) {
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
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
        float f = (float)this.age / (float)this.maxAge;
        float g = 1.0f - f;
        this.x = this.startX + this.velocityX * (double)g;
        this.y = this.startY + this.velocityY * (double)g;
        this.z = this.startZ + this.velocityZ * (double)g;
        int i = ColorHelper.lerp(f, this.fromColor, this.toColor);
        this.setColor((float)ColorHelper.getRed(i) / 255.0f, (float)ColorHelper.getGreen(i) / 255.0f, (float)ColorHelper.getBlue(i) / 255.0f);
        this.setAlpha((float)ColorHelper.getAlpha(i) / 255.0f);
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
            OminousSpawningParticle ominousSpawningParticle = new OminousSpawningParticle(clientWorld, d, e, f, g, h, i, -12210434, -1, this.spriteProvider.getSprite(random));
            ominousSpawningParticle.scale(MathHelper.nextBetween(clientWorld.getRandom(), 3.0f, 5.0f));
            return ominousSpawningParticle;
        }
    }
}
