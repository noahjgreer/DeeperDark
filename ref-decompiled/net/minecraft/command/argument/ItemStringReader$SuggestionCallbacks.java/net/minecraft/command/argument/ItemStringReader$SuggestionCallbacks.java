/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.command.argument.ItemStringReader;

static class ItemStringReader.SuggestionCallbacks
implements ItemStringReader.Callbacks {
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor = SUGGEST_DEFAULT;

    ItemStringReader.SuggestionCallbacks() {
    }

    @Override
    public void setSuggestor(Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestor) {
        this.suggestor = suggestor;
    }

    public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder, StringReader reader) {
        return this.suggestor.apply(builder.createOffset(reader.getCursor()));
    }
}
