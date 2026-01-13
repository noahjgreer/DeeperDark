/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.particle.ExplosionEmitterParticle
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
public class ExplosionEmitterParticle
extends NoRenderParticle {
    ExplosionEmitterParticle(ClientWorld clientWorld, double d, double e, double f) {
        super(clientWorld, d, e, f, 0.0, 0.0, 0.0);
        this.maxAge = 8;
    }

    public void tick() {
        for (int i = 0; i < 6; ++i) {
            double d = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            double e = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            double f = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0;
            this.world.addParticleClient((ParticleEffect)ParticleTypes.EXPLOSION, d, e, f, (double)((float)this.age / (float)this.maxAge), 0.0, 0.0);
        }
        ++this.age;
        if (this.age == this.maxAge) {
            this.markDead();
        }
    }
}

