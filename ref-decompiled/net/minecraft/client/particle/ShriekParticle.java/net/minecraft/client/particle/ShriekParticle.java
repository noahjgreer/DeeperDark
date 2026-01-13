/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.BillboardParticleSubmittable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ShriekParticleEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;

@Environment(value=EnvType.CLIENT)
public class ShriekParticle
extends BillboardParticle {
    private static final float X_ROTATION = 1.0472f;
    private int delay;

    ShriekParticle(ClientWorld world, double x, double y, double z, int delay, Sprite sprite) {
        super(world, x, y, z, 0.0, 0.0, 0.0, sprite);
        this.scale = 0.85f;
        this.delay = delay;
        this.maxAge = 30;
        this.gravityStrength = 0.0f;
        this.velocityX = 0.0;
        this.velocityY = 0.1;
        this.velocityZ = 0.0;
    }

    @Override
    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp(((float)this.age + tickProgress) / (float)this.maxAge * 0.75f, 0.0f, 1.0f);
    }

    @Override
    public void render(BillboardParticleSubmittable submittable, Camera camera, float tickProgress) {
        if (this.delay > 0) {
            return;
        }
        this.alpha = 1.0f - MathHelper.clamp(((float)this.age + tickProgress) / (float)this.maxAge, 0.0f, 1.0f);
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotationX(-1.0472f);
        this.render(submittable, camera, quaternionf, tickProgress);
        quaternionf.rotationYXZ((float)(-Math.PI), 1.0472f, 0.0f);
        this.render(submittable, camera, quaternionf, tickProgress);
    }

    @Override
    public int getBrightness(float tint) {
        return 240;
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if (this.delay > 0) {
            --this.delay;
            return;
        }
        super.tick();
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<ShriekParticleEffect> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(ShriekParticleEffect shriekParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            ShriekParticle shriekParticle = new ShriekParticle(clientWorld, d, e, f, shriekParticleEffect.getDelay(), this.spriteProvider.getSprite(random));
            shriekParticle.setAlpha(1.0f);
            return shriekParticle;
        }
    }
}
