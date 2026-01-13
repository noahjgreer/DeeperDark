/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.option;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ElementListWidget;

@Environment(value=EnvType.CLIENT)
public static abstract class ControlsListWidget.Entry
extends ElementListWidget.Entry<ControlsListWidget.Entry> {
    abstract void update();
}
