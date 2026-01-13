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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class BlockLeakParticle.FallingNectarFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public BlockLeakParticle.FallingNectarFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        BlockLeakParticle.Falling blockLeakParticle = new BlockLeakParticle.Falling(clientWorld, d, e, f, Fluids.EMPTY, this.spriteProvider.getSprite(random));
        blockLeakParticle.maxAge = (int)(16.0 / ((double)random.nextFloat() * 0.8 + 0.2));
        blockLeakParticle.gravityStrength = 0.007f;
        blockLeakParticle.setColor(0.92f, 0.782f, 0.72f);
        return blockLeakParticle;
    }
}
