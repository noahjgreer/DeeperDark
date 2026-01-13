/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.tab.GridScreenTab
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class GridScreenTab
implements Tab {
    private final Text title;
    protected final GridWidget grid = new GridWidget();

    public GridScreenTab(Text title) {
        this.title = title;
    }

    public Text getTitle() {
        return this.title;
    }

    public Text getNarratedHint() {
        return Text.empty();
    }

    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.grid.forEachChild(consumer);
    }

    public void refreshGrid(ScreenRect tabArea) {
        this.grid.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.grid, (ScreenRect)tabArea, (float)0.5f, (float)0.16666667f);
    }
}

