/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.passive;

import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfVariant;
import net.minecraft.registry.entry.RegistryEntry;

public static class WolfEntity.WolfData
extends PassiveEntity.PassiveData {
    public final RegistryEntry<WolfVariant> variant;

    public WolfEntity.WolfData(RegistryEntry<WolfVariant> variant) {
        super(false);
        this.variant = variant;
    }
}
