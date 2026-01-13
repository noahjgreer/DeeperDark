/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command.argument;

import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Property;
import org.jspecify.annotations.Nullable;

public record BlockArgumentParser.BlockResult(BlockState blockState, Map<Property<?>, Comparable<?>> properties, @Nullable NbtCompound nbt) {
}
