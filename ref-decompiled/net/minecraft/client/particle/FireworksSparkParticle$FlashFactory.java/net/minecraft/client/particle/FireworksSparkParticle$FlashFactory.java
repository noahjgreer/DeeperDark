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
import net.minecraft.client.particle.FireworksSparkParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class FireworksSparkParticle.FlashFactory
implements ParticleFactory<TintedParticleEffect> {
    private final SpriteProvider spriteProvider;

    public FireworksSparkParticle.FlashFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(TintedParticleEffect tintedParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        FireworksSparkParticle.Flash flash = new FireworksSparkParticle.Flash(clientWorld, d, e, f, this.spriteProvider.getSprite(random));
        flash.setColor(tintedParticleEffect.getRed(), tintedParticleEffect.getGreen(), tintedParticleEffect.getBlue());
        flash.setAlpha(tintedParticleEffect.getAlpha());
        return flash;
    }
}
