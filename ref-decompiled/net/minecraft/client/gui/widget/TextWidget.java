/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.widget.AbstractTextWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.TextWidget$TextOverflow
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.Text
 *  net.minecraft.util.Language
 */
package net.minecraft.client.gui.widget;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class TextWidget
extends AbstractTextWidget {
    private static final int field_63885 = 2;
    private int maxWidth;
    private int cachedWidth;
    private boolean cachedWidthDirty;
    private TextOverflow textOverflow;

    public TextWidget(Text message, TextRenderer textRenderer) {
        int n = textRenderer.getWidth(message.asOrderedText());
        Objects.requireNonNull(textRenderer);
        this(0, 0, n, 9, message, textRenderer);
    }

    public TextWidget(int width, int height, Text message, TextRenderer textRenderer) {
        this(0, 0, width, height, message, textRenderer);
    }

    public TextWidget(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message, textRenderer);
        this.maxWidth = 0;
        this.cachedWidth = 0;
        this.cachedWidthDirty = true;
        this.textOverflow = TextOverflow.CLAMPED;
        this.active = false;
    }

    public void setMessage(Text message) {
        super.setMessage(message);
        this.cachedWidthDirty = true;
    }

    public TextWidget setMaxWidth(int width) {
        return this.setMaxWidth(width, TextOverflow.CLAMPED);
    }

    public TextWidget setMaxWidth(int width, TextOverflow textOverflow) {
        this.maxWidth = width;
        this.textOverflow = textOverflow;
        return this;
    }

    public int getWidth() {
        if (this.maxWidth > 0) {
            if (this.cachedWidthDirty) {
                this.cachedWidth = Math.min(this.maxWidth, this.getTextRenderer().getWidth(this.getMessage().asOrderedText()));
                this.cachedWidthDirty = false;
            }
            return this.cachedWidth;
        }
        return super.getWidth();
    }

    public void draw(DrawnTextConsumer textConsumer) {
        boolean bl;
        Text text = this.getMessage();
        TextRenderer textRenderer = this.getTextRenderer();
        int i = this.maxWidth > 0 ? this.maxWidth : this.getWidth();
        int j = textRenderer.getWidth((StringVisitable)text);
        int k = this.getX();
        int n = this.getY();
        int n2 = this.getHeight();
        Objects.requireNonNull(textRenderer);
        int l = n + (n2 - 9) / 2;
        boolean bl2 = bl = j > i;
        if (bl) {
            switch (this.textOverflow.ordinal()) {
                case 0: {
                    textConsumer.text(k, l, TextWidget.trim((Text)text, (TextRenderer)textRenderer, (int)i));
                    break;
                }
                case 1: {
                    this.drawTextWithMargin(textConsumer, text, 2);
                }
            }
        } else {
            textConsumer.text(k, l, text.asOrderedText());
        }
    }

    public static OrderedText trim(Text text, TextRenderer textRenderer, int width) {
        StringVisitable stringVisitable = textRenderer.trimToWidth((StringVisitable)text, width - textRenderer.getWidth((StringVisitable)ScreenTexts.ELLIPSIS));
        return Language.getInstance().reorder(StringVisitable.concat((StringVisitable[])new StringVisitable[]{stringVisitable, ScreenTexts.ELLIPSIS}));
    }
}

