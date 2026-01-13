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
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SnowflakeParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class SnowflakeParticle.Factory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public SnowflakeParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        SnowflakeParticle snowflakeParticle = new SnowflakeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        snowflakeParticle.setColor(0.923f, 0.964f, 0.999f);
        return snowflakeParticle;
    }
}
