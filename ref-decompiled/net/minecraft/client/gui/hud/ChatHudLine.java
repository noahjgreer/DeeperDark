/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.hud.ChatHudLine
 *  net.minecraft.client.gui.hud.MessageIndicator
 *  net.minecraft.client.util.ChatMessages
 *  net.minecraft.network.message.MessageSignatureData
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ChatHudLine(int creationTick, Text content, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
    private final int creationTick;
    private final Text content;
    private final @Nullable MessageSignatureData signature;
    private final @Nullable MessageIndicator indicator;
    private static final int PADDING = 4;

    public ChatHudLine(int creationTick, Text content, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
        this.creationTick = creationTick;
        this.content = content;
        this.signature = signature;
        this.indicator = indicator;
    }

    public List<OrderedText> breakLines(TextRenderer textRenderer, int width) {
        if (this.indicator != null && this.indicator.icon() != null) {
            width -= this.indicator.icon().width + 4 + 2;
        }
        return ChatMessages.breakRenderedChatMessageLines((StringVisitable)this.content, (int)width, (TextRenderer)textRenderer);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ChatHudLine.class, "addedTime;content;signature;tag", "creationTick", "content", "signature", "indicator"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ChatHudLine.class, "addedTime;content;signature;tag", "creationTick", "content", "signature", "indicator"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ChatHudLine.class, "addedTime;content;signature;tag", "creationTick", "content", "signature", "indicator"}, this, object);
    }

    public int creationTick() {
        return this.creationTick;
    }

    public Text content() {
        return this.content;
    }

    public @Nullable MessageSignatureData signature() {
        return this.signature;
    }

    public @Nullable MessageIndicator indicator() {
        return this.indicator;
    }
}

