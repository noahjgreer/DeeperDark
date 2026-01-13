/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

record ItemPredicateArgumentType.SubPredicateCheck(Identifier id, Decoder<? extends Predicate<ItemStack>> type) {
    public ItemPredicateArgumentType.SubPredicateCheck(RegistryEntry.Reference<ComponentPredicate.Type<?>> type) {
        this(type.registryKey().getValue(), (Decoder<? extends Predicate<ItemStack>>)type.value().getPredicateCodec().map(predicate -> predicate::test));
    }

    public Predicate<ItemStack> createPredicate(ImmutableStringReader reader, Dynamic<?> value) throws CommandSyntaxException {
        DataResult dataResult = this.type.parse(value);
        return (Predicate)dataResult.getOrThrow(error -> MALFORMED_ITEM_PREDICATE_EXCEPTION.createWithContext(reader, (Object)this.id.toString(), error));
    }
}
