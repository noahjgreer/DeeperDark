/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.command.argument;

import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Property;
import org.jspecify.annotations.Nullable;

static class BlockPredicateArgumentType.StatePredicate
implements BlockPredicateArgumentType.BlockPredicate {
    private final BlockState state;
    private final Set<Property<?>> properties;
    private final @Nullable NbtCompound nbt;

    public BlockPredicateArgumentType.StatePredicate(BlockState state, Set<Property<?>> properties, @Nullable NbtCompound nbt) {
        this.state = state;
        this.properties = properties;
        this.nbt = nbt;
    }

    @Override
    public boolean test(CachedBlockPosition cachedBlockPosition) {
        BlockState blockState = cachedBlockPosition.getBlockState();
        if (!blockState.isOf(this.state.getBlock())) {
            return false;
        }
        for (Property<?> property : this.properties) {
            if (blockState.get(property) == this.state.get(property)) continue;
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
