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
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class GlowParticle.ElectricSparkFactory
implements ParticleFactory<SimpleParticleType> {
    private static final double velocityMultiplier = 0.25;
    private final SpriteProvider spriteProvider;

    public GlowParticle.ElectricSparkFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        GlowParticle glowParticle = new GlowParticle(clientWorld, d, e, f, 0.0, 0.0, 0.0, this.spriteProvider);
        glowParticle.setColor(1.0f, 0.9f, 1.0f);
        glowParticle.setVelocity(g * 0.25, h * 0.25, i * 0.25);
        int j = 2;
        int k = 4;
        glowParticle.setMaxAge(random.nextInt(2) + 2);
        return glowParticle;
    }
}
