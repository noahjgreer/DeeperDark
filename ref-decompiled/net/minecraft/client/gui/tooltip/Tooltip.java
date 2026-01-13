/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Narratable
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.screen.narration.NarrationPart
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Language
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.tooltip;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Narratable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class Tooltip
implements Narratable {
    private static final int ROW_LENGTH = 170;
    private final Text content;
    private @Nullable List<OrderedText> lines;
    private @Nullable Language language;
    private final @Nullable Text narration;

    private Tooltip(Text content, @Nullable Text narration) {
        this.content = content;
        this.narration = narration;
    }

    public static Tooltip of(Text content, @Nullable Text narration) {
        return new Tooltip(content, narration);
    }

    public static Tooltip of(Text content) {
        return new Tooltip(content, content);
    }

    public void appendNarrations(NarrationMessageBuilder builder) {
        if (this.narration != null) {
            builder.put(NarrationPart.HINT, this.narration);
        }
    }

    public List<OrderedText> getLines(MinecraftClient client) {
        Language language = Language.getInstance();
        if (this.lines == null || language != this.language) {
            this.lines = Tooltip.wrapLines((MinecraftClient)client, (Text)this.content);
            this.language = language;
        }
        return this.lines;
    }

    public static List<OrderedText> wrapLines(MinecraftClient client, Text text) {
        return client.textRenderer.wrapLines((StringVisitable)text, 170);
    }
}

