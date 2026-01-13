/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.widget;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractTextWidget;
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
            return MultilineText.create(textRenderer, cacheKey.maxWidth, cacheKey.maxRows.getAsInt(), cacheKey.message);
        }
        return MultilineText.create(textRenderer, cacheKey.message, cacheKey.maxWidth);
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

    @Override
    public int getWidth() {
        return this.cacheKeyToText.map(this.getCacheKey()).getMaxWidth();
    }

    @Override
    public int getHeight() {
        return this.cacheKeyToText.map(this.getCacheKey()).getLineCount() * this.getTextRenderer().fontHeight;
    }

    @Override
    public void draw(DrawnTextConsumer textConsumer) {
        MultilineText multilineText = this.cacheKeyToText.map(this.getCacheKey());
        int i = this.getTextX();
        int j = this.getTextY();
        int k = this.getTextRenderer().fontHeight;
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

    @Environment(value=EnvType.CLIENT)
    static final class CacheKey
    extends Record {
        final Text message;
        final int maxWidth;
        final OptionalInt maxRows;

        CacheKey(Text message, int maxWidth, OptionalInt maxRows) {
            this.message = message;
            this.maxWidth = maxWidth;
            this.maxRows = maxRows;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{CacheKey.class, "message;maxWidth;maxRows", "message", "maxWidth", "maxRows"}, this, object);
        }

        public Text message() {
            return this.message;
        }

        public int maxWidth() {
            return this.maxWidth;
        }

        public OptionalInt maxRows() {
            return this.maxRows;
        }
    }
}
