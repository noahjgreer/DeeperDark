/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.CurrentDownParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class CurrentDownParticle
extends BillboardParticle {
    private float accelerationAngle;

    CurrentDownParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e, f, sprite);
        this.maxAge = (int)(this.random.nextFloat() * 60.0f) + 30;
        this.collidesWithWorld = false;
        this.velocityX = 0.0;
        this.velocityY = -0.05;
        this.velocityZ = 0.0;
        this.setBoundingBoxSpacing(0.02f, 0.02f);
        this.scale *= this.random.nextFloat() * 0.6f + 0.2f;
        this.gravityStrength = 0.002f;
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public void tick() {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.age++ >= this.maxAge) {
            this.markDead();
            return;
        }
        float f = 0.6f;
        this.velocityX += (double)(0.6f * MathHelper.cos((double)this.accelerationAngle));
        this.velocityZ += (double)(0.6f * MathHelper.sin((double)this.accelerationAngle));
        this.velocityX *= 0.07;
        this.velocityZ *= 0.07;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        if (!this.world.getFluidState(BlockPos.ofFloored((double)this.x, (double)this.y, (double)this.z)).isIn(FluidTags.WATER) || this.onGround) {
            this.markDead();
        }
        this.accelerationAngle += 0.08f;
    }
}

