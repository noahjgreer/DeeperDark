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
public static class GlowParticle.ScrapeFactory
implements ParticleFactory<SimpleParticleType> {
    private static final double velocityMultiplier = 0.01;
    private final SpriteProvider spriteProvider;

    public GlowParticle.ScrapeFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        GlowParticle glowParticle = new GlowParticle(clientWorld, d, e, f, 0.0, 0.0, 0.0, this.spriteProvider);
        if (random.nextBoolean()) {
            glowParticle.setColor(0.29f, 0.58f, 0.51f);
        } else {
            glowParticle.setColor(0.43f, 0.77f, 0.62f);
        }
        glowParticle.setVelocity(g * 0.01, h * 0.01, i * 0.01);
        int j = 10;
        int k = 40;
        glowParticle.setMaxAge(random.nextInt(30) + 10);
        return glowParticle;
    }
}
