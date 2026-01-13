/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.item;

import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.Item;
import net.minecraft.item.map.MapState;
import net.minecraft.registry.RegistryWrapper;
import org.jspecify.annotations.Nullable;

static class Item.TooltipContext.3
implements Item.TooltipContext {
    final /* synthetic */ RegistryWrapper.WrapperLookup field_51355;

    Item.TooltipContext.3(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.field_51355 = wrapperLookup;
    }

    @Override
    public RegistryWrapper.WrapperLookup getRegistryLookup() {
        return this.field_51355;
    }

    @Override
    public float getUpdateTickRate() {
        return 20.0f;
    }

    @Override
    public @Nullable MapState getMapState(MapIdComponent mapIdComponent) {
        return null;
    }

    @Override
    public boolean isDifficultyPeaceful() {
        return false;
    }
}
