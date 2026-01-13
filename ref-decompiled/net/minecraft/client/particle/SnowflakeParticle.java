/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.SnowflakeParticle
 *  net.minecraft.client.particle.SpriteProvider
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class SnowflakeParticle
extends BillboardParticle {
    private final SpriteProvider spriteProvider;

    protected SnowflakeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
        super(world, x, y, z, spriteProvider.getFirst());
        this.gravityStrength = 0.225f;
        this.velocityMultiplier = 1.0f;
        this.spriteProvider = spriteProvider;
        this.velocityX = velocityX + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f);
        this.velocityY = velocityY + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f);
        this.velocityZ = velocityZ + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.05f);
        this.scale = 0.1f * (this.random.nextFloat() * this.random.nextFloat() * 1.0f + 1.0f);
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 2;
        this.updateSprite(spriteProvider);
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public void tick() {
        super.tick();
        this.updateSprite(this.spriteProvider);
        this.velocityX *= (double)0.95f;
        this.velocityY *= (double)0.9f;
        this.velocityZ *= (double)0.95f;
    }
}

