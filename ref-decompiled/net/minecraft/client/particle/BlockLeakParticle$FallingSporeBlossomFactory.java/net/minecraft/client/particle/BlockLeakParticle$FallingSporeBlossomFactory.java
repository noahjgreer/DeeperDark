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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class BlockLeakParticle.FallingSporeBlossomFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public BlockLeakParticle.FallingSporeBlossomFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        BlockLeakParticle.Falling blockLeakParticle = new BlockLeakParticle.Falling(clientWorld, d, e, f, Fluids.EMPTY, this.spriteProvider.getSprite(random));
        blockLeakParticle.maxAge = (int)(64.0f / MathHelper.nextBetween(blockLeakParticle.random, 0.1f, 0.9f));
        blockLeakParticle.gravityStrength = 0.005f;
        blockLeakParticle.setColor(0.32f, 0.5f, 0.22f);
        return blockLeakParticle;
    }
}
