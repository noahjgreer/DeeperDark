/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.AbstractSlowingParticle
 *  net.minecraft.client.particle.BillboardParticle
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
public abstract class AbstractSlowingParticle
extends BillboardParticle {
    protected AbstractSlowingParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, g, h, i, sprite);
        this.velocityMultiplier = 0.96f;
        this.velocityX = this.velocityX * (double)0.01f + g;
        this.velocityY = this.velocityY * (double)0.01f + h;
        this.velocityZ = this.velocityZ * (double)0.01f + i;
        this.x += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.y += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.z += (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.05f);
        this.maxAge = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2)) + 4;
    }
}

