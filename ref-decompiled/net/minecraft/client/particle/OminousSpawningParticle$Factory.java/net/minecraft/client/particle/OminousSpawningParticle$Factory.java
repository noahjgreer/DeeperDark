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
import net.minecraft.client.particle.OminousSpawningParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class OminousSpawningParticle.Factory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public OminousSpawningParticle.Factory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        OminousSpawningParticle ominousSpawningParticle = new OminousSpawningParticle(clientWorld, d, e, f, g, h, i, -12210434, -1, this.spriteProvider.getSprite(random));
        ominousSpawningParticle.scale(MathHelper.nextBetween(clientWorld.getRandom(), 3.0f, 5.0f));
        return ominousSpawningParticle;
    }
}
