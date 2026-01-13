/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.block.spawner.MobSpawnerEntry
 *  net.minecraft.block.spawner.MobSpawnerEntry$CustomSpawnRules
 *  net.minecraft.entity.EquipmentTable
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.collection.Pool
 */
package net.minecraft.block.spawner;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;

public record MobSpawnerEntry(NbtCompound entity, Optional<CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
    private final NbtCompound entity;
    private final Optional<CustomSpawnRules> customSpawnRules;
    private final Optional<EquipmentTable> equipment;
    public static final String ENTITY_KEY = "entity";
    public static final Codec<MobSpawnerEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)NbtCompound.CODEC.fieldOf(ENTITY_KEY).forGetter(entry -> entry.entity), (App)CustomSpawnRules.CODEC.optionalFieldOf("custom_spawn_rules").forGetter(entry -> entry.customSpawnRules), (App)EquipmentTable.CODEC.optionalFieldOf("equipment").forGetter(entry -> entry.equipment)).apply((Applicative)instance, MobSpawnerEntry::new));
    public static final Codec<Pool<MobSpawnerEntry>> DATA_POOL_CODEC = Pool.createCodec((Codec)CODEC);

    public MobSpawnerEntry() {
        this(new NbtCompound(), Optional.empty(), Optional.empty());
    }

    public MobSpawnerEntry(NbtCompound entity, Optional<CustomSpawnRules> customSpawnRules, Optional<EquipmentTable> equipment) {
        Optional optional = entity.get("id", Identifier.CODEC);
        if (optional.isPresent()) {
            entity.put("id", Identifier.CODEC, (Object)((Identifier)optional.get()));
        } else {
            entity.remove("id");
        }
        this.entity = entity;
        this.customSpawnRules = customSpawnRules;
        this.equipment = equipment;
    }

    public NbtCompound getNbt() {
        return this.entity;
    }

    public Optional<CustomSpawnRules> getCustomSpawnRules() {
        return this.customSpawnRules;
    }

    public Optional<EquipmentTable> getEquipment() {
        return this.equipment;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MobSpawnerEntry.class, "entityToSpawn;customSpawnRules;equipment", "entity", "customSpawnRules", "equipment"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MobSpawnerEntry.class, "entityToSpawn;customSpawnRules;equipment", "entity", "customSpawnRules", "equipment"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MobSpawnerEntry.class, "entityToSpawn;customSpawnRules;equipment", "entity", "customSpawnRules", "equipment"}, this, o);
    }

    public NbtCompound entity() {
        return this.entity;
    }

    public Optional<CustomSpawnRules> customSpawnRules() {
        return this.customSpawnRules;
    }

    public Optional<EquipmentTable> equipment() {
        return this.equipment;
    }
}

