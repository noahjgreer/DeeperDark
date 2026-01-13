/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.world.Heightmap;

static final class SpawnRestriction.Entry
extends Record {
    final Heightmap.Type heightmapType;
    final SpawnLocation location;
    final SpawnRestriction.SpawnPredicate<?> predicate;

    SpawnRestriction.Entry(Heightmap.Type heightmapType, SpawnLocation location, SpawnRestriction.SpawnPredicate<?> predicate) {
        this.heightmapType = heightmapType;
        this.location = location;
        this.predicate = predicate;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SpawnRestriction.Entry.class, "heightMap;placement;predicate", "heightmapType", "location", "predicate"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SpawnRestriction.Entry.class, "heightMap;placement;predicate", "heightmapType", "location", "predicate"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SpawnRestriction.Entry.class, "heightMap;placement;predicate", "heightmapType", "location", "predicate"}, this, object);
    }

    public Heightmap.Type heightmapType() {
        return this.heightmapType;
    }

    public SpawnLocation location() {
        return this.location;
    }

    public SpawnRestriction.SpawnPredicate<?> predicate() {
        return this.predicate;
    }
}
