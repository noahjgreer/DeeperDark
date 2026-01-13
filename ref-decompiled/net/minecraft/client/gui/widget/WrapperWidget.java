/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.WrapperWidget
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.LayoutWidget;

@Environment(value=EnvType.CLIENT)
public abstract class WrapperWidget
implements LayoutWidget {
    private int x;
    private int y;
    protected int width;
    protected int height;

    public WrapperWidget(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setX(int x) {
        this.forEachElement(element -> {
            int j = element.getX() + (x - this.getX());
            element.setX(j);
        });
        this.x = x;
    }

    public void setY(int y) {
        this.forEachElement(element -> {
            int j = element.getY() + (y - this.getY());
            element.setY(j);
        });
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}

