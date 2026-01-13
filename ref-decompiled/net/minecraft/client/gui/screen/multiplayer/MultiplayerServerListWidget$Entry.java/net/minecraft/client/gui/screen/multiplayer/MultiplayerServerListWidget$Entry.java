/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.multiplayer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;

@Environment(value=EnvType.CLIENT)
public static abstract class MultiplayerServerListWidget.Entry
extends AlwaysSelectedEntryListWidget.Entry<MultiplayerServerListWidget.Entry>
implements AutoCloseable {
    @Override
    public void close() {
    }

    abstract boolean isOfSameType(MultiplayerServerListWidget.Entry var1);

    public abstract void connect();
}
