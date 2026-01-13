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
import net.minecraft.client.particle.GustEmitterParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class GustEmitterParticle.Factory
implements ParticleFactory<SimpleParticleType> {
    private final double deviation;
    private final int maxAge;
    private final int interval;

    public GustEmitterParticle.Factory(double deviation, int maxAge, int interval) {
        this.deviation = deviation;
        this.maxAge = maxAge;
        this.interval = interval;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        return new GustEmitterParticle(clientWorld, d, e, f, this.deviation, this.maxAge, this.interval);
    }
}
