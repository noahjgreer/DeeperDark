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
import net.minecraft.client.gui.widget.AxisGridWidget;
import net.minecraft.client.gui.widget.Widget;

@Environment(value=EnvType.CLIENT)
public static final class AxisGridWidget.DisplayAxis
extends Enum<AxisGridWidget.DisplayAxis> {
    public static final /* enum */ AxisGridWidget.DisplayAxis HORIZONTAL = new AxisGridWidget.DisplayAxis();
    public static final /* enum */ AxisGridWidget.DisplayAxis VERTICAL = new AxisGridWidget.DisplayAxis();
    private static final /* synthetic */ AxisGridWidget.DisplayAxis[] field_40791;

    public static AxisGridWidget.DisplayAxis[] values() {
        return (AxisGridWidget.DisplayAxis[])field_40791.clone();
    }

    public static AxisGridWidget.DisplayAxis valueOf(String string) {
        return Enum.valueOf(AxisGridWidget.DisplayAxis.class, string);
    }

    int getSameAxisLength(Widget widget) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> widget.getWidth();
            case 1 -> widget.getHeight();
        };
    }

    int getSameAxisLength(AxisGridWidget.Element element) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> element.getWidth();
            case 1 -> element.getHeight();
        };
    }

    int getOtherAxisLength(Widget widget) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> widget.getHeight();
            case 1 -> widget.getWidth();
        };
    }

    int getOtherAxisLength(AxisGridWidget.Element element) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> element.getHeight();
            case 1 -> element.getWidth();
        };
    }

    void setSameAxisCoordinate(AxisGridWidget.Element element, int low) {
        switch (this.ordinal()) {
            case 0: {
                element.setX(low, element.getWidth());
                break;
            }
            case 1: {
                element.setY(low, element.getHeight());
            }
        }
    }

    void setOtherAxisCoordinate(AxisGridWidget.Element element, int low, int high) {
        switch (this.ordinal()) {
            case 0: {
                element.setY(low, high);
                break;
            }
            case 1: {
                element.setX(low, high);
            }
        }
    }

    int getSameAxisCoordinate(Widget widget) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> widget.getX();
            case 1 -> widget.getY();
        };
    }

    int getOtherAxisCoordinate(Widget widget) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> widget.getY();
            case 1 -> widget.getX();
        };
    }

    private static /* synthetic */ AxisGridWidget.DisplayAxis[] method_46501() {
        return new AxisGridWidget.DisplayAxis[]{HORIZONTAL, VERTICAL};
    }

    static {
        field_40791 = AxisGridWidget.DisplayAxis.method_46501();
    }
}
