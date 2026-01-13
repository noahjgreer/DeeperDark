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
import net.minecraft.client.particle.GustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class GustParticle.SmallGustFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider field_50230;

    public GustParticle.SmallGustFactory(SpriteProvider spriteProvider) {
        this.field_50230 = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        GustParticle particle = new GustParticle(clientWorld, d, e, f, this.field_50230);
        ((Particle)particle).scale(0.15f);
        return particle;
    }
}
