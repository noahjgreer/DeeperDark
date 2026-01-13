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
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.particle.ParticlesMode;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class BlockParticleEffectsManager {
    private static final int field_62022 = 512;
    private final List<Entry> pool = new ArrayList<Entry>();

    public void scheduleBlockParticles(Vec3d center, float radius, int blockCount, Pool<BlockParticleEffect> particles) {
        if (!particles.isEmpty()) {
            this.pool.add(new Entry(center, radius, blockCount, particles));
        }
    }

    public void tick(ClientWorld world) {
        if (MinecraftClient.getInstance().options.getParticles().getValue() != ParticlesMode.ALL) {
            this.pool.clear();
            return;
        }
        int i = Weighting.getWeightSum(this.pool, Entry::blockCount);
        int j = Math.min(i, 512);
        for (int k = 0; k < j; ++k) {
            Weighting.getRandom(world.getRandom(), this.pool, i, Entry::blockCount).ifPresent(entry -> this.addEffect(world, (Entry)entry));
        }
        this.pool.clear();
    }

    private void addEffect(ClientWorld world, Entry entry) {
        float f;
        Vec3d vec3d2;
        Vec3d vec3d3;
        Random random = world.getRandom();
        Vec3d vec3d = entry.center();
        Vec3d vec3d4 = vec3d.add(vec3d3 = (vec3d2 = new Vec3d(random.nextFloat() * 2.0f - 1.0f, random.nextFloat() * 2.0f - 1.0f, random.nextFloat() * 2.0f - 1.0f).normalize()).multiply(f = (float)Math.cbrt(random.nextFloat()) * entry.radius()));
        if (!world.getBlockState(BlockPos.ofFloored(vec3d4)).isAir()) {
            return;
        }
        float g = 0.5f / (f / entry.radius() + 0.1f) * random.nextFloat() * random.nextFloat() + 0.3f;
        BlockParticleEffect blockParticleEffect = entry.blockParticles.get(random);
        Vec3d vec3d5 = vec3d.add(vec3d3.multiply(blockParticleEffect.scaling()));
        Vec3d vec3d6 = vec3d2.multiply(g * blockParticleEffect.speed());
        world.addParticleClient(blockParticleEffect.particle(), vec3d5.getX(), vec3d5.getY(), vec3d5.getZ(), vec3d6.getX(), vec3d6.getY(), vec3d6.getZ());
    }

    @Environment(value=EnvType.CLIENT)
    static final class Entry
    extends Record {
        private final Vec3d center;
        private final float radius;
        private final int blockCount;
        final Pool<BlockParticleEffect> blockParticles;

        Entry(Vec3d center, float radius, int blockCount, Pool<BlockParticleEffect> blockParticles) {
            this.center = center;
            this.radius = radius;
            this.blockCount = blockCount;
            this.blockParticles = blockParticles;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Entry.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Entry.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Entry.class, "center;radius;blockCount;blockParticles", "center", "radius", "blockCount", "blockParticles"}, this, object);
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
}
