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
import net.minecraft.client.particle.FireflyParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class FireflyParticle.Factory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public FireflyParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        FireflyParticle fireflyParticle = new FireflyParticle(clientWorld, d, e, f, 0.5 - random.nextDouble(), random.nextBoolean() ? h : -h, 0.5 - random.nextDouble(), this.spriteProvider.getSprite(random));
        fireflyParticle.setMaxAge(random.nextBetween(200, 300));
        fireflyParticle.scale(1.5f);
        fireflyParticle.setAlpha(0.0f);
        return fireflyParticle;
    }
}
