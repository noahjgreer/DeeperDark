/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.recipe.display;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.display.CuttingRecipeDisplay;

public record CuttingRecipeDisplay.Grouping<T extends Recipe<?>>(List<CuttingRecipeDisplay.GroupEntry<T>> entries) {
    public static <T extends Recipe<?>> CuttingRecipeDisplay.Grouping<T> empty() {
        return new CuttingRecipeDisplay.Grouping<T>(List.of());
    }

    public static <T extends Recipe<?>> PacketCodec<RegistryByteBuf, CuttingRecipeDisplay.Grouping<T>> codec() {
        return PacketCodec.tuple(CuttingRecipeDisplay.GroupEntry.codec().collect(PacketCodecs.toList()), CuttingRecipeDisplay.Grouping::entries, CuttingRecipeDisplay.Grouping::new);
    }

    public boolean contains(ItemStack stack) {
        return this.entries.stream().anyMatch(entry -> entry.input.test(stack));
    }

    public CuttingRecipeDisplay.Grouping<T> filter(ItemStack stack) {
        return new CuttingRecipeDisplay.Grouping<T>(this.entries.stream().filter((? super T entry) -> entry.input.test(stack)).toList());
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public int size() {
        return this.entries.size();
    }
}
