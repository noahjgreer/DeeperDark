/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.WaterSuspendParticle
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
public class WaterSuspendParticle
extends BillboardParticle {
    WaterSuspendParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e - 0.125, f, sprite);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0f;
        this.gravityStrength = 0.0f;
    }

    WaterSuspendParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e - 0.125, f, g, h, i, sprite);
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.6f;
        this.maxAge = (int)(16.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.collidesWithWorld = false;
        this.velocityMultiplier = 1.0f;
        this.gravityStrength = 0.0f;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }
}

