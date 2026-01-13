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

@Environment(value=EnvType.CLIENT)
public static final class DirectionalLayoutWidget.DisplayAxis
extends Enum<DirectionalLayoutWidget.DisplayAxis> {
    public static final /* enum */ DirectionalLayoutWidget.DisplayAxis HORIZONTAL = new DirectionalLayoutWidget.DisplayAxis();
    public static final /* enum */ DirectionalLayoutWidget.DisplayAxis VERTICAL = new DirectionalLayoutWidget.DisplayAxis();
    private static final /* synthetic */ DirectionalLayoutWidget.DisplayAxis[] field_45405;

    public static DirectionalLayoutWidget.DisplayAxis[] values() {
        return (DirectionalLayoutWidget.DisplayAxis[])field_45405.clone();
    }

    public static DirectionalLayoutWidget.DisplayAxis valueOf(String string) {
        return Enum.valueOf(DirectionalLayoutWidget.DisplayAxis.class, string);
    }

    void setSpacing(GridWidget grid, int spacing) {
        switch (this.ordinal()) {
            case 0: {
                grid.setColumnSpacing(spacing);
                break;
            }
            case 1: {
                grid.setRowSpacing(spacing);
            }
        }
    }

    public <T extends Widget> T add(GridWidget grid, T widget, int index, Positioner positioner) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> grid.add(widget, 0, index, positioner);
            case 1 -> grid.add(widget, index, 0, positioner);
        };
    }

    private static /* synthetic */ DirectionalLayoutWidget.DisplayAxis[] method_52743() {
        return new DirectionalLayoutWidget.DisplayAxis[]{HORIZONTAL, VERTICAL};
    }

    static {
        field_45405 = DirectionalLayoutWidget.DisplayAxis.method_52743();
    }
}
