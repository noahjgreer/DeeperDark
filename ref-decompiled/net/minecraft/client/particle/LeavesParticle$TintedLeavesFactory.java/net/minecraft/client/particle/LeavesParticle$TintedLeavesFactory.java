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
import net.minecraft.client.particle.LeavesParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.TintedParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class LeavesParticle.TintedLeavesFactory
implements ParticleFactory<TintedParticleEffect> {
    private final SpriteProvider spriteProvider;

    public LeavesParticle.TintedLeavesFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(TintedParticleEffect tintedParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        LeavesParticle leavesParticle = new LeavesParticle(clientWorld, d, e, f, this.spriteProvider.getSprite(random), 0.07f, 10.0f, true, false, 2.0f, 0.021f);
        leavesParticle.setColor(tintedParticleEffect.getRed(), tintedParticleEffect.getGreen(), tintedParticleEffect.getBlue());
        return leavesParticle;
    }
}
