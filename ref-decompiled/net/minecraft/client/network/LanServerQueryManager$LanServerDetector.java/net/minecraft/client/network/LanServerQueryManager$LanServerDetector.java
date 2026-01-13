/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.util.logging.UncaughtExceptionLogger;

@Environment(value=EnvType.CLIENT)
public static class LanServerQueryManager.LanServerDetector
extends Thread {
    private final LanServerQueryManager.LanServerEntryList entryList;
    private final InetAddress multicastAddress;
    private final MulticastSocket socket;

    public LanServerQueryManager.LanServerDetector(LanServerQueryManager.LanServerEntryList entryList) throws IOException {
        super("LanServerDetector #" + THREAD_ID.incrementAndGet());
        this.entryList = entryList;
        this.setDaemon(true);
        this.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        this.socket = new MulticastSocket(4445);
        this.multicastAddress = InetAddress.getByName("224.0.2.60");
        this.socket.setSoTimeout(5000);
        this.socket.joinGroup(this.multicastAddress);
    }

    @Override
    public void run() {
        byte[] bs = new byte[1024];
        while (!this.isInterrupted()) {
            DatagramPacket datagramPacket = new DatagramPacket(bs, bs.length);
            try {
                this.socket.receive(datagramPacket);
            }
            catch (SocketTimeoutException socketTimeoutException) {
                continue;
            }
            catch (IOException iOException) {
                LOGGER.error("Couldn't ping server", (Throwable)iOException);
                break;
            }
            String string = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength(), StandardCharsets.UTF_8);
            LOGGER.debug("{}: {}", (Object)datagramPacket.getAddress(), (Object)string);
            this.entryList.addServer(string, datagramPacket.getAddress());
        }
        try {
            this.socket.leaveGroup(this.multicastAddress);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        this.socket.close();
    }
}
