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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class BlockLeakParticle.FallingHoneyFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public BlockLeakParticle.FallingHoneyFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        BlockLeakParticle.FallingHoney blockLeakParticle = new BlockLeakParticle.FallingHoney(clientWorld, d, e, f, Fluids.EMPTY, ParticleTypes.LANDING_HONEY, this.spriteProvider.getSprite(random));
        blockLeakParticle.gravityStrength = 0.01f;
        blockLeakParticle.setColor(0.582f, 0.448f, 0.082f);
        return blockLeakParticle;
    }
}
