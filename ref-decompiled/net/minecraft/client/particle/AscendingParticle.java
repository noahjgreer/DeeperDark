/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AscendingParticle
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class AscendingParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected AscendingParticle(ClientWorld world, double x, double y, double z, float randomVelocityXMultiplier, float randomVelocityYMultiplier, float randomVelocityZMultiplier, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider, float colorMultiplier, int baseMaxAge, float gravityStrength, boolean collidesWithWorld) {
        super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
        float f;
        this.velocityMultiplier = 0.96f;
        this.gravityStrength = gravityStrength;
        this.ascending = true;
        this.spriteProvider = spriteProvider;
        this.velocityX *= (double)randomVelocityXMultiplier;
        this.velocityY *= (double)randomVelocityYMultiplier;
        this.velocityZ *= (double)randomVelocityZMultiplier;
        this.velocityX += velocityX;
        this.velocityY += velocityY;
        this.velocityZ += velocityZ;
        this.red = f = this.random.nextFloat() * colorMultiplier;
        this.green = f;
        this.blue = f;
        this.scale *= 0.75f * scaleMultiplier;
        this.maxAge = (int)((double)baseMaxAge / ((double)this.random.nextFloat() * 0.8 + 0.2) * (double)scaleMultiplier);
        this.maxAge = Math.max(this.maxAge, 1);
        this.updateSprite(spriteProvider);
        this.collidesWithWorld = collidesWithWorld;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f), (float)0.0f, (float)1.0f);
    }

    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
    }
}

