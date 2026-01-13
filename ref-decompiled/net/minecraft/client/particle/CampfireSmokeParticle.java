/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.CampfireSmokeParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class CampfireSmokeParticle
extends BillboardParticle {
    CampfireSmokeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, boolean signal, Sprite sprite) {
        super(world, x, y, z, sprite);
        this.scale(3.0f);
        this.setBoundingBoxSpacing(0.25f, 0.25f);
        this.maxAge = signal ? this.random.nextInt(50) + 280 : this.random.nextInt(50) + 80;
        this.gravityStrength = 3.0E-6f;
        this.velocityX = velocityX;
        this.velocityY = velocityY + (double)(this.random.nextFloat() / 500.0f);
        this.velocityZ = velocityZ;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge || this.alpha <= 0.0f) {
            this.markDead();
            return;
        }
        this.velocityX += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.velocityZ += (double)(this.random.nextFloat() / 5000.0f * (float)(this.random.nextBoolean() ? 1 : -1));
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (this.age >= this.maxAge - 60 && this.alpha > 0.01f) {
            this.alpha -= 0.015f;
        }
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_TRANSLUCENT;
    }
}

