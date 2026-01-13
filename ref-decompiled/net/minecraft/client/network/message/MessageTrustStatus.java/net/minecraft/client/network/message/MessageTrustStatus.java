/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network.message;

import com.mojang.serialization.Codec;
import java.time.Instant;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public final class MessageTrustStatus
extends Enum<MessageTrustStatus>
implements StringIdentifiable {
    public static final /* enum */ MessageTrustStatus SECURE = new MessageTrustStatus("secure");
    public static final /* enum */ MessageTrustStatus MODIFIED = new MessageTrustStatus("modified");
    public static final /* enum */ MessageTrustStatus NOT_SECURE = new MessageTrustStatus("not_secure");
    public static final Codec<MessageTrustStatus> CODEC;
    private final String id;
    private static final /* synthetic */ MessageTrustStatus[] field_39784;

    public static MessageTrustStatus[] values() {
        return (MessageTrustStatus[])field_39784.clone();
    }

    public static MessageTrustStatus valueOf(String string) {
        return Enum.valueOf(MessageTrustStatus.class, string);
    }

    private MessageTrustStatus(String id) {
        this.id = id;
    }

    public static MessageTrustStatus getStatus(SignedMessage message, Text decorated, Instant receptionTimestamp) {
        if (!message.hasSignature() || message.isExpiredOnClient(receptionTimestamp)) {
            return NOT_SECURE;
        }
        if (MessageTrustStatus.isModified(message, decorated)) {
            return MODIFIED;
        }
        return SECURE;
    }

    private static boolean isModified(SignedMessage message, Text decorated) {
        if (!decorated.getString().contains(message.getSignedContent())) {
            return true;
        }
        Text text = message.unsignedContent();
        if (text == null) {
            return false;
        }
        return MessageTrustStatus.isNotInDefaultFont(text);
    }

    private static boolean isNotInDefaultFont(Text content) {
        return content.visit((style, part) -> {
            if (MessageTrustStatus.isNotInDefaultFont(style)) {
                return Optional.of(true);
            }
            return Optional.empty();
        }, Style.EMPTY).orElse(false);
    }

    private static boolean isNotInDefaultFont(Style style) {
        return !style.getFont().equals(StyleSpriteSource.DEFAULT);
    }

    public boolean isInsecure() {
        return this == NOT_SECURE;
    }

    public @Nullable MessageIndicator createIndicator(SignedMessage message) {
        return switch (this.ordinal()) {
            case 1 -> MessageIndicator.modified(message.getSignedContent());
            case 2 -> MessageIndicator.notSecure();
            default -> null;
        };
    }

    @Override
    public String asString() {
        return this.id;
    }

    private static /* synthetic */ MessageTrustStatus[] method_44743() {
        return new MessageTrustStatus[]{SECURE, MODIFIED, NOT_SECURE};
    }

    static {
        field_39784 = MessageTrustStatus.method_44743();
        CODEC = StringIdentifiable.createCodec(MessageTrustStatus::values);
    }
}
