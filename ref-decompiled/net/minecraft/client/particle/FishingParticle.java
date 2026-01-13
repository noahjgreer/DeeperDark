/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.FishingParticle
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

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

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
}

