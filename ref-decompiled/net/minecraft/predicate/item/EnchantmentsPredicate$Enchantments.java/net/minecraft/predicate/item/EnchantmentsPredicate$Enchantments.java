/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;

public static class EnchantmentsPredicate.Enchantments
extends EnchantmentsPredicate {
    public static final Codec<EnchantmentsPredicate.Enchantments> CODEC = EnchantmentsPredicate.Enchantments.createCodec(EnchantmentsPredicate.Enchantments::new);

    protected EnchantmentsPredicate.Enchantments(List<EnchantmentPredicate> list) {
        super(list);
    }

    @Override
    public ComponentType<ItemEnchantmentsComponent> getComponentType() {
        return DataComponentTypes.ENCHANTMENTS;
    }
}
