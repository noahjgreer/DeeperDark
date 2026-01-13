/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
class StatsScreen.StatsTab
extends GridScreenTab {
    protected final EntryListWidget<?> widget;

    public StatsScreen.StatsTab(Text title, EntryListWidget<?> widget) {
        super(title);
        this.grid.add(widget, 1, 1);
        this.widget = widget;
    }

    @Override
    public void refreshGrid(ScreenRect tabArea) {
        this.widget.position(StatsScreen.this.width, StatsScreen.this.layout.getContentHeight(), StatsScreen.this.layout.getHeaderHeight());
        super.refreshGrid(tabArea);
    }
}
