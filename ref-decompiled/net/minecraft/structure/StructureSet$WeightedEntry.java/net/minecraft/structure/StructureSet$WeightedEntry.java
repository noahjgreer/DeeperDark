/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.structure;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.structure.Structure;

public record StructureSet.WeightedEntry(RegistryEntry<Structure> structure, int weight) {
    public static final Codec<StructureSet.WeightedEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Structure.ENTRY_CODEC.fieldOf("structure").forGetter(StructureSet.WeightedEntry::structure), (App)Codecs.POSITIVE_INT.fieldOf("weight").forGetter(StructureSet.WeightedEntry::weight)).apply((Applicative)instance, StructureSet.WeightedEntry::new));
}
