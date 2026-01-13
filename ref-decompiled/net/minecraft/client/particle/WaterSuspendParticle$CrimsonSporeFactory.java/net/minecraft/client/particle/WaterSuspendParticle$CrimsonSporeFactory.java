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
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class WaterSuspendParticle.CrimsonSporeFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public WaterSuspendParticle.CrimsonSporeFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        double j = random.nextGaussian() * (double)1.0E-6f;
        double k = random.nextGaussian() * (double)1.0E-4f;
        double l = random.nextGaussian() * (double)1.0E-6f;
        WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, j, k, l, this.spriteProvider.getSprite(random));
        waterSuspendParticle.setColor(0.9f, 0.4f, 0.5f);
        return waterSuspendParticle;
    }
}
