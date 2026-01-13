/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.command.argument.ItemPredicateArgumentType;
import net.minecraft.command.argument.ItemPredicateParsing;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

static class ItemPredicateArgumentType.Context
implements ItemPredicateParsing.Callbacks<Predicate<ItemStack>, ItemPredicateArgumentType.ComponentCheck, ItemPredicateArgumentType.SubPredicateCheck> {
    private final RegistryWrapper.WrapperLookup registries;
    private final RegistryWrapper.Impl<Item> itemRegistryWrapper;
    private final RegistryWrapper.Impl<ComponentType<?>> dataComponentTypeRegistryWrapper;
    private final RegistryWrapper.Impl<ComponentPredicate.Type<?>> itemSubPredicateTypeRegistryWrapper;

    ItemPredicateArgumentType.Context(RegistryWrapper.WrapperLookup registries) {
        this.registries = registries;
        this.itemRegistryWrapper = registries.getOrThrow(RegistryKeys.ITEM);
        this.dataComponentTypeRegistryWrapper = registries.getOrThrow(RegistryKeys.DATA_COMPONENT_TYPE);
        this.itemSubPredicateTypeRegistryWrapper = registries.getOrThrow(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE);
    }

    @Override
    public Predicate<ItemStack> itemMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
        RegistryEntry.Reference<Item> reference = this.itemRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> INVALID_ITEM_ID_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
        return stack -> stack.itemMatches(reference);
    }

    @Override
    public Predicate<ItemStack> tagMatchPredicate(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
        RegistryEntryList registryEntryList = this.itemRegistryWrapper.getOptional(TagKey.of(RegistryKeys.ITEM, identifier)).orElseThrow(() -> UNKNOWN_ITEM_TAG_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
        return stack -> stack.isIn(registryEntryList);
    }

    @Override
    public ItemPredicateArgumentType.ComponentCheck componentCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
        ItemPredicateArgumentType.ComponentCheck componentCheck = SPECIAL_COMPONENT_CHECKS.get(identifier);
        if (componentCheck != null) {
            return componentCheck;
        }
        ComponentType componentType = this.dataComponentTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_TYPE, identifier)).map(RegistryEntry::value).orElseThrow(() -> UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
        return ItemPredicateArgumentType.ComponentCheck.read(immutableStringReader, identifier, componentType);
    }

    @Override
    public Predicate<ItemStack> componentMatchPredicate(ImmutableStringReader immutableStringReader, ItemPredicateArgumentType.ComponentCheck componentCheck, Dynamic<?> dynamic) throws CommandSyntaxException {
        return componentCheck.createPredicate(immutableStringReader, RegistryOps.withRegistry(dynamic, this.registries));
    }

    @Override
    public Predicate<ItemStack> componentPresencePredicate(ImmutableStringReader immutableStringReader, ItemPredicateArgumentType.ComponentCheck componentCheck) {
        return componentCheck.presenceChecker;
    }

    @Override
    public ItemPredicateArgumentType.SubPredicateCheck subPredicateCheck(ImmutableStringReader immutableStringReader, Identifier identifier) throws CommandSyntaxException {
        ItemPredicateArgumentType.SubPredicateCheck subPredicateCheck = SPECIAL_SUB_PREDICATE_CHECKS.get(identifier);
        if (subPredicateCheck != null) {
            return subPredicateCheck;
        }
        return this.itemSubPredicateTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_PREDICATE_TYPE, identifier)).map(ItemPredicateArgumentType.SubPredicateCheck::new).or(() -> this.dataComponentTypeRegistryWrapper.getOptional(RegistryKey.of(RegistryKeys.DATA_COMPONENT_TYPE, identifier)).map(ItemPredicateArgumentType::hasComponentCheck)).orElseThrow(() -> UNKNOWN_ITEM_PREDICATE_EXCEPTION.createWithContext(immutableStringReader, (Object)identifier));
    }

    @Override
    public Predicate<ItemStack> subPredicatePredicate(ImmutableStringReader immutableStringReader, ItemPredicateArgumentType.SubPredicateCheck subPredicateCheck, Dynamic<?> dynamic) throws CommandSyntaxException {
        return subPredicateCheck.createPredicate(immutableStringReader, RegistryOps.withRegistry(dynamic, this.registries));
    }

    @Override
    public Stream<Identifier> streamItemIds() {
        return this.itemRegistryWrapper.streamKeys().map(RegistryKey::getValue);
    }

    @Override
    public Stream<Identifier> streamTags() {
        return this.itemRegistryWrapper.streamTagKeys().map(TagKey::id);
    }

    @Override
    public Stream<Identifier> streamComponentIds() {
        return Stream.concat(SPECIAL_COMPONENT_CHECKS.keySet().stream(), this.dataComponentTypeRegistryWrapper.streamEntries().filter(entry -> !((ComponentType)entry.value()).shouldSkipSerialization()).map(entry -> entry.registryKey().getValue()));
    }

    @Override
    public Stream<Identifier> streamSubPredicateIds() {
        return Stream.concat(SPECIAL_SUB_PREDICATE_CHECKS.keySet().stream(), this.itemSubPredicateTypeRegistryWrapper.streamKeys().map(RegistryKey::getValue));
    }

    @Override
    public Predicate<ItemStack> negate(Predicate<ItemStack> predicate) {
        return predicate.negate();
    }

    @Override
    public Predicate<ItemStack> anyOf(List<Predicate<ItemStack>> list) {
        return Util.anyOf(list);
    }

    @Override
    public /* synthetic */ Object anyOf(List predicates) {
        return this.anyOf(predicates);
    }

    @Override
    public /* synthetic */ Object subPredicatePredicate(ImmutableStringReader reader, Object check, Dynamic dynamic) throws CommandSyntaxException {
        return this.subPredicatePredicate(reader, (ItemPredicateArgumentType.SubPredicateCheck)check, dynamic);
    }

    @Override
    public /* synthetic */ Object subPredicateCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
        return this.subPredicateCheck(reader, id);
    }

    @Override
    public /* synthetic */ Object componentCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
        return this.componentCheck(reader, id);
    }

    @Override
    public /* synthetic */ Object tagMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
        return this.tagMatchPredicate(reader, id);
    }

    @Override
    public /* synthetic */ Object itemMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException {
        return this.itemMatchPredicate(reader, id);
    }
}
