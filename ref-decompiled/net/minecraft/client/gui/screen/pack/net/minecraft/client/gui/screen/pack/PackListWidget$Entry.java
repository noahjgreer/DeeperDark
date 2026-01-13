/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.pack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

@Environment(value=EnvType.CLIENT)
public abstract class PackListWidget.Entry
extends AlwaysSelectedEntryListWidget.Entry<PackListWidget.Entry> {
    @Override
    public int getWidth() {
        return super.getWidth() - (PackListWidget.this.overflows() ? 6 : 0);
    }

    public abstract String getName();
}
