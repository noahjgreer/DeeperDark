/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server;

import net.minecraft.server.dedicated.MinecraftDedicatedServer;

static class Main.1
extends Thread {
    final /* synthetic */ MinecraftDedicatedServer field_4611;

    Main.1(String string, MinecraftDedicatedServer minecraftDedicatedServer) {
        this.field_4611 = minecraftDedicatedServer;
        super(string);
    }

    @Override
    public void run() {
        this.field_4611.stop(true);
    }
}
