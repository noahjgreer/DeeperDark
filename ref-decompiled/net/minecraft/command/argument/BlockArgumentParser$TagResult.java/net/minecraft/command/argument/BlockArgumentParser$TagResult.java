/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command.argument;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntryList;
import org.jspecify.annotations.Nullable;

public record BlockArgumentParser.TagResult(RegistryEntryList<Block> tag, Map<String, String> vagueProperties, @Nullable NbtCompound nbt) {
}
