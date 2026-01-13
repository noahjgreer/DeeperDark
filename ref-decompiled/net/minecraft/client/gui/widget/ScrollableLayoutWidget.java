/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.ScrollableLayoutWidget
 *  net.minecraft.client.gui.widget.ScrollableLayoutWidget$Container
 *  net.minecraft.client.gui.widget.Widget
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.ScrollableLayoutWidget;
import net.minecraft.client.gui.widget.Widget;

@Environment(value=EnvType.CLIENT)
public class ScrollableLayoutWidget
implements LayoutWidget {
    private static final int field_60715 = 4;
    private static final int field_61059 = 10;
    final LayoutWidget layout;
    private final Container container;
    private int width;
    private int height;

    public ScrollableLayoutWidget(MinecraftClient client, LayoutWidget layout, int height) {
        this.layout = layout;
        this.container = new Container(this, client, 0, height);
    }

    public void setWidth(int width) {
        this.width = width;
        this.container.setWidth(Math.max(this.layout.getWidth(), width));
    }

    public void setHeight(int height) {
        this.height = height;
        this.container.setHeight(Math.min(this.layout.getHeight(), height));
        this.container.refreshScroll();
    }

    public void refreshPositions() {
        this.layout.refreshPositions();
        int i = this.layout.getWidth();
        this.container.setWidth(Math.max(i + 20, this.width));
        this.container.setHeight(Math.min(this.layout.getHeight(), this.height));
        this.container.refreshScroll();
    }

    public void forEachElement(Consumer<Widget> consumer) {
        consumer.accept((Widget)this.container);
    }

    public void setX(int x) {
        this.container.setX(x);
    }

    public void setY(int y) {
        this.container.setY(y);
    }

    public int getX() {
        return this.container.getX();
    }

    public int getY() {
        return this.container.getY();
    }

    public int getWidth() {
        return this.container.getWidth();
    }

    public int getHeight() {
        return this.container.getHeight();
    }
}

