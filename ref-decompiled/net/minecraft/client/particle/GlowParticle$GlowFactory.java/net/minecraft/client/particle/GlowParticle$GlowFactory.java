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
public static class GlowParticle.GlowFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public GlowParticle.GlowFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        GlowParticle glowParticle = new GlowParticle(clientWorld, d, e, f, 0.5 - random.nextDouble(), h, 0.5 - random.nextDouble(), this.spriteProvider);
        if (random.nextBoolean()) {
            glowParticle.setColor(0.6f, 1.0f, 0.8f);
        } else {
            glowParticle.setColor(0.08f, 0.4f, 0.4f);
        }
        glowParticle.velocityY *= (double)0.2f;
        if (g == 0.0 && i == 0.0) {
            glowParticle.velocityX *= (double)0.1f;
            glowParticle.velocityZ *= (double)0.1f;
        }
        glowParticle.setMaxAge((int)(8.0 / (random.nextDouble() * 0.8 + 0.2)));
        return glowParticle;
    }
}
