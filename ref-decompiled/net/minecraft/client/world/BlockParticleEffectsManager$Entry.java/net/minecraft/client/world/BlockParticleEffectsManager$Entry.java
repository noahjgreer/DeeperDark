/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
static final class BlockParticleEffectsManager.Entry
extends Record {
    private final Vec3d center;
    private final float radius;
    private final int blockCount;
    final Pool<BlockParticleEffect> blockParticles;

    BlockParticleEffectsManager.Entry(Vec3d center, float radius, int blockCount, Pool<BlockParticleEffect> blockParticles) {
        this.center = center;
        this.radius = radius;
        this.blockCount = blockCount;
        this.blockParticles = blockParticles;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockParticleEffectsManager.Entry.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockParticleEffectsManager.Entry.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockParticleEffectsManager.Entry.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this, object);
    }

    public Vec3d center() {
        return this.center;
    }

    public float radius() {
        return this.radius;
    }

    public int blockCount() {
        return this.blockCount;
    }

    public Pool<BlockParticleEffect> blockParticles() {
        return this.blockParticles;
    }
}
