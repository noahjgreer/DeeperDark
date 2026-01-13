/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

public static final class ClientStatusC2SPacket.Mode
extends Enum<ClientStatusC2SPacket.Mode> {
    public static final /* enum */ ClientStatusC2SPacket.Mode PERFORM_RESPAWN = new ClientStatusC2SPacket.Mode();
    public static final /* enum */ ClientStatusC2SPacket.Mode REQUEST_STATS = new ClientStatusC2SPacket.Mode();
    private static final /* synthetic */ ClientStatusC2SPacket.Mode[] field_12776;

    public static ClientStatusC2SPacket.Mode[] values() {
        return (ClientStatusC2SPacket.Mode[])field_12776.clone();
    }

    public static ClientStatusC2SPacket.Mode valueOf(String string) {
        return Enum.valueOf(ClientStatusC2SPacket.Mode.class, string);
    }

    private static /* synthetic */ ClientStatusC2SPacket.Mode[] method_36955() {
        return new ClientStatusC2SPacket.Mode[]{PERFORM_RESPAWN, REQUEST_STATS};
    }

    static {
        field_12776 = ClientStatusC2SPacket.Mode.method_36955();
    }
}
