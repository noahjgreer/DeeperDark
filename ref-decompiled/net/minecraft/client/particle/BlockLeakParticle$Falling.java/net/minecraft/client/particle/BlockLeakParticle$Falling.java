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

@Environment(value=EnvType.CLIENT)
static class BlockLeakParticle.Falling
extends BlockLeakParticle {
    BlockLeakParticle.Falling(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, Sprite sprite) {
        super(clientWorld, d, e, f, fluid, sprite);
    }

    @Override
    protected void updateVelocity() {
        if (this.onGround) {
            this.markDead();
        }
    }
}
