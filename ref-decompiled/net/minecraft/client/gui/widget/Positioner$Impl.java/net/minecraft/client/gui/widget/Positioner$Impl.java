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

@Environment(value=EnvType.CLIENT)
public static class Positioner.Impl
implements Positioner {
    public int marginLeft;
    public int marginTop;
    public int marginRight;
    public int marginBottom;
    public float relativeX;
    public float relativeY;

    public Positioner.Impl() {
    }

    public Positioner.Impl(Positioner.Impl original) {
        this.marginLeft = original.marginLeft;
        this.marginTop = original.marginTop;
        this.marginRight = original.marginRight;
        this.marginBottom = original.marginBottom;
        this.relativeX = original.relativeX;
        this.relativeY = original.relativeY;
    }

    @Override
    public Positioner.Impl margin(int i) {
        return this.margin(i, i);
    }

    @Override
    public Positioner.Impl margin(int i, int j) {
        return this.marginX(i).marginY(j);
    }

    @Override
    public Positioner.Impl margin(int i, int j, int k, int l) {
        return this.marginLeft(i).marginRight(k).marginTop(j).marginBottom(l);
    }

    @Override
    public Positioner.Impl marginLeft(int i) {
        this.marginLeft = i;
        return this;
    }

    @Override
    public Positioner.Impl marginTop(int i) {
        this.marginTop = i;
        return this;
    }

    @Override
    public Positioner.Impl marginRight(int i) {
        this.marginRight = i;
        return this;
    }

    @Override
    public Positioner.Impl marginBottom(int i) {
        this.marginBottom = i;
        return this;
    }

    @Override
    public Positioner.Impl marginX(int i) {
        return this.marginLeft(i).marginRight(i);
    }

    @Override
    public Positioner.Impl marginY(int i) {
        return this.marginTop(i).marginBottom(i);
    }

    @Override
    public Positioner.Impl relative(float f, float g) {
        this.relativeX = f;
        this.relativeY = g;
        return this;
    }

    @Override
    public Positioner.Impl relativeX(float f) {
        this.relativeX = f;
        return this;
    }

    @Override
    public Positioner.Impl relativeY(float f) {
        this.relativeY = f;
        return this;
    }

    @Override
    public Positioner.Impl copy() {
        return new Positioner.Impl(this);
    }

    @Override
    public Positioner.Impl toImpl() {
        return this;
    }

    @Override
    public /* synthetic */ Positioner copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ Positioner relativeY(float relativeY) {
        return this.relativeY(relativeY);
    }

    @Override
    public /* synthetic */ Positioner relativeX(float relativeX) {
        return this.relativeX(relativeX);
    }

    @Override
    public /* synthetic */ Positioner relative(float x, float y) {
        return this.relative(x, y);
    }

    @Override
    public /* synthetic */ Positioner marginY(int marginY) {
        return this.marginY(marginY);
    }

    @Override
    public /* synthetic */ Positioner marginX(int marginX) {
        return this.marginX(marginX);
    }

    @Override
    public /* synthetic */ Positioner marginBottom(int marginBottom) {
        return this.marginBottom(marginBottom);
    }

    @Override
    public /* synthetic */ Positioner marginRight(int marginRight) {
        return this.marginRight(marginRight);
    }

    @Override
    public /* synthetic */ Positioner marginTop(int marginTop) {
        return this.marginTop(marginTop);
    }

    @Override
    public /* synthetic */ Positioner marginLeft(int marginLeft) {
        return this.marginLeft(marginLeft);
    }

    @Override
    public /* synthetic */ Positioner margin(int left, int top, int right, int bottom) {
        return this.margin(left, top, right, bottom);
    }

    @Override
    public /* synthetic */ Positioner margin(int x, int y) {
        return this.margin(x, y);
    }

    @Override
    public /* synthetic */ Positioner margin(int value) {
        return this.margin(value);
    }
}
