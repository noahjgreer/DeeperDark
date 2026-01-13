/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.advancement.criterion.AbstractCriterion
 *  net.minecraft.advancement.criterion.EnchantedItemCriterion
 *  net.minecraft.advancement.criterion.EnchantedItemCriterion$Conditions
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package net.minecraft.advancement.criterion;

import com.mojang.serialization.Codec;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class EnchantedItemCriterion
extends AbstractCriterion<Conditions> {
    public Codec<Conditions> getConditionsCodec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, int levels) {
        this.trigger(player, conditions -> conditions.matches(stack, levels));
    }
}

