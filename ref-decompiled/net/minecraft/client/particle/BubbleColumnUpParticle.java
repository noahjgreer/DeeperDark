/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.BubbleColumnUpParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.math.BlockPos
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class BubbleColumnUpParticle
extends BillboardParticle {
    BubbleColumnUpParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, sprite);
        this.gravityStrength = -0.125f;
        this.velocityMultiplier = 0.85f;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.velocityX = g * (double)0.2f + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.02f);
        this.velocityY = h * (double)0.2f + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.02f);
        this.velocityZ = i * (double)0.2f + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.02f);
        this.maxAge = (int)(40.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
    }

    public void tick() {
        super.tick();
        if (!this.dead && !this.world.getFluidState(BlockPos.ofFloored((double)this.x, (double)this.y, (double)this.z)).isIn(FluidTags.WATER)) {
            this.markDead();
        }
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }
}

