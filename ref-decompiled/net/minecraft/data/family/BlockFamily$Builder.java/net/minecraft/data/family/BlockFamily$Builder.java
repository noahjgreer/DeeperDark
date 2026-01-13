/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.family;

import net.minecraft.block.Block;
import net.minecraft.data.family.BlockFamily;

public static class BlockFamily.Builder {
    private final BlockFamily family;

    public BlockFamily.Builder(Block baseBlock) {
        this.family = new BlockFamily(baseBlock);
    }

    public BlockFamily build() {
        return this.family;
    }

    public BlockFamily.Builder button(Block block) {
        this.family.variants.put(BlockFamily.Variant.BUTTON, block);
        return this;
    }

    public BlockFamily.Builder chiseled(Block block) {
        this.family.variants.put(BlockFamily.Variant.CHISELED, block);
        return this;
    }

    public BlockFamily.Builder mosaic(Block block) {
        this.family.variants.put(BlockFamily.Variant.MOSAIC, block);
        return this;
    }

    public BlockFamily.Builder cracked(Block block) {
        this.family.variants.put(BlockFamily.Variant.CRACKED, block);
        return this;
    }

    public BlockFamily.Builder cut(Block block) {
        this.family.variants.put(BlockFamily.Variant.CUT, block);
        return this;
    }

    public BlockFamily.Builder door(Block block) {
        this.family.variants.put(BlockFamily.Variant.DOOR, block);
        return this;
    }

    public BlockFamily.Builder customFence(Block block) {
        this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE, block);
        return this;
    }

    public BlockFamily.Builder fence(Block block) {
        this.family.variants.put(BlockFamily.Variant.FENCE, block);
        return this;
    }

    public BlockFamily.Builder customFenceGate(Block block) {
        this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, block);
        return this;
    }

    public BlockFamily.Builder fenceGate(Block block) {
        this.family.variants.put(BlockFamily.Variant.FENCE_GATE, block);
        return this;
    }

    public BlockFamily.Builder sign(Block block, Block wallBlock) {
        this.family.variants.put(BlockFamily.Variant.SIGN, block);
        this.family.variants.put(BlockFamily.Variant.WALL_SIGN, wallBlock);
        return this;
    }

    public BlockFamily.Builder slab(Block block) {
        this.family.variants.put(BlockFamily.Variant.SLAB, block);
        return this;
    }

    public BlockFamily.Builder stairs(Block block) {
        this.family.variants.put(BlockFamily.Variant.STAIRS, block);
        return this;
    }

    public BlockFamily.Builder pressurePlate(Block block) {
        this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, block);
        return this;
    }

    public BlockFamily.Builder polished(Block block) {
        this.family.variants.put(BlockFamily.Variant.POLISHED, block);
        return this;
    }

    public BlockFamily.Builder trapdoor(Block block) {
        this.family.variants.put(BlockFamily.Variant.TRAPDOOR, block);
        return this;
    }

    public BlockFamily.Builder wall(Block block) {
        this.family.variants.put(BlockFamily.Variant.WALL, block);
        return this;
    }

    public BlockFamily.Builder noGenerateModels() {
        this.family.generateModels = false;
        return this;
    }

    public BlockFamily.Builder noGenerateRecipes() {
        this.family.generateRecipes = false;
        return this;
    }

    public BlockFamily.Builder group(String group) {
        this.family.group = group;
        return this;
    }

    public BlockFamily.Builder unlockCriterionName(String unlockCriterionName) {
        this.family.unlockCriterionName = unlockCriterionName;
        return this;
    }
}
