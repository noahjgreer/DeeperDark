/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.c2s.play;

public static final class PlayerActionC2SPacket.Action
extends Enum<PlayerActionC2SPacket.Action> {
    public static final /* enum */ PlayerActionC2SPacket.Action START_DESTROY_BLOCK = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action ABORT_DESTROY_BLOCK = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action STOP_DESTROY_BLOCK = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action DROP_ALL_ITEMS = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action DROP_ITEM = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action RELEASE_USE_ITEM = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action SWAP_ITEM_WITH_OFFHAND = new PlayerActionC2SPacket.Action();
    public static final /* enum */ PlayerActionC2SPacket.Action STAB = new PlayerActionC2SPacket.Action();
    private static final /* synthetic */ PlayerActionC2SPacket.Action[] field_12972;

    public static PlayerActionC2SPacket.Action[] values() {
        return (PlayerActionC2SPacket.Action[])field_12972.clone();
    }

    public static PlayerActionC2SPacket.Action valueOf(String string) {
        return Enum.valueOf(PlayerActionC2SPacket.Action.class, string);
    }

    private static /* synthetic */ PlayerActionC2SPacket.Action[] method_36957() {
        return new PlayerActionC2SPacket.Action[]{START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK, STOP_DESTROY_BLOCK, DROP_ALL_ITEMS, DROP_ITEM, RELEASE_USE_ITEM, SWAP_ITEM_WITH_OFFHAND, STAB};
    }

    static {
        field_12972 = PlayerActionC2SPacket.Action.method_36957();
    }
}
