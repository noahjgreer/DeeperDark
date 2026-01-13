/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.GustEmitterParticle
 *  net.minecraft.client.particle.NoRenderParticle
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;

@Environment(value=EnvType.CLIENT)
public class GustEmitterParticle
extends NoRenderParticle {
    private final double deviation;
    private final int interval;

    GustEmitterParticle(ClientWorld world, double x, double y, double z, double deviation, int maxAge, int interval) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.deviation = deviation;
        this.maxAge = maxAge;
        this.interval = interval;
    }

    public void tick() {
        if (this.age % (this.interval + 1) == 0) {
            for (int i = 0; i < 3; ++i) {
                double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * this.deviation;
                double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * this.deviation;
                double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * this.deviation;
                this.world.addParticleClient((ParticleEffect)ParticleTypes.GUST, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
            }
        }
        if (this.age++ == this.maxAge) {
            this.markDead();
        }
    }
}

