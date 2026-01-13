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
import net.minecraft.client.particle.SuspendParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class SuspendParticle.DolphinFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public SuspendParticle.DolphinFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        SuspendParticle suspendParticle = new SuspendParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider.getSprite(random));
        suspendParticle.setColor(0.3f, 0.5f, 1.0f);
        suspendParticle.setAlpha(1.0f - random.nextFloat() * 0.7f);
        suspendParticle.setMaxAge(suspendParticle.getMaxAge() / 2);
        return suspendParticle;
    }
}
