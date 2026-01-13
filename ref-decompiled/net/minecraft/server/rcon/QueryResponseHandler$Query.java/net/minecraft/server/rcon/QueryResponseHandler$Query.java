/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.rcon;

import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import net.minecraft.util.math.random.Random;

static class QueryResponseHandler.Query {
    private final long startTime = new Date().getTime();
    private final int id;
    private final byte[] messageBytes;
    private final byte[] replyBuf;
    private final String message;

    public QueryResponseHandler.Query(DatagramPacket packet) {
        byte[] bs = packet.getData();
        this.messageBytes = new byte[4];
        this.messageBytes[0] = bs[3];
        this.messageBytes[1] = bs[4];
        this.messageBytes[2] = bs[5];
        this.messageBytes[3] = bs[6];
        this.message = new String(this.messageBytes, StandardCharsets.UTF_8);
        this.id = Random.create().nextInt(0x1000000);
        this.replyBuf = String.format(Locale.ROOT, "\t%s%d\u0000", this.message, this.id).getBytes(StandardCharsets.UTF_8);
    }

    public Boolean startedBefore(long lastQueryTime) {
        return this.startTime < lastQueryTime;
    }

    public int getId() {
        return this.id;
    }

    public byte[] getReplyBuf() {
        return this.replyBuf;
    }

    public byte[] getMessageBytes() {
        return this.messageBytes;
    }

    public String getMessage() {
        return this.message;
    }
}
