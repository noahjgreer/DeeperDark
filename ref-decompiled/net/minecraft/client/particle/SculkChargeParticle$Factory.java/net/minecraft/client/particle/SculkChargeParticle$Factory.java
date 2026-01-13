/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SculkChargeParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SculkChargeParticleEffect;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public record SculkChargeParticle.Factory(SpriteProvider spriteProvider) implements ParticleFactory<SculkChargeParticleEffect>
{
    @Override
    public Particle createParticle(SculkChargeParticleEffect sculkChargeParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        SculkChargeParticle sculkChargeParticle = new SculkChargeParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        sculkChargeParticle.setAlpha(1.0f);
        sculkChargeParticle.setVelocity(g, h, i);
        sculkChargeParticle.lastZRotation = sculkChargeParticleEffect.roll();
        sculkChargeParticle.zRotation = sculkChargeParticleEffect.roll();
        sculkChargeParticle.setMaxAge(random.nextInt(12) + 8);
        return sculkChargeParticle;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SculkChargeParticle.Factory.class, "sprite", "spriteProvider"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SculkChargeParticle.Factory.class, "sprite", "spriteProvider"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SculkChargeParticle.Factory.class, "sprite", "spriteProvider"}, this, object);
    }
}
