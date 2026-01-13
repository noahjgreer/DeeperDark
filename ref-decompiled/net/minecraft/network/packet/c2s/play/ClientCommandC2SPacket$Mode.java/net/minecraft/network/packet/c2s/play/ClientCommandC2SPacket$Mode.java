/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

public static final class ClientCommandC2SPacket.Mode
extends Enum<ClientCommandC2SPacket.Mode> {
    public static final /* enum */ ClientCommandC2SPacket.Mode STOP_SLEEPING = new ClientCommandC2SPacket.Mode();
    public static final /* enum */ ClientCommandC2SPacket.Mode START_SPRINTING = new ClientCommandC2SPacket.Mode();
    public static final /* enum */ ClientCommandC2SPacket.Mode STOP_SPRINTING = new ClientCommandC2SPacket.Mode();
    public static final /* enum */ ClientCommandC2SPacket.Mode START_RIDING_JUMP = new ClientCommandC2SPacket.Mode();
    public static final /* enum */ ClientCommandC2SPacket.Mode STOP_RIDING_JUMP = new ClientCommandC2SPacket.Mode();
    public static final /* enum */ ClientCommandC2SPacket.Mode OPEN_INVENTORY = new ClientCommandC2SPacket.Mode();
    public static final /* enum */ ClientCommandC2SPacket.Mode START_FALL_FLYING = new ClientCommandC2SPacket.Mode();
    private static final /* synthetic */ ClientCommandC2SPacket.Mode[] field_12983;

    public static ClientCommandC2SPacket.Mode[] values() {
        return (ClientCommandC2SPacket.Mode[])field_12983.clone();
    }

    public static ClientCommandC2SPacket.Mode valueOf(String string) {
        return Enum.valueOf(ClientCommandC2SPacket.Mode.class, string);
    }

    private static /* synthetic */ ClientCommandC2SPacket.Mode[] method_36958() {
        return new ClientCommandC2SPacket.Mode[]{STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, START_RIDING_JUMP, STOP_RIDING_JUMP, OPEN_INVENTORY, START_FALL_FLYING};
    }

    static {
        field_12983 = ClientCommandC2SPacket.Mode.method_36958();
    }
}
