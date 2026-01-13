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
public static class BlockLeakParticle.DrippingDripstoneLavaFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public BlockLeakParticle.DrippingDripstoneLavaFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        BlockLeakParticle.DrippingLava drippingLava = new BlockLeakParticle.DrippingLava(clientWorld, d, e, f, Fluids.LAVA, ParticleTypes.FALLING_DRIPSTONE_LAVA, this.spriteProvider.getSprite(random));
        return drippingLava;
    }
}
