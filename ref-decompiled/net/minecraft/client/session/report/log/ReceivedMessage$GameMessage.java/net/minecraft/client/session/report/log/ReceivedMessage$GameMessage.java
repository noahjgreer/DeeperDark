/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report.log;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record ReceivedMessage.GameMessage(Text message, Instant timestamp) implements ReceivedMessage
{
    public static final MapCodec<ReceivedMessage.GameMessage> GAME_MESSAGE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)TextCodecs.CODEC.fieldOf("message").forGetter(ReceivedMessage.GameMessage::message), (App)Codecs.INSTANT.fieldOf("time_stamp").forGetter(ReceivedMessage.GameMessage::timestamp)).apply((Applicative)instance, ReceivedMessage.GameMessage::new));

    @Override
    public Text getContent() {
        return this.message;
    }

    @Override
    public boolean isSentFrom(UUID uuid) {
        return false;
    }

    @Override
    public ChatLogEntry.Type getType() {
        return ChatLogEntry.Type.SYSTEM;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReceivedMessage.GameMessage.class, "message;timeStamp", "message", "timestamp"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReceivedMessage.GameMessage.class, "message;timeStamp", "message", "timestamp"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReceivedMessage.GameMessage.class, "message;timeStamp", "message", "timestamp"}, this, object);
    }
}
