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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
protected static abstract class WrapperWidget.WrappedElement {
    public final Widget widget;
    public final Positioner.Impl positioner;

    protected WrapperWidget.WrappedElement(Widget widget, Positioner positioner) {
        this.widget = widget;
        this.positioner = positioner.toImpl();
    }

    public int getHeight() {
        return this.widget.getHeight() + this.positioner.marginTop + this.positioner.marginBottom;
    }

    public int getWidth() {
        return this.widget.getWidth() + this.positioner.marginLeft + this.positioner.marginRight;
    }

    public void setX(int left, int right) {
        float f = this.positioner.marginLeft;
        float g = right - this.widget.getWidth() - this.positioner.marginRight;
        int i = (int)MathHelper.lerp(this.positioner.relativeX, f, g);
        this.widget.setX(i + left);
    }

    public void setY(int top, int bottom) {
        float f = this.positioner.marginTop;
        float g = bottom - this.widget.getHeight() - this.positioner.marginBottom;
        int i = Math.round(MathHelper.lerp(this.positioner.relativeY, f, g));
        this.widget.setY(i + top);
    }
}
