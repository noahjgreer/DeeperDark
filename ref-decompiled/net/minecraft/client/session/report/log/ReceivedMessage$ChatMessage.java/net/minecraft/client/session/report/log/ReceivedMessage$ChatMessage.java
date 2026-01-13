/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report.log;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.message.MessageTrustStatus;
import net.minecraft.client.session.report.log.ChatLogEntry;
import net.minecraft.client.session.report.log.ReceivedMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record ReceivedMessage.ChatMessage(GameProfile profile, SignedMessage message, MessageTrustStatus trustStatus) implements ReceivedMessage
{
    public static final MapCodec<ReceivedMessage.ChatMessage> CHAT_MESSAGE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codecs.GAME_PROFILE_CODEC.fieldOf("profile").forGetter(ReceivedMessage.ChatMessage::profile), (App)SignedMessage.CODEC.forGetter(ReceivedMessage.ChatMessage::message), (App)MessageTrustStatus.CODEC.optionalFieldOf("trust_level", (Object)MessageTrustStatus.SECURE).forGetter(ReceivedMessage.ChatMessage::trustStatus)).apply((Applicative)instance, ReceivedMessage.ChatMessage::new));
    private static final DateTimeFormatter DATE_TIME_FORMATTER = Util.getDefaultLocaleFormatter(FormatStyle.SHORT);

    @Override
    public Text getContent() {
        if (!this.message.filterMask().isPassThrough()) {
            Text text = this.message.filterMask().getFilteredText(this.message.getSignedContent());
            return text != null ? text : Text.empty();
        }
        return this.message.getContent();
    }

    @Override
    public Text getNarration() {
        Text text = this.getContent();
        Text text2 = this.getFormattedTimestamp();
        return Text.translatable("gui.chatSelection.message.narrate", this.profile.name(), text, text2);
    }

    public Text getHeadingText() {
        Text text = this.getFormattedTimestamp();
        return Text.translatable("gui.chatSelection.heading", this.profile.name(), text);
    }

    private Text getFormattedTimestamp() {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(this.message.getTimestamp(), ZoneId.systemDefault());
        return Text.literal(zonedDateTime.format(DATE_TIME_FORMATTER)).formatted(Formatting.ITALIC, Formatting.GRAY);
    }

    @Override
    public boolean isSentFrom(UUID uuid) {
        return this.message.canVerifyFrom(uuid);
    }

    public UUID getSenderUuid() {
        return this.profile.id();
    }

    @Override
    public ChatLogEntry.Type getType() {
        return ChatLogEntry.Type.PLAYER;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ReceivedMessage.ChatMessage.class, "profile;message;trustLevel", "profile", "message", "trustStatus"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ReceivedMessage.ChatMessage.class, "profile;message;trustLevel", "profile", "message", "trustStatus"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ReceivedMessage.ChatMessage.class, "profile;message;trustLevel", "profile", "message", "trustStatus"}, this, object);
    }
}
