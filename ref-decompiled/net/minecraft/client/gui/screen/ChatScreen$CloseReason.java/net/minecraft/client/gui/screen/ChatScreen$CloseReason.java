/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
protected static final class ChatScreen.CloseReason
extends Enum<ChatScreen.CloseReason> {
    public static final /* enum */ ChatScreen.CloseReason INTENTIONAL = new ChatScreen.CloseReason();
    public static final /* enum */ ChatScreen.CloseReason INTERRUPTED = new ChatScreen.CloseReason();
    public static final /* enum */ ChatScreen.CloseReason DONE = new ChatScreen.CloseReason();
    private static final /* synthetic */ ChatScreen.CloseReason[] field_62018;

    public static ChatScreen.CloseReason[] values() {
        return (ChatScreen.CloseReason[])field_62018.clone();
    }

    public static ChatScreen.CloseReason valueOf(String string) {
        return Enum.valueOf(ChatScreen.CloseReason.class, string);
    }

    private static /* synthetic */ ChatScreen.CloseReason[] method_73219() {
        return new ChatScreen.CloseReason[]{INTENTIONAL, INTERRUPTED, DONE};
    }

    static {
        field_62018 = ChatScreen.CloseReason.method_73219();
    }
}
