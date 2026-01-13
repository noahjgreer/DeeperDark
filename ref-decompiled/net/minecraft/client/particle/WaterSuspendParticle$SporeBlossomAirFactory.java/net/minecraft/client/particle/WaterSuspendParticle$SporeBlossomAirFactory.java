/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleGroup;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class WaterSuspendParticle.SporeBlossomAirFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public WaterSuspendParticle.SporeBlossomAirFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(this, clientWorld, d, e, f, 0.0, -0.8f, 0.0, this.spriteProvider.getSprite(random)){

            @Override
            public Optional<ParticleGroup> getGroup() {
                return Optional.of(ParticleGroup.SPORE_BLOSSOM_AIR);
            }
        };
        waterSuspendParticle.maxAge = MathHelper.nextBetween(random, 500, 1000);
        waterSuspendParticle.gravityStrength = 0.01f;
        waterSuspendParticle.setColor(0.32f, 0.5f, 0.22f);
        return waterSuspendParticle;
    }
}
