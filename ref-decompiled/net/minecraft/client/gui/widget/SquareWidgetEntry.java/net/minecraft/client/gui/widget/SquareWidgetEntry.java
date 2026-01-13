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

@Environment(value=EnvType.CLIENT)
public interface SquareWidgetEntry {
    default public boolean isInside(int x, int y, int sideLength) {
        return x >= 0 && x < sideLength && y >= 0 && y < sideLength;
    }

    default public boolean isLeft(int x, int y, int sideLength) {
        return x >= 0 && x < sideLength / 2 && y >= 0 && y < sideLength;
    }

    default public boolean isRight(int x, int y, int sideLength) {
        return x >= sideLength / 2 && x < sideLength && y >= 0 && y < sideLength;
    }

    default public boolean isBottomRight(int x, int y, int sideLength) {
        return x >= sideLength / 2 && x < sideLength && y >= 0 && y < sideLength / 2;
    }

    default public boolean isTopRight(int x, int y, int sideLength) {
        return x >= sideLength / 2 && x < sideLength && y >= sideLength / 2 && y < sideLength;
    }

    default public boolean isBottomLeft(int x, int y, int sideLength) {
        return x >= 0 && x < sideLength / 2 && y >= 0 && y < sideLength / 2;
    }

    default public boolean isTopLeft(int x, int y, int sideLength) {
        return x >= 0 && x < sideLength / 2 && y >= sideLength / 2 && y < sideLength;
    }
}
