/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Decoder
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

static final class ItemPredicateArgumentType.ComponentCheck
extends Record {
    private final Identifier id;
    final Predicate<ItemStack> presenceChecker;
    private final Decoder<? extends Predicate<ItemStack>> valueChecker;

    ItemPredicateArgumentType.ComponentCheck(Identifier id, Predicate<ItemStack> presenceChecker, Decoder<? extends Predicate<ItemStack>> valueChecker) {
        this.id = id;
        this.presenceChecker = presenceChecker;
        this.valueChecker = valueChecker;
    }

    public static <T> ItemPredicateArgumentType.ComponentCheck read(ImmutableStringReader reader, Identifier id, ComponentType<T> type) throws CommandSyntaxException {
        Codec<T> codec = type.getCodec();
        if (codec == null) {
            throw UNKNOWN_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, (Object)id);
        }
        return new ItemPredicateArgumentType.ComponentCheck(id, stack -> stack.contains(type), (Decoder<? extends Predicate<ItemStack>>)codec.map(expected -> stack -> {
            Object object2 = stack.get(type);
            return Objects.equals(expected, object2);
        }));
    }

    public Predicate<ItemStack> createPredicate(ImmutableStringReader reader, Dynamic<?> value) throws CommandSyntaxException {
        DataResult dataResult = this.valueChecker.parse(value);
        return (Predicate)dataResult.getOrThrow(error -> MALFORMED_ITEM_COMPONENT_EXCEPTION.createWithContext(reader, (Object)this.id.toString(), error));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemPredicateArgumentType.ComponentCheck.class, "id;presenceChecker;valueChecker", "id", "presenceChecker", "valueChecker"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemPredicateArgumentType.ComponentCheck.class, "id;presenceChecker;valueChecker", "id", "presenceChecker", "valueChecker"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemPredicateArgumentType.ComponentCheck.class, "id;presenceChecker;valueChecker", "id", "presenceChecker", "valueChecker"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }

    public Predicate<ItemStack> presenceChecker() {
        return this.presenceChecker;
    }

    public Decoder<? extends Predicate<ItemStack>> valueChecker() {
        return this.valueChecker;
    }
}
