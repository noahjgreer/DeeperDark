/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report.log;

import com.mojang.serialization.MapCodec;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.util.StringIdentifiable;

@Environment(value=EnvType.CLIENT)
public static final class ChatLogEntry.Type
extends Enum<ChatLogEntry.Type>
implements StringIdentifiable {
    public static final /* enum */ ChatLogEntry.Type PLAYER = new ChatLogEntry.Type("player", () -> ReceivedMessage.ChatMessage.CHAT_MESSAGE_CODEC);
    public static final /* enum */ ChatLogEntry.Type SYSTEM = new ChatLogEntry.Type("system", () -> ReceivedMessage.GameMessage.GAME_MESSAGE_CODEC);
    private final String id;
    private final Supplier<MapCodec<? extends ChatLogEntry>> codecSupplier;
    private static final /* synthetic */ ChatLogEntry.Type[] field_40808;

    public static ChatLogEntry.Type[] values() {
        return (ChatLogEntry.Type[])field_40808.clone();
    }

    public static ChatLogEntry.Type valueOf(String string) {
        return Enum.valueOf(ChatLogEntry.Type.class, string);
    }

    private ChatLogEntry.Type(String id, Supplier<MapCodec<? extends ChatLogEntry>> codecSupplier) {
        this.id = id;
        this.codecSupplier = codecSupplier;
    }

    private MapCodec<? extends ChatLogEntry> getCodec() {
        return this.codecSupplier.get();
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ ChatLogEntry.Type[] method_46542() {
        return new ChatLogEntry.Type[]{PLAYER, SYSTEM};
    }

    static {
        field_40808 = ChatLogEntry.Type.method_46542();
    }
}
