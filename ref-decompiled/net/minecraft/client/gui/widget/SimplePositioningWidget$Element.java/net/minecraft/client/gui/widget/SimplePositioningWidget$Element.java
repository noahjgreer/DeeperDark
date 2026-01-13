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
static class SimplePositioningWidget.Element
extends WrapperWidget.WrappedElement {
    protected SimplePositioningWidget.Element(Widget widget, Positioner positioner) {
        super(widget, positioner);
    }
}
