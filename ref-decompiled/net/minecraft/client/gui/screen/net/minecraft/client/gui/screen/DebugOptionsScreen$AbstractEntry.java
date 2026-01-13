/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ElementListWidget;

@Environment(value=EnvType.CLIENT)
public static abstract class DebugOptionsScreen.AbstractEntry
extends ElementListWidget.Entry<DebugOptionsScreen.AbstractEntry> {
    public abstract void init();
}
