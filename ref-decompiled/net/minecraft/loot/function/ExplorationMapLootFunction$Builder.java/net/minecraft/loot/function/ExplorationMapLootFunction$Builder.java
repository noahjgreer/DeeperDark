/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.loot.function;

import net.minecraft.item.map.MapDecorationType;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.ExplorationMapLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.gen.structure.Structure;

public static class ExplorationMapLootFunction.Builder
extends ConditionalLootFunction.Builder<ExplorationMapLootFunction.Builder> {
    private TagKey<Structure> destination = DEFAULT_DESTINATION;
    private RegistryEntry<MapDecorationType> decoration = DEFAULT_DECORATION;
    private byte zoom = (byte)2;
    private int searchRadius = 50;
    private boolean skipExistingChunks = true;

    @Override
    protected ExplorationMapLootFunction.Builder getThisBuilder() {
        return this;
    }

    public ExplorationMapLootFunction.Builder withDestination(TagKey<Structure> destination) {
        this.destination = destination;
        return this;
    }

    public ExplorationMapLootFunction.Builder withDecoration(RegistryEntry<MapDecorationType> decoration) {
        this.decoration = decoration;
        return this;
    }

    public ExplorationMapLootFunction.Builder withZoom(byte zoom) {
        this.zoom = zoom;
        return this;
    }

    public ExplorationMapLootFunction.Builder searchRadius(int searchRadius) {
        this.searchRadius = searchRadius;
        return this;
    }

    public ExplorationMapLootFunction.Builder withSkipExistingChunks(boolean skipExistingChunks) {
        this.skipExistingChunks = skipExistingChunks;
        return this;
    }

    @Override
    public LootFunction build() {
        return new ExplorationMapLootFunction(this.getConditions(), this.destination, this.decoration, this.zoom, this.searchRadius, this.skipExistingChunks);
    }

    @Override
    protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
        return this.getThisBuilder();
    }
}
