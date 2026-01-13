/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.tag.vanilla;

import net.minecraft.block.Block;
import net.minecraft.data.tag.ProvidedTagBuilder;
import net.minecraft.data.tag.vanilla.VanillaBlockItemTags;
import net.minecraft.data.tag.vanilla.VanillaItemTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

class VanillaItemTagProvider.1
extends VanillaBlockItemTags {
    VanillaItemTagProvider.1() {
    }

    @Override
    protected ProvidedTagBuilder<Block, Block> builder(TagKey<Block> blockTag, TagKey<Item> itemTag) {
        return new VanillaItemTagProvider.ItemTagBuilder(VanillaItemTagProvider.this.builder(itemTag));
    }
}
