/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public static interface ItemStringReader.Callbacks {
    default public void onItem(RegistryEntry<Item> item) {
    }

    default public <T> void onComponentAdded(ComponentType<T> type, T value) {
    }

    default public <T> void onComponentRemoved(ComponentType<T> type) {
    }

    default public void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
    }
}
