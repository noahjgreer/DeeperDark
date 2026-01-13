/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.tag.vanilla;

import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

static class VanillaItemTagProvider.ItemTagBuilder
implements ProvidedTagBuilder<Block, Block> {
    private final ProvidedTagBuilder<Item, Item> parent;

    public VanillaItemTagProvider.ItemTagBuilder(ProvidedTagBuilder<Item, Item> parent) {
        this.parent = parent;
    }

    @Override
    public ProvidedTagBuilder<Block, Block> add(Block block) {
        this.parent.add(Objects.requireNonNull(block.asItem()));
        return this;
    }

    @Override
    public ProvidedTagBuilder<Block, Block> addOptional(Block block) {
        this.parent.addOptional(Objects.requireNonNull(block.asItem()));
        return this;
    }

    private static TagKey<Item> getItemTag(TagKey<Block> tag) {
        return TagKey.of(RegistryKeys.ITEM, tag.id());
    }

    @Override
    public ProvidedTagBuilder<Block, Block> addTag(TagKey<Block> tag) {
        this.parent.addTag(VanillaItemTagProvider.ItemTagBuilder.getItemTag(tag));
        return this;
    }

    @Override
    public ProvidedTagBuilder<Block, Block> addOptionalTag(TagKey<Block> tag) {
        this.parent.addOptionalTag(VanillaItemTagProvider.ItemTagBuilder.getItemTag(tag));
        return this;
    }
}
