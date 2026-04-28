package net.noahsarch.deeperdark.client.chat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ChatInputLayoutState {
    private static int chatOffset;

    private ChatInputLayoutState() {}

    public static int getChatOffset() {
        return chatOffset;
    }

    public static void setChatOffset(int offset) {
        chatOffset = Math.max(offset, 0);
    }

    public static void reset() {
        chatOffset = 0;
    }
}
