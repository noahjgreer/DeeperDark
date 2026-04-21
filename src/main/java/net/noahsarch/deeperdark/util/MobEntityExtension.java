package net.noahsarch.deeperdark.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.core.Holder;

public interface MobEntityExtension {
    void deeperdark$setStoredEffect(Holder<MobEffect> effect);
    Holder<MobEffect> deeperdark$getStoredEffect();
}

