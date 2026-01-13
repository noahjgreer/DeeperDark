/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component.type;

import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;

public static interface ConsumableComponent.ConsumableSoundProvider {
    public SoundEvent getConsumeSound(ItemStack var1);
}
