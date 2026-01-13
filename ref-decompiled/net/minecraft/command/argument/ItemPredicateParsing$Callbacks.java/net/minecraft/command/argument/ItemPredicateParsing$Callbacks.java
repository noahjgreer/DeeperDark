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
import java.util.stream.Stream;
import net.minecraft.util.Identifier;

public static interface ItemPredicateParsing.Callbacks<T, C, P> {
    public T itemMatchPredicate(ImmutableStringReader var1, Identifier var2) throws CommandSyntaxException;

    public Stream<Identifier> streamItemIds();

    public T tagMatchPredicate(ImmutableStringReader var1, Identifier var2) throws CommandSyntaxException;

    public Stream<Identifier> streamTags();

    public C componentCheck(ImmutableStringReader var1, Identifier var2) throws CommandSyntaxException;

    public Stream<Identifier> streamComponentIds();

    public T componentMatchPredicate(ImmutableStringReader var1, C var2, Dynamic<?> var3) throws CommandSyntaxException;

    public T componentPresencePredicate(ImmutableStringReader var1, C var2);

    public P subPredicateCheck(ImmutableStringReader var1, Identifier var2) throws CommandSyntaxException;

    public Stream<Identifier> streamSubPredicateIds();

    public T subPredicatePredicate(ImmutableStringReader var1, P var2, Dynamic<?> var3) throws CommandSyntaxException;

    public T negate(T var1);

    public T anyOf(List<T> var1);
}
