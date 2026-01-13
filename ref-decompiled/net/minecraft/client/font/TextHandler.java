/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextHandler
 *  net.minecraft.client.font.TextHandler$LineBreakingVisitor
 *  net.minecraft.client.font.TextHandler$LineWrappingCollector
 *  net.minecraft.client.font.TextHandler$LineWrappingConsumer
 *  net.minecraft.client.font.TextHandler$StyledString
 *  net.minecraft.client.font.TextHandler$WidthLimitingVisitor
 *  net.minecraft.client.font.TextHandler$WidthRetriever
 *  net.minecraft.text.CharacterVisitor
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.text.StringVisitable$StyledVisitor
 *  net.minecraft.text.Style
 *  net.minecraft.text.TextVisitFactory
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class TextHandler {
    final WidthRetriever widthRetriever;

    public TextHandler(WidthRetriever widthRetriever) {
        this.widthRetriever = widthRetriever;
    }

    public float getWidth(@Nullable String text) {
        if (text == null) {
            return 0.0f;
        }
        MutableFloat mutableFloat = new MutableFloat();
        TextVisitFactory.visitFormatted((String)text, (Style)Style.EMPTY, (unused, style, codePoint) -> {
            mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public float getWidth(StringVisitable text) {
        MutableFloat mutableFloat = new MutableFloat();
        TextVisitFactory.visitFormatted((StringVisitable)text, (Style)Style.EMPTY, (unused, style, codePoint) -> {
            mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public float getWidth(OrderedText text) {
        MutableFloat mutableFloat = new MutableFloat();
        text.accept((index, style, codePoint) -> {
            mutableFloat.add(this.widthRetriever.getWidth(codePoint, style));
            return true;
        });
        return mutableFloat.floatValue();
    }

    public int getTrimmedLength(String text, int maxWidth, Style style) {
        WidthLimitingVisitor widthLimitingVisitor = new WidthLimitingVisitor(this, (float)maxWidth);
        TextVisitFactory.visitForwards((String)text, (Style)style, (CharacterVisitor)widthLimitingVisitor);
        return widthLimitingVisitor.getLength();
    }

    public String trimToWidth(String text, int maxWidth, Style style) {
        return text.substring(0, this.getTrimmedLength(text, maxWidth, style));
    }

    public String trimToWidthBackwards(String text, int maxWidth, Style style2) {
        MutableFloat mutableFloat = new MutableFloat();
        MutableInt mutableInt = new MutableInt(text.length());
        TextVisitFactory.visitBackwards((String)text, (Style)style2, (index, style, codePoint) -> {
            float f = mutableFloat.addAndGet(this.widthRetriever.getWidth(codePoint, style));
            if (f > (float)maxWidth) {
                return false;
            }
            mutableInt.setValue(index);
            return true;
        });
        return text.substring(mutableInt.intValue());
    }

    public StringVisitable trimToWidth(StringVisitable text, int width, Style style) {
        WidthLimitingVisitor widthLimitingVisitor = new WidthLimitingVisitor(this, (float)width);
        return text.visit((StringVisitable.StyledVisitor)new /* Unavailable Anonymous Inner Class!! */, style).orElse(text);
    }

    public int getEndingIndex(String text, int maxWidth, Style style) {
        LineBreakingVisitor lineBreakingVisitor = new LineBreakingVisitor(this, (float)maxWidth);
        TextVisitFactory.visitFormatted((String)text, (Style)style, (CharacterVisitor)lineBreakingVisitor);
        return lineBreakingVisitor.getEndingIndex();
    }

    public static int moveCursorByWords(String text, int offset, int cursor, boolean consumeSpaceOrBreak) {
        int i = cursor;
        boolean bl = offset < 0;
        int j = Math.abs(offset);
        for (int k = 0; k < j; ++k) {
            if (bl) {
                while (consumeSpaceOrBreak && i > 0 && (text.charAt(i - 1) == ' ' || text.charAt(i - 1) == '\n')) {
                    --i;
                }
                while (i > 0 && text.charAt(i - 1) != ' ' && text.charAt(i - 1) != '\n') {
                    --i;
                }
                continue;
            }
            int l = text.length();
            int m = text.indexOf(32, i);
            int n = text.indexOf(10, i);
            i = m == -1 && n == -1 ? -1 : (m != -1 && n != -1 ? Math.min(m, n) : (m != -1 ? m : n));
            if (i == -1) {
                i = l;
                continue;
            }
            while (consumeSpaceOrBreak && i < l && (text.charAt(i) == ' ' || text.charAt(i) == '\n')) {
                ++i;
            }
        }
        return i;
    }

    public void wrapLines(String text, int maxWidth, Style style, boolean retainTrailingWordSplit, LineWrappingConsumer consumer) {
        int i = 0;
        int j = text.length();
        Style style2 = style;
        while (i < j) {
            LineBreakingVisitor lineBreakingVisitor = new LineBreakingVisitor(this, (float)maxWidth);
            boolean bl = TextVisitFactory.visitFormatted((String)text, (int)i, (Style)style2, (Style)style, (CharacterVisitor)lineBreakingVisitor);
            if (bl) {
                consumer.accept(style2, i, j);
                break;
            }
            int k = lineBreakingVisitor.getEndingIndex();
            char c = text.charAt(k);
            int l = c == '\n' || c == ' ' ? k + 1 : k;
            consumer.accept(style2, i, retainTrailingWordSplit ? l : k);
            i = l;
            style2 = lineBreakingVisitor.getEndingStyle();
        }
    }

    public List<StringVisitable> wrapLines(String text, int maxWidth, Style style2) {
        ArrayList list = Lists.newArrayList();
        this.wrapLines(text, maxWidth, style2, false, (style, start, end) -> list.add(StringVisitable.styled((String)text.substring(start, end), (Style)style)));
        return list;
    }

    public List<StringVisitable> wrapLines(StringVisitable text2, int maxWidth, Style style) {
        ArrayList list = Lists.newArrayList();
        this.wrapLines(text2, maxWidth, style, (text, lastLineWrapped) -> list.add(text));
        return list;
    }

    public void wrapLines(StringVisitable text2, int maxWidth, Style style2, BiConsumer<StringVisitable, Boolean> lineConsumer) {
        ArrayList list = Lists.newArrayList();
        text2.visit((style, text) -> {
            if (!text.isEmpty()) {
                list.add(new StyledString(text, style));
            }
            return Optional.empty();
        }, style2);
        LineWrappingCollector lineWrappingCollector = new LineWrappingCollector((List)list);
        boolean bl = true;
        boolean bl2 = false;
        boolean bl3 = false;
        block0: while (bl) {
            bl = false;
            LineBreakingVisitor lineBreakingVisitor = new LineBreakingVisitor(this, (float)maxWidth);
            for (StyledString styledString : lineWrappingCollector.parts) {
                boolean bl4 = TextVisitFactory.visitFormatted((String)styledString.literal, (int)0, (Style)styledString.style, (Style)style2, (CharacterVisitor)lineBreakingVisitor);
                if (!bl4) {
                    int i = lineBreakingVisitor.getEndingIndex();
                    Style style22 = lineBreakingVisitor.getEndingStyle();
                    char c = lineWrappingCollector.charAt(i);
                    boolean bl5 = c == '\n';
                    boolean bl6 = bl5 || c == ' ';
                    bl2 = bl5;
                    StringVisitable stringVisitable = lineWrappingCollector.collectLine(i, bl6 ? 1 : 0, style22);
                    lineConsumer.accept(stringVisitable, bl3);
                    bl3 = !bl5;
                    bl = true;
                    continue block0;
                }
                lineBreakingVisitor.offset(styledString.literal.length());
            }
        }
        StringVisitable stringVisitable2 = lineWrappingCollector.collectRemainders();
        if (stringVisitable2 != null) {
            lineConsumer.accept(stringVisitable2, bl3);
        } else if (bl2) {
            lineConsumer.accept(StringVisitable.EMPTY, false);
        }
    }
}

