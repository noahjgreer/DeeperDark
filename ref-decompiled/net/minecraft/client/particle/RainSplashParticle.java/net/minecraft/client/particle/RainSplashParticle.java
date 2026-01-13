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
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

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

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
    }

    @Override
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
        if ((d = Math.max(this.world.getBlockState(blockPos = BlockPos.ofFloored(this.x, this.y, this.z)).getCollisionShape(this.world, blockPos).getEndingCoord(Direction.Axis.Y, this.x - (double)blockPos.getX(), this.z - (double)blockPos.getZ()), (double)this.world.getFluidState(blockPos).getHeight(this.world, blockPos))) > 0.0 && this.y < (double)blockPos.getY() + d) {
            this.markDead();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
            return new RainSplashParticle(clientWorld, d, e, f, this.spriteProvider.getSprite(random));
        }
    }
}
