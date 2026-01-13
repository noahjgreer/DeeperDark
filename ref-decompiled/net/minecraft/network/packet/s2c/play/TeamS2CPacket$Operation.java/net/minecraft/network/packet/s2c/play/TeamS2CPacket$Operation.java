/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

public static final class TeamS2CPacket.Operation
extends Enum<TeamS2CPacket.Operation> {
    public static final /* enum */ TeamS2CPacket.Operation ADD = new TeamS2CPacket.Operation();
    public static final /* enum */ TeamS2CPacket.Operation REMOVE = new TeamS2CPacket.Operation();
    private static final /* synthetic */ TeamS2CPacket.Operation[] field_29157;

    public static TeamS2CPacket.Operation[] values() {
        return (TeamS2CPacket.Operation[])field_29157.clone();
    }

    public static TeamS2CPacket.Operation valueOf(String string) {
        return Enum.valueOf(TeamS2CPacket.Operation.class, string);
    }

    private static /* synthetic */ TeamS2CPacket.Operation[] method_36954() {
        return new TeamS2CPacket.Operation[]{ADD, REMOVE};
    }

    static {
        field_29157 = TeamS2CPacket.Operation.method_36954();
    }
}
