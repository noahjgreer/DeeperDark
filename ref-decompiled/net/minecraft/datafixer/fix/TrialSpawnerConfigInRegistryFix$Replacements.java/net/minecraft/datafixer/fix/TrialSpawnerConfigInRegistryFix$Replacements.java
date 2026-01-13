/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.fix;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;

static final class TrialSpawnerConfigInRegistryFix.Replacements {
    public static final Map<Pair<Dynamic<NbtElement>, Dynamic<NbtElement>>, Identifier> REPLACEMENTS = new HashMap<Pair<Dynamic<NbtElement>, Dynamic<NbtElement>>, Identifier>();

    private TrialSpawnerConfigInRegistryFix.Replacements() {
    }

    private static void register(Identifier id, String normal, String ominous) {
        try {
            NbtCompound nbtCompound = TrialSpawnerConfigInRegistryFix.Replacements.parse(normal);
            NbtCompound nbtCompound2 = TrialSpawnerConfigInRegistryFix.Replacements.parse(ominous);
            NbtCompound nbtCompound3 = nbtCompound.copy().copyFrom(nbtCompound2);
            NbtCompound nbtCompound4 = TrialSpawnerConfigInRegistryFix.Replacements.removeDefaults(nbtCompound3.copy());
            Dynamic<NbtElement> dynamic = TrialSpawnerConfigInRegistryFix.Replacements.toDynamic(nbtCompound);
            REPLACEMENTS.put((Pair<Dynamic<NbtElement>, Dynamic<NbtElement>>)Pair.of(dynamic, TrialSpawnerConfigInRegistryFix.Replacements.toDynamic(nbtCompound2)), id);
            REPLACEMENTS.put((Pair<Dynamic<NbtElement>, Dynamic<NbtElement>>)Pair.of(dynamic, TrialSpawnerConfigInRegistryFix.Replacements.toDynamic(nbtCompound3)), id);
            REPLACEMENTS.put((Pair<Dynamic<NbtElement>, Dynamic<NbtElement>>)Pair.of(dynamic, TrialSpawnerConfigInRegistryFix.Replacements.toDynamic(nbtCompound4)), id);
        }
        catch (RuntimeException runtimeException) {
            throw new IllegalStateException("Failed to parse NBT for " + String.valueOf(id), runtimeException);
        }
    }

    private static Dynamic<NbtElement> toDynamic(NbtCompound nbt) {
        return new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)nbt);
    }

    private static NbtCompound parse(String nbt) {
        try {
            return StringNbtReader.readCompound(nbt);
        }
        catch (CommandSyntaxException commandSyntaxException) {
            throw new IllegalArgumentException("Failed to parse Trial Spawner NBT config: " + nbt, commandSyntaxException);
        }
    }

    private static NbtCompound removeDefaults(NbtCompound nbt) {
        if (nbt.getInt("spawn_range", 0) == 4) {
            nbt.remove("spawn_range");
        }
        if (nbt.getFloat("total_mobs", 0.0f) == 6.0f) {
            nbt.remove("total_mobs");
        }
        if (nbt.getFloat("simultaneous_mobs", 0.0f) == 2.0f) {
            nbt.remove("simultaneous_mobs");
        }
        if (nbt.getFloat("total_mobs_added_per_player", 0.0f) == 2.0f) {
            nbt.remove("total_mobs_added_per_player");
        }
        if (nbt.getFloat("simultaneous_mobs_added_per_player", 0.0f) == 1.0f) {
            nbt.remove("simultaneous_mobs_added_per_player");
        }
        if (nbt.getInt("ticks_between_spawn", 0) == 40) {
            nbt.remove("ticks_between_spawn");
        }
        return nbt;
    }

    static {
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/breeze"), "{simultaneous_mobs: 1.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:breeze\"}}, weight: 1}], ticks_between_spawn: 20, total_mobs: 2.0f, total_mobs_added_per_player: 1.0f}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 2.0f, total_mobs: 4.0f}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/melee/husk"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:husk\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:husk\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/melee/spider"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:spider\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/melee/zombie"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:zombie\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:zombie\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/ranged/poison_skeleton"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/ranged/skeleton"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/ranged/stray"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/slow_ranged/poison_skeleton"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:bogged\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/slow_ranged/skeleton"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {id: \"minecraft:skeleton\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/slow_ranged/stray"), "{simultaneous_mobs: 4.0f, simultaneous_mobs_added_per_player: 2.0f, spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}}, weight: 1}], ticks_between_spawn: 160}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}],spawn_potentials: [{data: {entity: {id: \"minecraft:stray\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_ranged\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/small_melee/baby_zombie"), "{simultaneous_mobs: 2.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {IsBaby: 1b, id: \"minecraft:zombie\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], spawn_potentials: [{data: {entity: {IsBaby: 1b, id: \"minecraft:zombie\"}, equipment: {loot_table: \"minecraft:equipment/trial_chamber_melee\", slot_drop_chances: 0.0f}}, weight: 1}]}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/small_melee/cave_spider"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:cave_spider\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/small_melee/silverfish"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {id: \"minecraft:silverfish\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
        TrialSpawnerConfigInRegistryFix.Replacements.register(Identifier.ofVanilla("trial_chamber/small_melee/slime"), "{simultaneous_mobs: 3.0f, simultaneous_mobs_added_per_player: 0.5f, spawn_potentials: [{data: {entity: {Size: 1, id: \"minecraft:slime\"}}, weight: 3}, {data: {entity: {Size: 2, id: \"minecraft:slime\"}}, weight: 1}], ticks_between_spawn: 20}", "{loot_tables_to_eject: [{data: \"minecraft:spawners/ominous/trial_chamber/key\", weight: 3}, {data: \"minecraft:spawners/ominous/trial_chamber/consumables\", weight: 7}], simultaneous_mobs: 4.0f, total_mobs: 12.0f}");
    }
}
