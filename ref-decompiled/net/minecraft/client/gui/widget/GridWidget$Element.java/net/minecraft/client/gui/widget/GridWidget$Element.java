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
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.WrapperWidget;

@Environment(value=EnvType.CLIENT)
static class GridWidget.Element
extends WrapperWidget.WrappedElement {
    final int row;
    final int column;
    final int occupiedRows;
    final int occupiedColumns;

    GridWidget.Element(Widget widget, int row, int column, int occupiedRows, int occupiedColumns, Positioner positioner) {
        super(widget, positioner.toImpl());
        this.row = row;
        this.column = column;
        this.occupiedRows = occupiedRows;
        this.occupiedColumns = occupiedColumns;
    }

    public int getRowEnd() {
        return this.row + this.occupiedRows - 1;
    }

    public int getColumnEnd() {
        return this.column + this.occupiedColumns - 1;
    }
}
