/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatComparators
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.floats.FloatComparators;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudEntryCategory;
import net.minecraft.client.gui.screen.DebugOptionsScreen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DebugOptionsScreen.OptionsListWidget
extends ElementListWidget<DebugOptionsScreen.AbstractEntry> {
    private static final Comparator<Map.Entry<Identifier, DebugHudEntry>> ENTRY_COMPARATOR = (a, b) -> {
        int i = FloatComparators.NATURAL_COMPARATOR.compare(((DebugHudEntry)a.getValue()).getCategory().sortKey(), ((DebugHudEntry)b.getValue()).getCategory().sortKey());
        if (i != 0) {
            return i;
        }
        return ((Identifier)a.getKey()).compareTo((Identifier)b.getKey());
    };
    private static final int ITEM_HEIGHT = 20;

    public DebugOptionsScreen.OptionsListWidget() {
        super(MinecraftClient.getInstance(), DebugOptionsScreen.this.width, DebugOptionsScreen.this.layout.getContentHeight(), DebugOptionsScreen.this.layout.getHeaderHeight(), 20);
        this.fillEntries("");
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.renderWidget(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public int getRowWidth() {
        return 350;
    }

    public void init() {
        this.children().forEach(DebugOptionsScreen.AbstractEntry::init);
    }

    public void fillEntries(String searchString) {
        this.clearEntries();
        ArrayList<Map.Entry<Identifier, DebugHudEntry>> list = new ArrayList<Map.Entry<Identifier, DebugHudEntry>>(DebugHudEntries.getEntries().entrySet());
        list.sort(ENTRY_COMPARATOR);
        DebugHudEntryCategory debugHudEntryCategory = null;
        for (Map.Entry entry : list) {
            if (!((Identifier)entry.getKey()).getPath().contains(searchString)) continue;
            DebugHudEntryCategory debugHudEntryCategory2 = ((DebugHudEntry)entry.getValue()).getCategory();
            if (!debugHudEntryCategory2.equals(debugHudEntryCategory)) {
                this.addEntry(new DebugOptionsScreen.Category(DebugOptionsScreen.this, debugHudEntryCategory2.label()));
                debugHudEntryCategory = debugHudEntryCategory2;
            }
            this.addEntry(new DebugOptionsScreen.Entry(DebugOptionsScreen.this, (Identifier)entry.getKey()));
        }
        this.refreshScreen();
    }

    private void refreshScreen() {
        this.refreshScroll();
        DebugOptionsScreen.this.narrateScreenIfNarrationEnabled(true);
    }
}
