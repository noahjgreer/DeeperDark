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
import net.minecraft.client.particle.WaterSuspendParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static class WaterSuspendParticle.WarpedSporeFactory
implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public WaterSuspendParticle.WarpedSporeFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, Random random) {
        double j = (double)random.nextFloat() * -1.9 * (double)random.nextFloat() * 0.1;
        WaterSuspendParticle waterSuspendParticle = new WaterSuspendParticle(clientWorld, d, e, f, 0.0, j, 0.0, this.spriteProvider.getSprite(random));
        waterSuspendParticle.setColor(0.1f, 0.1f, 0.3f);
        waterSuspendParticle.setBoundingBoxSpacing(0.001f, 0.001f);
        return waterSuspendParticle;
    }
}
