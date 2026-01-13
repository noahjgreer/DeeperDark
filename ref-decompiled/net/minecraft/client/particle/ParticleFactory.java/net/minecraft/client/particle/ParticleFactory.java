/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.BillboardParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface ParticleFactory<T extends ParticleEffect> {
    public @Nullable Particle createParticle(T var1, ClientWorld var2, double var3, double var5, double var7, double var9, double var11, double var13, Random var15);

    @Environment(value=EnvType.CLIENT)
    public static interface BlockLeakParticleFactory<T extends ParticleEffect> {
        public @Nullable BillboardParticle createParticle(T var1, ClientWorld var2, double var3, double var5, double var7, double var9, double var11, double var13, Random var15);
    }
}
