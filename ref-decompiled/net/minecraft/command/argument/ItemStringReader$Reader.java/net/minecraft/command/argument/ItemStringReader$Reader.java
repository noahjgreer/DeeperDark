/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.serialization.DataResult
 *  it.unimi.dsi.fastutil.objects.ReferenceArraySet
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.ItemStringReader;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

class ItemStringReader.Reader {
    private final StringReader reader;
    private final ItemStringReader.Callbacks callbacks;

    ItemStringReader.Reader(StringReader reader, ItemStringReader.Callbacks callbacks) {
        this.reader = reader;
        this.callbacks = callbacks;
    }

    public void read() throws CommandSyntaxException {
        this.callbacks.setSuggestor(this::suggestItems);
        this.readItem();
        this.callbacks.setSuggestor(this::suggestBracket);
        if (this.reader.canRead() && this.reader.peek() == '[') {
            this.callbacks.setSuggestor(SUGGEST_DEFAULT);
            this.readComponents();
        }
    }

    private void readItem() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(this.reader);
        this.callbacks.onItem((RegistryEntry<Item>)ItemStringReader.this.itemRegistry.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> {
            this.reader.setCursor(i);
            return INVALID_ITEM_ID_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)identifier);
        }));
    }

    private void readComponents() throws CommandSyntaxException {
        this.reader.expect('[');
        this.callbacks.setSuggestor(this::suggestComponents);
        ReferenceArraySet set = new ReferenceArraySet();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            if (this.reader.canRead() && this.reader.peek() == '!') {
                this.reader.skip();
                this.callbacks.setSuggestor(this::suggestComponentsToRemove);
                componentType = ItemStringReader.Reader.readComponentType(this.reader);
                if (!set.add(componentType)) {
                    throw REPEATED_COMPONENT_EXCEPTION.create(componentType);
                }
                this.callbacks.onComponentRemoved(componentType);
                this.callbacks.setSuggestor(SUGGEST_DEFAULT);
                this.reader.skipWhitespace();
            } else {
                componentType = ItemStringReader.Reader.readComponentType(this.reader);
                if (!set.add(componentType)) {
                    throw REPEATED_COMPONENT_EXCEPTION.create(componentType);
                }
                this.callbacks.setSuggestor(this::suggestEqual);
                this.reader.skipWhitespace();
                this.reader.expect('=');
                this.callbacks.setSuggestor(SUGGEST_DEFAULT);
                this.reader.skipWhitespace();
                this.readComponentValue(ItemStringReader.this.snbtReader, ItemStringReader.this.ops, componentType);
                this.reader.skipWhitespace();
            }
            this.callbacks.setSuggestor(this::suggestEndOfComponent);
            if (!this.reader.canRead() || this.reader.peek() != ',') break;
            this.reader.skip();
            this.reader.skipWhitespace();
            this.callbacks.setSuggestor(this::suggestComponents);
            if (this.reader.canRead()) continue;
            throw COMPONENT_EXPECTED_EXCEPTION.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.expect(']');
        this.callbacks.setSuggestor(SUGGEST_DEFAULT);
    }

    public static ComponentType<?> readComponentType(StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw COMPONENT_EXPECTED_EXCEPTION.createWithContext((ImmutableStringReader)reader);
        }
        int i = reader.getCursor();
        Identifier identifier = Identifier.fromCommandInput(reader);
        ComponentType<?> componentType = Registries.DATA_COMPONENT_TYPE.get(identifier);
        if (componentType == null || componentType.shouldSkipSerialization()) {
            reader.setCursor(i);
            throw UNKNOWN_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)reader, (Object)identifier);
        }
        return componentType;
    }

    private <T, O> void readComponentValue(StringNbtReader<O> snbtReader, RegistryOps<O> ops, ComponentType<T> type) throws CommandSyntaxException {
        int i = this.reader.getCursor();
        O object = snbtReader.readAsArgument(this.reader);
        DataResult dataResult = type.getCodecOrThrow().parse(ops, object);
        this.callbacks.onComponentAdded(type, dataResult.getOrThrow(error -> {
            this.reader.setCursor(i);
            return MALFORMED_COMPONENT_EXCEPTION.createWithContext((ImmutableStringReader)this.reader, (Object)type.toString(), error);
        }));
    }

    private CompletableFuture<Suggestions> suggestBracket(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('['));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEndOfComponent(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(','));
            builder.suggest(String.valueOf(']'));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEqual(SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('='));
        }
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestItems(SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(ItemStringReader.this.itemRegistry.streamKeys().map(RegistryKey::getValue), builder);
    }

    private CompletableFuture<Suggestions> suggestComponents(SuggestionsBuilder builder) {
        builder.suggest(String.valueOf('!'));
        return this.suggestComponents(builder, String.valueOf('='));
    }

    private CompletableFuture<Suggestions> suggestComponentsToRemove(SuggestionsBuilder builder) {
        return this.suggestComponents(builder, "");
    }

    private CompletableFuture<Suggestions> suggestComponents(SuggestionsBuilder builder, String suffix) {
        String string = builder.getRemaining().toLowerCase(Locale.ROOT);
        CommandSource.forEachMatching(Registries.DATA_COMPONENT_TYPE.getEntrySet(), string, entry -> ((RegistryKey)entry.getKey()).getValue(), entry -> {
            ComponentType componentType = (ComponentType)entry.getValue();
            if (componentType.getCodec() != null) {
                Identifier identifier = ((RegistryKey)entry.getKey()).getValue();
                builder.suggest(String.valueOf(identifier) + suffix);
            }
        });
        return builder.buildFuture();
    }
}
