/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ChatHudLine(int creationTick, Text content, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator) {
    private static final int PADDING = 4;

    public List<OrderedText> breakLines(TextRenderer textRenderer, int width) {
        if (this.indicator != null && this.indicator.icon() != null) {
            width -= this.indicator.icon().width + 4 + 2;
        }
        return ChatMessages.breakRenderedChatMessageLines(this.content, width, textRenderer);
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

    @Environment(value=EnvType.CLIENT)
    public record Visible(int addedTime, OrderedText content, @Nullable MessageIndicator indicator, boolean endOfEntry) {
        public int getWidth(TextRenderer textRenderer) {
            return textRenderer.getWidth(this.content) + 4;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Visible.class, "addedTime;content;tag;endOfEntry", "addedTime", "content", "indicator", "endOfEntry"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Visible.class, "addedTime;content;tag;endOfEntry", "addedTime", "content", "indicator", "endOfEntry"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Visible.class, "addedTime;content;tag;endOfEntry", "addedTime", "content", "indicator", "endOfEntry"}, this, object);
        }
    }
}
