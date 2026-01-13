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
import org.jspecify.annotations.Nullable;

class Item.TooltipContext.1
implements Item.TooltipContext {
    Item.TooltipContext.1() {
    }

    @Override
    public  @Nullable RegistryWrapper.WrapperLookup getRegistryLookup() {
        return null;
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
