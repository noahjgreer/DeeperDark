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
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

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

    @Override
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
        if (!this.world.getFluidState(BlockPos.ofFloored(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            this.markDead();
        }
    }

    @Override
    public BillboardParticle.RenderType getRenderType() {
        return BillboardParticle.RenderType.PARTICLE_ATLAS_OPAQUE;
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
            WaterBubbleParticle waterBubbleParticle = new WaterBubbleParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
            return waterBubbleParticle;
        }
    }
}
