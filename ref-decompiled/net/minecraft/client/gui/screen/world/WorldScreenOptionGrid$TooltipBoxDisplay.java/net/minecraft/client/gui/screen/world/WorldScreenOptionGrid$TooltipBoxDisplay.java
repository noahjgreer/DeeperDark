/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
static final class WorldScreenOptionGrid.TooltipBoxDisplay
extends Record {
    final int maxInfoRows;
    final boolean alwaysMaxHeight;

    WorldScreenOptionGrid.TooltipBoxDisplay(int maxInfoRows, boolean alwaysMaxHeight) {
        this.maxInfoRows = maxInfoRows;
        this.alwaysMaxHeight = alwaysMaxHeight;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{WorldScreenOptionGrid.TooltipBoxDisplay.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{WorldScreenOptionGrid.TooltipBoxDisplay.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{WorldScreenOptionGrid.TooltipBoxDisplay.class, "maxInfoRows;alwaysMaxHeight", "maxInfoRows", "alwaysMaxHeight"}, this, object);
    }

    public int maxInfoRows() {
        return this.maxInfoRows;
    }

    public boolean alwaysMaxHeight() {
        return this.alwaysMaxHeight;
    }
}
