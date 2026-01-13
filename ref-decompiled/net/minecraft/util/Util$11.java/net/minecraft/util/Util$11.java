/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

static class Util.11
extends Thread {
    Util.11(String string) {
        super(string);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(Integer.MAX_VALUE);
            }
        }
        catch (InterruptedException interruptedException) {
            LOGGER.warn("Timer hack thread interrupted, that really should not happen");
            return;
        }
    }
}
