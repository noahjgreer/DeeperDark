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
import net.minecraft.client.particle.SculkChargePopParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public record SculkChargePopParticle.Factory(SpriteProvider spriteProvider) implements ParticleFactory<SimpleParticleType>
{
    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        SculkChargePopParticle sculkChargePopParticle = new SculkChargePopParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        sculkChargePopParticle.setAlpha(1.0f);
        sculkChargePopParticle.setVelocity(g, h, i);
        sculkChargePopParticle.setMaxAge(random.nextInt(4) + 6);
        return sculkChargePopParticle;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SculkChargePopParticle.Factory.class, "sprite", "spriteProvider"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SculkChargePopParticle.Factory.class, "sprite", "spriteProvider"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SculkChargePopParticle.Factory.class, "sprite", "spriteProvider"}, this, object);
    }
}
