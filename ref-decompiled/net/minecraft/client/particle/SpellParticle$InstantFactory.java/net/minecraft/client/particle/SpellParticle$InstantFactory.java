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
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.EffectParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class SpellParticle.InstantFactory
implements ParticleFactory<EffectParticleEffect> {
    private final SpriteProvider spriteProvider;

    public SpellParticle.InstantFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(EffectParticleEffect effectParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        SpellParticle spellParticle = new SpellParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        spellParticle.setColor(effectParticleEffect.getRed(), effectParticleEffect.getGreen(), effectParticleEffect.getBlue());
        spellParticle.move(effectParticleEffect.getPower());
        return spellParticle;
    }
}
