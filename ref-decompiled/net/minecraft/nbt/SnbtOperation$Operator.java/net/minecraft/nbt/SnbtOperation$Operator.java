/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.serialization.DynamicOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import net.minecraft.util.packrat.ParsingState;
import org.jspecify.annotations.Nullable;

public static interface SnbtOperation.Operator {
    public <T> @Nullable T apply(DynamicOps<T> var1, List<T> var2, ParsingState<StringReader> var3);
}
