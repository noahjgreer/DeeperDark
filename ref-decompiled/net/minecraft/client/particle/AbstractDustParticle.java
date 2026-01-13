/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AbstractDustParticle
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.particle.AbstractDustParticleEffect
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.AbstractDustParticleEffect;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class AbstractDustParticle<T extends AbstractDustParticleEffect>
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected AbstractDustParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, T parameters, SpriteProvider spriteProvider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider.getFirst());
        this.velocityMultiplier = 0.96f;
        this.ascending = true;
        this.spriteProvider = spriteProvider;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.scale *= 0.75f * parameters.getScale();
        int i = (int)(8.0 / (this.random.nextDouble() * 0.8 + 0.2));
        this.maxAge = (int)Math.max((float)i * parameters.getScale(), 1.0f);
        this.updateSprite(spriteProvider);
    }

    protected float darken(float colorComponent, float multiplier) {
        return (this.random.nextFloat() * 0.2f + 0.8f) * colorComponent * multiplier;
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

