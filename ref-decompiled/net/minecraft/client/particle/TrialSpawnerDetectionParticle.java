/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BillboardParticle$Rotator
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.particle.TrialSpawnerDetectionParticle
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.random.Random
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class TrialSpawnerDetectionParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;
    private static final int field_47460 = 8;

    protected TrialSpawnerDetectionParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scale, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        this.spriteProvider = spriteProvider;
        this.velocityMultiplier = 0.96f;
        this.gravityStrength = -0.1f;
        this.ascending = true;
        this.velocityX *= 0.0;
        this.velocityY *= 0.9;
        this.velocityZ *= 0.0;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.scale *= 0.75f * scale;
        this.maxAge = (int)(8.0f / MathHelper.nextBetween((Random)this.random, (float)0.5f, (float)1.0f) * scale);
        this.maxAge = Math.max(this.maxAge, 1);
        this.updateSprite(spriteProvider);
        this.collidesWithWorld = true;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public int getBrightness(float tint) {
        return 240;
    }

    public BillboardParticle.Rotator getRotator() {
        return BillboardParticle.Rotator.Y_AND_W_ONLY;
    }

    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f), (float)0.0f, (float)1.0f);
    }
}

