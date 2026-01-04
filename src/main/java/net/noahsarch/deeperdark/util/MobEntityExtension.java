package net.noahsarch.deeperdark.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public interface MobEntityExtension {
    void deeperdark$setStoredEffect(RegistryEntry<StatusEffect> effect);
    RegistryEntry<StatusEffect> deeperdark$getStoredEffect();
}

