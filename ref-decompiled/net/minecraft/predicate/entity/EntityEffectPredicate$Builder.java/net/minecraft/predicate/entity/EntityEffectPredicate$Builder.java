/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package net.minecraft.predicate.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.registry.entry.RegistryEntry;

public static class EntityEffectPredicate.Builder {
    private final ImmutableMap.Builder<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData> effects = ImmutableMap.builder();

    public static EntityEffectPredicate.Builder create() {
        return new EntityEffectPredicate.Builder();
    }

    public EntityEffectPredicate.Builder addEffect(RegistryEntry<StatusEffect> effect) {
        this.effects.put(effect, (Object)new EntityEffectPredicate.EffectData());
        return this;
    }

    public EntityEffectPredicate.Builder addEffect(RegistryEntry<StatusEffect> effect, EntityEffectPredicate.EffectData effectData) {
        this.effects.put(effect, (Object)effectData);
        return this;
    }

    public Optional<EntityEffectPredicate> build() {
        return Optional.of(new EntityEffectPredicate((Map<RegistryEntry<StatusEffect>, EntityEffectPredicate.EffectData>)this.effects.build()));
    }
}
