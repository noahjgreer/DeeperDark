/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.predicate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public static class BlockPredicate.Builder {
    private Optional<RegistryEntryList<Block>> blocks = Optional.empty();
    private Optional<StatePredicate> state = Optional.empty();
    private Optional<NbtPredicate> nbt = Optional.empty();
    private ComponentsPredicate components = ComponentsPredicate.EMPTY;

    private BlockPredicate.Builder() {
    }

    public static BlockPredicate.Builder create() {
        return new BlockPredicate.Builder();
    }

    public BlockPredicate.Builder blocks(RegistryEntryLookup<Block> blockRegistry, Block ... blocks) {
        return this.blocks(blockRegistry, Arrays.asList(blocks));
    }

    public BlockPredicate.Builder blocks(RegistryEntryLookup<Block> blockRegistry, Collection<Block> blocks) {
        this.blocks = Optional.of(RegistryEntryList.of(Block::getRegistryEntry, blocks));
        return this;
    }

    public BlockPredicate.Builder tag(RegistryEntryLookup<Block> blockRegistry, TagKey<Block> tag) {
        this.blocks = Optional.of(blockRegistry.getOrThrow(tag));
        return this;
    }

    public BlockPredicate.Builder nbt(NbtCompound nbt) {
        this.nbt = Optional.of(new NbtPredicate(nbt));
        return this;
    }

    public BlockPredicate.Builder state(StatePredicate.Builder state) {
        this.state = state.build();
        return this;
    }

    public BlockPredicate.Builder components(ComponentsPredicate components) {
        this.components = components;
        return this;
    }

    public BlockPredicate build() {
        return new BlockPredicate(this.blocks, this.state, this.nbt, this.components);
    }
}
