/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.hud.MessageIndicator
 *  net.minecraft.client.gui.hud.MessageIndicator$Icon
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record MessageIndicator(int indicatorColor, // Could not load outer class - annotation placement on inner may be incorrect
@Nullable MessageIndicator.Icon icon, @Nullable Text text, @Nullable String loggedName) {
    private final int indicatorColor;
    private final // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MessageIndicator.Icon icon;
    private final @Nullable Text text;
    private final @Nullable String loggedName;
    private static final Text SYSTEM_TEXT = Text.translatable((String)"chat.tag.system");
    private static final Text SINGLE_PLAYER_TEXT = Text.translatable((String)"chat.tag.system_single_player");
    private static final Text NOT_SECURE_TEXT = Text.translatable((String)"chat.tag.not_secure");
    private static final Text MODIFIED_TEXT = Text.translatable((String)"chat.tag.modified");
    private static final Text ERROR_TEXT = Text.translatable((String)"chat.tag.error");
    private static final int NOT_SECURE_COLOR = 0xD0D0D0;
    private static final int MODIFIED_COLOR = 0x606060;
    private static final MessageIndicator SYSTEM = new MessageIndicator(0xD0D0D0, null, SYSTEM_TEXT, "System");
    private static final MessageIndicator SINGLE_PLAYER = new MessageIndicator(0xD0D0D0, null, SINGLE_PLAYER_TEXT, "System");
    private static final MessageIndicator NOT_SECURE = new MessageIndicator(0xD0D0D0, null, NOT_SECURE_TEXT, "Not Secure");
    private static final MessageIndicator CHAT_ERROR = new MessageIndicator(0xFF5555, null, ERROR_TEXT, "Chat Error");

    public MessageIndicator(int indicatorColor, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MessageIndicator.Icon icon, @Nullable Text text, @Nullable String loggedName) {
        this.indicatorColor = indicatorColor;
        this.icon = icon;
        this.text = text;
        this.loggedName = loggedName;
    }

    public static MessageIndicator system() {
        return SYSTEM;
    }

    public static MessageIndicator singlePlayer() {
        return SINGLE_PLAYER;
    }

    public static MessageIndicator notSecure() {
        return NOT_SECURE;
    }

    public static MessageIndicator modified(String originalText) {
        MutableText text = Text.literal((String)originalText).formatted(Formatting.GRAY);
        MutableText text2 = Text.empty().append(MODIFIED_TEXT).append(ScreenTexts.LINE_BREAK).append((Text)text);
        return new MessageIndicator(0x606060, Icon.CHAT_MODIFIED, (Text)text2, "Modified");
    }

    public static MessageIndicator chatError() {
        return CHAT_ERROR;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MessageIndicator.class, "indicatorColor;icon;text;logTag", "indicatorColor", "icon", "text", "loggedName"}, this, object);
    }

    public int indicatorColor() {
        return this.indicatorColor;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable MessageIndicator.Icon icon() {
        return this.icon;
    }

    public @Nullable Text text() {
        return this.text;
    }

    public @Nullable String loggedName() {
        return this.loggedName;
    }
}

