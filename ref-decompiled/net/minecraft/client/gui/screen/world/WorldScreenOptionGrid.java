/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.world.WorldScreenOptionGrid
 *  net.minecraft.client.gui.screen.world.WorldScreenOptionGrid$Builder
 *  net.minecraft.client.gui.screen.world.WorldScreenOptionGrid$Option
 *  net.minecraft.client.gui.widget.LayoutWidget
 */
package net.minecraft.client.gui.screen.world;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.world.WorldScreenOptionGrid;
import net.minecraft.client.gui.widget.LayoutWidget;

@Environment(value=EnvType.CLIENT)
class WorldScreenOptionGrid {
    private static final int BUTTON_WIDTH = 44;
    private final List<Option> options;
    private final LayoutWidget layout;

    WorldScreenOptionGrid(List<Option> options, LayoutWidget layout) {
        this.options = options;
        this.layout = layout;
    }

    public LayoutWidget getLayout() {
        return this.layout;
    }

    public void refresh() {
        this.options.forEach(Option::refresh);
    }

    public static Builder builder(int width) {
        return new Builder(width);
    }
}

