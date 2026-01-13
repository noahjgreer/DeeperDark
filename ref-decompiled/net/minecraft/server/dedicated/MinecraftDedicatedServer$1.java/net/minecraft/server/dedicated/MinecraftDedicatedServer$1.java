/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.dedicated;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

class MinecraftDedicatedServer.1
extends Thread {
    MinecraftDedicatedServer.1(String string) {
        super(string);
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        try {
            String string;
            while (!MinecraftDedicatedServer.this.isStopped() && MinecraftDedicatedServer.this.isRunning() && (string = bufferedReader.readLine()) != null) {
                MinecraftDedicatedServer.this.enqueueCommand(string, MinecraftDedicatedServer.this.getCommandSource());
            }
        }
        catch (IOException iOException) {
            LOGGER.error("Exception handling console input", (Throwable)iOException);
        }
    }
}
