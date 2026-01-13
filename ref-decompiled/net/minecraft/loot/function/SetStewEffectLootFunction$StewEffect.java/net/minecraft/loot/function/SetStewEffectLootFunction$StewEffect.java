/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.loot.function;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;

record SetStewEffectLootFunction.StewEffect(RegistryEntry<StatusEffect> effect, LootNumberProvider duration) {
    public static final Codec<SetStewEffectLootFunction.StewEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)StatusEffect.ENTRY_CODEC.fieldOf("type").forGetter(SetStewEffectLootFunction.StewEffect::effect), (App)LootNumberProviderTypes.CODEC.fieldOf("duration").forGetter(SetStewEffectLootFunction.StewEffect::duration)).apply((Applicative)instance, SetStewEffectLootFunction.StewEffect::new));
}
