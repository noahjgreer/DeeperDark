/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command.argument;

import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.state.property.Property;
import org.jspecify.annotations.Nullable;

static class BlockPredicateArgumentType.TagPredicate
implements BlockPredicateArgumentType.BlockPredicate {
    private final RegistryEntryList<Block> tag;
    private final @Nullable NbtCompound nbt;
    private final Map<String, String> properties;

    BlockPredicateArgumentType.TagPredicate(RegistryEntryList<Block> tag, Map<String, String> properties, @Nullable NbtCompound nbt) {
        this.tag = tag;
        this.properties = properties;
        this.nbt = nbt;
    }

    @Override
    public boolean test(CachedBlockPosition cachedBlockPosition) {
        BlockState blockState = cachedBlockPosition.getBlockState();
        if (!blockState.isIn(this.tag)) {
            return false;
        }
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            Property<?> property = blockState.getBlock().getStateManager().getProperty(entry.getKey());
            if (property == null) {
                return false;
            }
            Comparable comparable = property.parse(entry.getValue()).orElse(null);
            if (comparable == null) {
                return false;
            }
            if (blockState.get(property) == comparable) continue;
            return false;
        }
        if (this.nbt != null) {
            BlockEntity blockEntity = cachedBlockPosition.getBlockEntity();
            return blockEntity != null && NbtHelper.matches(this.nbt, blockEntity.createNbtWithIdentifyingData(cachedBlockPosition.getWorld().getRegistryManager()), true);
        }
        return true;
    }

    @Override
    public boolean hasNbt() {
        return this.nbt != null;
    }

    @Override
    public /* synthetic */ boolean test(Object context) {
        return this.test((CachedBlockPosition)context);
    }
}
