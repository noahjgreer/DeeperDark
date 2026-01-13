/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.entity.mob;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

public static class SpiderEntity.SpiderData
implements EntityData {
    public @Nullable RegistryEntry<StatusEffect> effect;

    public void setEffect(Random random) {
        int i = random.nextInt(5);
        if (i <= 1) {
            this.effect = StatusEffects.SPEED;
        } else if (i <= 2) {
            this.effect = StatusEffects.STRENGTH;
        } else if (i <= 3) {
            this.effect = StatusEffects.REGENERATION;
        } else if (i <= 4) {
            this.effect = StatusEffects.INVISIBILITY;
        }
    }
}
