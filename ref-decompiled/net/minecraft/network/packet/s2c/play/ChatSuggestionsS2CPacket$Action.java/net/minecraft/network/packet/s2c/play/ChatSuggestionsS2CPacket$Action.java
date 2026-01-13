/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.packet.s2c.play;

public static final class ChatSuggestionsS2CPacket.Action
extends Enum<ChatSuggestionsS2CPacket.Action> {
    public static final /* enum */ ChatSuggestionsS2CPacket.Action ADD = new ChatSuggestionsS2CPacket.Action();
    public static final /* enum */ ChatSuggestionsS2CPacket.Action REMOVE = new ChatSuggestionsS2CPacket.Action();
    public static final /* enum */ ChatSuggestionsS2CPacket.Action SET = new ChatSuggestionsS2CPacket.Action();
    private static final /* synthetic */ ChatSuggestionsS2CPacket.Action[] field_39804;

    public static ChatSuggestionsS2CPacket.Action[] values() {
        return (ChatSuggestionsS2CPacket.Action[])field_39804.clone();
    }

    public static ChatSuggestionsS2CPacket.Action valueOf(String string) {
        return Enum.valueOf(ChatSuggestionsS2CPacket.Action.class, string);
    }

    private static /* synthetic */ ChatSuggestionsS2CPacket.Action[] method_44784() {
        return new ChatSuggestionsS2CPacket.Action[]{ADD, REMOVE, SET};
    }

    static {
        field_39804 = ChatSuggestionsS2CPacket.Action.method_44784();
    }
}
