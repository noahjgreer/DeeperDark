/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.WaterBubbleParticle
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
public class WaterBubbleParticle
extends BillboardParticle {
    WaterBubbleParticle(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Sprite sprite) {
        super(clientWorld, d, e, f, sprite);
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.velocityX = g * (double)0.2f + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.02f);
        this.velocityY = h * (double)0.2f + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.02f);
        this.velocityZ = i * (double)0.2f + (double)((this.random.nextFloat() * 2.0f - 1.0f) * 0.02f);
        this.maxAge = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.velocityY += 0.002;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.85f;
        this.velocityY *= (double)0.85f;
        this.velocityZ *= (double)0.85f;
        if (!this.world.getFluidState(BlockPos.ofFloored((double)this.x, (double)this.y, (double)this.z)).isIn(FluidTags.WATER)) {
            this.markDead();
        }
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }
}

