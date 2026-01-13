/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.Alignment
 *  net.minecraft.client.font.DrawnTextConsumer
 *  net.minecraft.client.font.MultilineText
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.widget.AbstractTextWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget
 *  net.minecraft.client.gui.widget.MultilineTextWidget$CacheKey
 *  net.minecraft.text.Text
 *  net.minecraft.util.CachedMapper
 *  net.minecraft.util.Util
 */
package net.minecraft.client.gui.widget;

import java.util.Objects;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractTextWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.CachedMapper;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class MultilineTextWidget
extends AbstractTextWidget {
    private OptionalInt maxWidth = OptionalInt.empty();
    private OptionalInt maxRows = OptionalInt.empty();
    private final CachedMapper<CacheKey, MultilineText> cacheKeyToText = Util.cachedMapper(cacheKey -> {
        if (cacheKey.maxRows.isPresent()) {
            return MultilineText.create((TextRenderer)textRenderer, (int)cacheKey.maxWidth, (int)cacheKey.maxRows.getAsInt(), (Text[])new Text[]{cacheKey.message});
        }
        return MultilineText.create((TextRenderer)textRenderer, (Text)cacheKey.message, (int)cacheKey.maxWidth);
    });
    private boolean centered = false;

    public MultilineTextWidget(Text message, TextRenderer textRenderer) {
        this(0, 0, message, textRenderer);
    }

    public MultilineTextWidget(int x, int y, Text message, TextRenderer textRenderer) {
        super(x, y, 0, 0, message, textRenderer);
        this.active = false;
    }

    public MultilineTextWidget setMaxWidth(int maxWidth) {
        this.maxWidth = OptionalInt.of(maxWidth);
        return this;
    }

    public MultilineTextWidget setMaxRows(int maxRows) {
        this.maxRows = OptionalInt.of(maxRows);
        return this;
    }

    public MultilineTextWidget setCentered(boolean centered) {
        this.centered = centered;
        return this;
    }

    public int getWidth() {
        return ((MultilineText)this.cacheKeyToText.map((Object)this.getCacheKey())).getMaxWidth();
    }

    public int getHeight() {
        int n = ((MultilineText)this.cacheKeyToText.map((Object)this.getCacheKey())).getLineCount();
        Objects.requireNonNull(this.getTextRenderer());
        return n * 9;
    }

    public void draw(DrawnTextConsumer textConsumer) {
        MultilineText multilineText = (MultilineText)this.cacheKeyToText.map((Object)this.getCacheKey());
        int i = this.getTextX();
        int j = this.getTextY();
        Objects.requireNonNull(this.getTextRenderer());
        int k = 9;
        if (this.centered) {
            int l = this.getX() + this.getWidth() / 2;
            multilineText.draw(Alignment.CENTER, l, j, k, textConsumer);
        } else {
            multilineText.draw(Alignment.LEFT, i, j, k, textConsumer);
        }
    }

    protected int getTextX() {
        return this.getX();
    }

    protected int getTextY() {
        return this.getY();
    }

    private CacheKey getCacheKey() {
        return new CacheKey(this.getMessage(), this.maxWidth.orElse(Integer.MAX_VALUE), this.maxRows);
    }
}

