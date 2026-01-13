/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.predicate.entity;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.StatType;

record PlayerPredicate.StatMatcher<T>(StatType<T> type, RegistryEntry<T> value, NumberRange.IntRange range, Supplier<Stat<T>> stat) {
    public static final Codec<PlayerPredicate.StatMatcher<?>> CODEC = Registries.STAT_TYPE.getCodec().dispatch(PlayerPredicate.StatMatcher::type, PlayerPredicate.StatMatcher::createCodec);

    public PlayerPredicate.StatMatcher(StatType<T> type, RegistryEntry<T> value, NumberRange.IntRange range) {
        this(type, value, range, (Supplier<Stat<T>>)Suppliers.memoize(() -> type.getOrCreateStat(value.value())));
    }

    private static <T> MapCodec<PlayerPredicate.StatMatcher<T>> createCodec(StatType<T> type) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group((App)type.getRegistry().getEntryCodec().fieldOf("stat").forGetter(PlayerPredicate.StatMatcher::value), (App)NumberRange.IntRange.CODEC.optionalFieldOf("value", (Object)NumberRange.IntRange.ANY).forGetter(PlayerPredicate.StatMatcher::range)).apply((Applicative)instance, (value, range) -> new PlayerPredicate.StatMatcher(type, value, (NumberRange.IntRange)range)));
    }

    public boolean test(StatHandler statHandler) {
        return this.range.test(statHandler.getStat(this.stat.get()));
    }
}
