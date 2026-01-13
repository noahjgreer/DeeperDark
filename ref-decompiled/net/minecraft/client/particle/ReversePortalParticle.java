/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.PortalParticle
 *  net.minecraft.client.particle.ReversePortalParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;

@Environment(value=EnvType.CLIENT)
public class ReversePortalParticle
extends PortalParticle {
    ReversePortalParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        this.scale *= 1.5f;
        this.maxAge = (int)(this.random.nextFloat() * 2.0f) + 60;
    }

    public float getSize(float tickProgress) {
        float f = 1.0f - ((float)this.age + tickProgress) / ((float)this.maxAge * 1.5f);
        return this.scale * f;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        float f = (float)this.age / (float)this.maxAge;
        this.x += this.velocityX * (double)f;
        this.y += this.velocityY * (double)f;
        this.z += this.velocityZ * (double)f;
    }
}

