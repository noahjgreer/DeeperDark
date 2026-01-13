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
import net.minecraft.client.particle.VibrationParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.VibrationParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class VibrationParticle.Factory
implements ParticleFactory<VibrationParticleEffect> {
    private final SpriteProvider spriteProvider;

    public VibrationParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(VibrationParticleEffect vibrationParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        VibrationParticle vibrationParticle = new VibrationParticle(clientWorld, d, e, f, vibrationParticleEffect.getVibration(), vibrationParticleEffect.getArrivalInTicks(), this.spriteProvider.getSprite(random));
        vibrationParticle.setAlpha(1.0f);
        return vibrationParticle;
    }
}
