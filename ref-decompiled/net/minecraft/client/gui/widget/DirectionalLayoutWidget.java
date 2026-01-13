/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget$DisplayAxis
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.util.Util
 */
package net.minecraft.client.gui.widget;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class DirectionalLayoutWidget
implements LayoutWidget {
    private final GridWidget grid;
    private final DisplayAxis axis;
    private int currentIndex = 0;

    private DirectionalLayoutWidget(DisplayAxis axis) {
        this(0, 0, axis);
    }

    public DirectionalLayoutWidget(int x, int y, DisplayAxis axis) {
        this.grid = new GridWidget(x, y);
        this.axis = axis;
    }

    public DirectionalLayoutWidget spacing(int spacing) {
        this.axis.setSpacing(this.grid, spacing);
        return this;
    }

    public Positioner copyPositioner() {
        return this.grid.copyPositioner();
    }

    public Positioner getMainPositioner() {
        return this.grid.getMainPositioner();
    }

    public <T extends Widget> T add(T widget, Positioner positioner) {
        return (T)this.axis.add(this.grid, widget, this.currentIndex++, positioner);
    }

    public <T extends Widget> T add(T widget) {
        return (T)this.add(widget, this.copyPositioner());
    }

    public <T extends Widget> T add(T widget, Consumer<Positioner> callback) {
        return (T)this.axis.add(this.grid, widget, this.currentIndex++, (Positioner)Util.make((Object)this.copyPositioner(), callback));
    }

    public void forEachElement(Consumer<Widget> consumer) {
        this.grid.forEachElement(consumer);
    }

    public void refreshPositions() {
        this.grid.refreshPositions();
    }

    public int getWidth() {
        return this.grid.getWidth();
    }

    public int getHeight() {
        return this.grid.getHeight();
    }

    public void setX(int x) {
        this.grid.setX(x);
    }

    public void setY(int y) {
        this.grid.setY(y);
    }

    public int getX() {
        return this.grid.getX();
    }

    public int getY() {
        return this.grid.getY();
    }

    public static DirectionalLayoutWidget vertical() {
        return new DirectionalLayoutWidget(DisplayAxis.VERTICAL);
    }

    public static DirectionalLayoutWidget horizontal() {
        return new DirectionalLayoutWidget(DisplayAxis.HORIZONTAL);
    }
}

