/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 */
package net.minecraft.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Identifier;

static final class SuggestionProviders.LocalProvider
extends Record
implements SuggestionProvider<CommandSource> {
    final Identifier id;
    private final SuggestionProvider<CommandSource> provider;

    SuggestionProviders.LocalProvider(Identifier id, SuggestionProvider<CommandSource> provider) {
        this.id = id;
        this.provider = provider;
    }

    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        return this.provider.getSuggestions(context, builder);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SuggestionProviders.LocalProvider.class, "name;delegate", "id", "provider"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SuggestionProviders.LocalProvider.class, "name;delegate", "id", "provider"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SuggestionProviders.LocalProvider.class, "name;delegate", "id", "provider"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }

    public SuggestionProvider<CommandSource> provider() {
        return this.provider;
    }
}
