/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.SuspiciousStewIngredient
 *  net.minecraft.component.type.SuspiciousStewEffectsComponent
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.registry.Registries
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import org.jspecify.annotations.Nullable;

public interface SuspiciousStewIngredient {
    public SuspiciousStewEffectsComponent getStewEffects();

    public static List<SuspiciousStewIngredient> getAll() {
        return Registries.ITEM.stream().map(SuspiciousStewIngredient::of).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static @Nullable SuspiciousStewIngredient of(ItemConvertible item) {
        BlockItem blockItem;
        Item item2 = item.asItem();
        if (item2 instanceof BlockItem && (item2 = (blockItem = (BlockItem)item2).getBlock()) instanceof SuspiciousStewIngredient) {
            SuspiciousStewIngredient suspiciousStewIngredient = (SuspiciousStewIngredient)item2;
            return suspiciousStewIngredient;
        }
        Item item3 = item.asItem();
        if (item3 instanceof SuspiciousStewIngredient) {
            SuspiciousStewIngredient suspiciousStewIngredient2 = (SuspiciousStewIngredient)item3;
            return suspiciousStewIngredient2;
        }
        return null;
    }
}

