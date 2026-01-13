/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.ScreenPos
 *  net.minecraft.client.gui.ScreenPos$1
 *  net.minecraft.client.gui.navigation.NavigationAxis
 *  net.minecraft.client.gui.navigation.NavigationDirection
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.navigation.NavigationAxis;
import net.minecraft.client.gui.navigation.NavigationDirection;

@Environment(value=EnvType.CLIENT)
public record ScreenPos(int x, int y) {
    private final int x;
    private final int y;

    public ScreenPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static ScreenPos of(NavigationAxis axis, int sameAxis, int otherAxis) {
        return switch (1.field_41833[axis.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> new ScreenPos(sameAxis, otherAxis);
            case 2 -> new ScreenPos(otherAxis, sameAxis);
        };
    }

    public ScreenPos add(NavigationDirection direction) {
        return switch (1.field_41834[direction.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> new ScreenPos(this.x, this.y + 1);
            case 2 -> new ScreenPos(this.x, this.y - 1);
            case 3 -> new ScreenPos(this.x - 1, this.y);
            case 4 -> new ScreenPos(this.x + 1, this.y);
        };
    }

    public int getComponent(NavigationAxis axis) {
        return switch (1.field_41833[axis.ordinal()]) {
            default -> throw new MatchException(null, null);
            case 1 -> this.x;
            case 2 -> this.y;
        };
    }

    public int x() {
        return this.x;
    }

    public int y() {
        return this.y;
    }
}

