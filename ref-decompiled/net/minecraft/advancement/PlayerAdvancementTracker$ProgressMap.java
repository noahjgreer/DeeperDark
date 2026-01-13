/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.advancement;

import com.mojang.serialization.Codec;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.Identifier;

record PlayerAdvancementTracker.ProgressMap(Map<Identifier, AdvancementProgress> map) {
    public static final Codec<PlayerAdvancementTracker.ProgressMap> CODEC = Codec.unboundedMap(Identifier.CODEC, AdvancementProgress.CODEC).xmap(PlayerAdvancementTracker.ProgressMap::new, PlayerAdvancementTracker.ProgressMap::map);

    public void forEach(BiConsumer<Identifier, AdvancementProgress> consumer) {
        this.map.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach((? super T entry) -> consumer.accept((Identifier)entry.getKey(), (AdvancementProgress)entry.getValue()));
    }
}
