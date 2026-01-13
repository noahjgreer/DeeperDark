/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public final class GridWidget.Adder {
    private final int columns;
    private int totalOccupiedColumns;

    GridWidget.Adder(int columns) {
        this.columns = columns;
    }

    public <T extends Widget> T add(T widget) {
        return this.add(widget, 1);
    }

    public <T extends Widget> T add(T widget, int occupiedColumns) {
        return this.add(widget, occupiedColumns, this.getMainPositioner());
    }

    public <T extends Widget> T add(T widget, Positioner positioner) {
        return this.add(widget, 1, positioner);
    }

    public <T extends Widget> T add(T widget, int occupiedColumns, Positioner positioner) {
        int i = this.totalOccupiedColumns / this.columns;
        int j = this.totalOccupiedColumns % this.columns;
        if (j + occupiedColumns > this.columns) {
            ++i;
            j = 0;
            this.totalOccupiedColumns = MathHelper.roundUpToMultiple(this.totalOccupiedColumns, this.columns);
        }
        this.totalOccupiedColumns += occupiedColumns;
        return GridWidget.this.add(widget, i, j, 1, occupiedColumns, positioner);
    }

    public GridWidget getGridWidget() {
        return GridWidget.this;
    }

    public Positioner copyPositioner() {
        return GridWidget.this.copyPositioner();
    }

    public Positioner getMainPositioner() {
        return GridWidget.this.getMainPositioner();
    }
}
