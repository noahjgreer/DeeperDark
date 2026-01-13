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
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
static class BlockLeakParticle.DripstoneLavaDrip
extends BlockLeakParticle.ContinuousFalling {
    BlockLeakParticle.DripstoneLavaDrip(ClientWorld clientWorld, double d, double e, double f, Fluid fluid, ParticleEffect particleEffect, Sprite sprite) {
        super(clientWorld, d, e, f, fluid, particleEffect, sprite);
    }

    @Override
    protected void updateVelocity() {
        if (this.onGround) {
            this.markDead();
            this.world.addParticleClient(this.nextParticle, this.x, this.y, this.z, 0.0, 0.0, 0.0);
            SoundEvent soundEvent = this.getFluid() == Fluids.LAVA ? SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_LAVA : SoundEvents.BLOCK_POINTED_DRIPSTONE_DRIP_WATER;
            float f = MathHelper.nextBetween(this.random, 0.3f, 1.0f);
            this.world.playSoundClient(this.x, this.y, this.z, soundEvent, SoundCategory.BLOCKS, f, 1.0f, false);
        }
    }
}
