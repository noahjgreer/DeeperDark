/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.DamageParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class DamageParticle
extends BillboardParticle {
    DamageParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0, sprite);
        float j;
        this.velocityMultiplier = 0.7f;
        this.gravityStrength = 0.5f;
        this.velocityX *= (double)0.1f;
        this.velocityY *= (double)0.1f;
        this.velocityZ *= (double)0.1f;
        this.velocityX += g * 0.4;
        this.velocityY += h * 0.4;
        this.velocityZ += i * 0.4;
        this.red = j = this.random.nextFloat() * 0.3f + 0.6f;
        this.green = j;
        this.blue = j;
        this.scale *= 0.75f;
        this.maxAge = Math.max((int)(6.0 / ((double)this.random.nextFloat() * 0.8 + 0.6)), 1);
        this.collidesWithWorld = false;
        this.tick();
    }

    public float getSize(float tickProgress) {
        return this.scale * MathHelper.clamp((float)(((float)this.age + tickProgress) / (float)this.maxAge * 32.0f), (float)0.0f, (float)1.0f);
    }

    public void tick() {
        super.tick();
        this.green *= 0.96f;
        this.blue *= 0.9f;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }
}

