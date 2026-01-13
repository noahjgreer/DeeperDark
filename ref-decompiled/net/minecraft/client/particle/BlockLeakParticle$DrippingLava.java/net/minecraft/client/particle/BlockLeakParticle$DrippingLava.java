/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BlockLeakParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.particle.ParticleEffect;

@Environment(value=EnvType.CLIENT)
static class BlockLeakParticle.DrippingLava
extends BlockLeakParticle.Dripping {
    BlockLeakParticle.DrippingLava(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect, Sprite sprite) {
        super(clientWorld, d, e, f, fluid, particleEffect, sprite);
    }

    @Override
    protected void updateAge() {
        this.red = 1.0f;
        this.green = 16.0f / (float)(40 - this.maxAge + 16);
        this.blue = 4.0f / (float)(40 - this.maxAge + 8);
        super.updateAge();
    }
}
