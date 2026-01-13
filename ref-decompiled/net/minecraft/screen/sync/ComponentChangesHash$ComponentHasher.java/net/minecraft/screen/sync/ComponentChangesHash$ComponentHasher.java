/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.screen.sync;

import java.util.function.Function;
import net.minecraft.component.Component;

@FunctionalInterface
public static interface ComponentChangesHash.ComponentHasher
extends Function<Component<?>, Integer> {
}
