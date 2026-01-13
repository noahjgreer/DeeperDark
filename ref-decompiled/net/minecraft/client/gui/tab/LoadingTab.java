/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.tab.LoadingTab
 *  net.minecraft.client.gui.tab.Tab
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.LoadingWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.tab;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LoadingWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class LoadingTab
implements Tab {
    private final Text title;
    private final Text narratedHint;
    protected final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical();

    public LoadingTab(TextRenderer textRenderer, Text title, Text narratedHint) {
        this.title = title;
        this.narratedHint = narratedHint;
        LoadingWidget loadingWidget = new LoadingWidget(textRenderer, narratedHint);
        this.layout.getMainPositioner().alignVerticalCenter().alignHorizontalCenter();
        this.layout.add((Widget)loadingWidget, positioner -> positioner.marginBottom(30));
    }

    public Text getTitle() {
        return this.title;
    }

    public Text getNarratedHint() {
        return this.narratedHint;
    }

    public void forEachChild(Consumer<ClickableWidget> consumer) {
        this.layout.forEachChild(consumer);
    }

    public void refreshGrid(ScreenRect tabArea) {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)tabArea, (float)0.5f, (float)0.5f);
    }
}

