/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;

@Environment(value=EnvType.CLIENT)
public static class TabNavigationWidget.Builder {
    private final int width;
    private final TabManager tabManager;
    private final List<Tab> tabs = new ArrayList<Tab>();

    TabNavigationWidget.Builder(TabManager tabManager, int width) {
        this.tabManager = tabManager;
        this.width = width;
    }

    public TabNavigationWidget.Builder tabs(Tab ... tabs) {
        Collections.addAll(this.tabs, tabs);
        return this;
    }

    public TabNavigationWidget build() {
        return new TabNavigationWidget(this.width, this.tabManager, this.tabs);
    }
}
