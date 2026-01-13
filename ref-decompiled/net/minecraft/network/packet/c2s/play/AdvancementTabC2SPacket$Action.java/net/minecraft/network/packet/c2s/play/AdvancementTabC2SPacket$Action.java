/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

public static final class AdvancementTabC2SPacket.Action
extends Enum<AdvancementTabC2SPacket.Action> {
    public static final /* enum */ AdvancementTabC2SPacket.Action OPENED_TAB = new AdvancementTabC2SPacket.Action();
    public static final /* enum */ AdvancementTabC2SPacket.Action CLOSED_SCREEN = new AdvancementTabC2SPacket.Action();
    private static final /* synthetic */ AdvancementTabC2SPacket.Action[] field_13022;

    public static AdvancementTabC2SPacket.Action[] values() {
        return (AdvancementTabC2SPacket.Action[])field_13022.clone();
    }

    public static AdvancementTabC2SPacket.Action valueOf(String string) {
        return Enum.valueOf(AdvancementTabC2SPacket.Action.class, string);
    }

    private static /* synthetic */ AdvancementTabC2SPacket.Action[] method_36962() {
        return new AdvancementTabC2SPacket.Action[]{OPENED_TAB, CLOSED_SCREEN};
    }

    static {
        field_13022 = AdvancementTabC2SPacket.Action.method_36962();
    }
}
