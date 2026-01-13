/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.LavaEmberParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

@Environment(value=EnvType.CLIENT)
public class LavaEmberParticle
extends BillboardParticle {
    LavaEmberParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0, sprite);
        this.gravityStrength = 0.75f;
        this.velocityMultiplier = 0.999f;
        this.velocityX *= (double)0.8f;
        this.velocityY *= (double)0.8f;
        this.velocityZ *= (double)0.8f;
        this.velocityY = this.random.nextFloat() * 0.4f + 0.05f;
        this.scale *= this.random.nextFloat() * 2.0f + 0.2f;
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public int getBrightness(float tint) {
        int i = super.getBrightness(tint);
        int j = 240;
        int k = i >> 16 & 0xFF;
        return 0xF0 | k << 16;
    }

    public float getSize(float tickProgress) {
        float f = ((float)this.age + tickProgress) / (float)this.maxAge;
        return this.scale * (1.0f - f * f);
    }

    public void tick() {
        super.tick();
        if (!this.dead) {
            float f = (float)this.age / (float)this.maxAge;
            if (this.random.nextFloat() > f) {
                this.world.addParticleClient((ParticleEffect)ParticleTypes.SMOKE, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
            }
        }
    }
}

