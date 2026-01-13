/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.item;

import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

static class Item.TooltipContext.2
implements Item.TooltipContext {
    final /* synthetic */ World field_51354;

    Item.TooltipContext.2(World world) {
        this.field_51354 = world;
    }

    @Override
    public RegistryWrapper.WrapperLookup getRegistryLookup() {
        return this.field_51354.getRegistryManager();
    }

    @Override
    public float getUpdateTickRate() {
        return this.field_51354.getTickManager().getTickRate();
    }

    @Override
    public MapState getMapState(MapIdComponent mapIdComponent) {
        return this.field_51354.getMapState(mapIdComponent);
    }

    @Override
    public boolean isDifficultyPeaceful() {
        return this.field_51354.getDifficulty() == Difficulty.PEACEFUL;
    }
}
