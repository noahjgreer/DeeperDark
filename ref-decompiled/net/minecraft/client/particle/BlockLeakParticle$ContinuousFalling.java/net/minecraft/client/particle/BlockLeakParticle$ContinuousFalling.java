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
static class BlockLeakParticle.ContinuousFalling
extends BlockLeakParticle.Falling {
    protected final ParticleEffect nextParticle;

    BlockLeakParticle.ContinuousFalling(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect particleEffect, Sprite sprite) {
        super(world, x, y, z, fluid, sprite);
        this.maxAge = (int)(64.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
        this.nextParticle = particleEffect;
    }

    @Override
    protected void updateVelocity() {
        if (this.onGround) {
            this.markDead();
            this.world.addParticleClient(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
        }
    }
}
