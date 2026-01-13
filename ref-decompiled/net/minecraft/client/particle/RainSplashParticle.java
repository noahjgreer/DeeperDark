/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.BillboardParticle
 *  net.minecraft.client.particle.BillboardParticle$RenderType
 *  net.minecraft.client.particle.RainSplashParticle
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction$Axis
 *  net.minecraft.world.BlockView
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

@Environment(value=EnvType.CLIENT)
public class RainSplashParticle
extends BillboardParticle {
    protected RainSplashParticle(ClientWorld clientWorld, double d, double e, double f, Sprite sprite) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0, sprite);
        this.velocityX *= (double)0.3f;
        this.velocityY = this.random.nextFloat() * 0.2f + 0.1f;
        this.velocityZ *= (double)0.3f;
        this.setBoundingBoxSpacing(0.01f, 0.01f);
        this.gravityStrength = 0.06f;
        this.maxAge = (int)(8.0 / ((double)this.random.nextFloat() * 0.8 + 0.2));
    }

    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    public void tick() {
        BlockPos blockPos;
        double d;
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        if (this.maxAge-- <= 0) {
            this.markDead();
            return;
        }
        this.velocityY -= (double)this.gravityStrength;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
        this.velocityX *= (double)0.98f;
        this.velocityY *= (double)0.98f;
        this.velocityZ *= (double)0.98f;
        if (this.onGround) {
            if (this.random.nextFloat() < 0.5f) {
                this.markDead();
            }
            this.velocityX *= (double)0.7f;
            this.velocityZ *= (double)0.7f;
        }
        if ((d = Math.max(this.world.getBlockState(blockPos = BlockPos.ofFloored((double)this.x, (double)this.y, (double)this.z)).getCollisionShape((BlockView)this.world, blockPos).getEndingCoord(Direction.Axis.Y, this.x - (double)blockPos.getX(), this.z - (double)blockPos.getZ()), (double)this.world.getFluidState(blockPos).getHeight((BlockView)this.world, blockPos))) > 0.0 && this.y < (double)blockPos.getY() + d) {
            this.markDead();
        }
    }
}

