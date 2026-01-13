/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated;

import java.util.TimerTask;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;

class DedicatedServerWatchdog.1
extends TimerTask {
    DedicatedServerWatchdog.1(DedicatedServerWatchdog dedicatedServerWatchdog) {
    }

    @Override
    public void run() {
        Runtime.getRuntime().halt(1);
    }
}
