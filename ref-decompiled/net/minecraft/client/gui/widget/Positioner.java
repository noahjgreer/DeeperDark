/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.widget.Positioner
 *  net.minecraft.client.gui.widget.Positioner$Impl
 */
package net.minecraft.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.Positioner;

@Environment(value=EnvType.CLIENT)
public interface Positioner {
    public Positioner margin(int var1);

    public Positioner margin(int var1, int var2);

    public Positioner margin(int var1, int var2, int var3, int var4);

    public Positioner marginLeft(int var1);

    public Positioner marginTop(int var1);

    public Positioner marginRight(int var1);

    public Positioner marginBottom(int var1);

    public Positioner marginX(int var1);

    public Positioner marginY(int var1);

    public Positioner relative(float var1, float var2);

    public Positioner relativeX(float var1);

    public Positioner relativeY(float var1);

    default public Positioner alignLeft() {
        return this.relativeX(0.0f);
    }

    default public Positioner alignHorizontalCenter() {
        return this.relativeX(0.5f);
    }

    default public Positioner alignRight() {
        return this.relativeX(1.0f);
    }

    default public Positioner alignTop() {
        return this.relativeY(0.0f);
    }

    default public Positioner alignVerticalCenter() {
        return this.relativeY(0.5f);
    }

    default public Positioner alignBottom() {
        return this.relativeY(1.0f);
    }

    public Positioner copy();

    public Impl toImpl();

    public static Positioner create() {
        return new Impl();
    }
}

