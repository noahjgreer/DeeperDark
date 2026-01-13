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
static class BlockLeakParticle.Dripping
extends BlockLeakParticle {
    private final ParticleEffect nextParticle;

    BlockLeakParticle.Dripping(ClientWorld world, double x, double y, double z, Fluid fluid, ParticleEffect nextParticle, Sprite sprite) {
        super(world, x, y, z, fluid, sprite);
        this.nextParticle = nextParticle;
        this.gravityStrength *= 0.02f;
        this.maxAge = 40;
    }

    @Override
    protected void updateAge() {
        if (this.maxAge-- <= 0) {
            this.markDead();
            this.world.addParticleClient(this.nextParticle, this.x, this.y, this.z, this.velocityX, this.velocityY, this.velocityZ);
        }
    }

    @Override
    protected void updateVelocity() {
        this.velocityX *= 0.02;
        this.velocityY *= 0.02;
        this.velocityZ *= 0.02;
    }
}
