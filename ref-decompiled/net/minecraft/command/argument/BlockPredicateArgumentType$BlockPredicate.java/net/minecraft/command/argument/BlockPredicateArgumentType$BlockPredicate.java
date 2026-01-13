/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.argument;

import java.util.function.Predicate;
import net.minecraft.block.pattern.CachedBlockPosition;

public static interface BlockPredicateArgumentType.BlockPredicate
extends Predicate<CachedBlockPosition> {
    public boolean hasNbt();
}
