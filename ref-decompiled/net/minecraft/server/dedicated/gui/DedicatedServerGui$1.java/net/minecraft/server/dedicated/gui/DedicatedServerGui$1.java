/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;

static class DedicatedServerGui.1
extends WindowAdapter {
    final /* synthetic */ DedicatedServerGui field_16857;
    final /* synthetic */ JFrame field_16856;
    final /* synthetic */ MinecraftDedicatedServer field_13841;

    DedicatedServerGui.1(DedicatedServerGui dedicatedServerGui, JFrame jFrame, MinecraftDedicatedServer minecraftDedicatedServer) {
        this.field_16857 = dedicatedServerGui;
        this.field_16856 = jFrame;
        this.field_13841 = minecraftDedicatedServer;
    }

    @Override
    public void windowClosing(WindowEvent event) {
        if (!this.field_16857.stopped.getAndSet(true)) {
            this.field_16856.setTitle(DedicatedServerGui.SHUTTING_DOWN_TITLE);
            this.field_13841.stop(true);
            this.field_16857.runStopTasks();
        }
    }
}
