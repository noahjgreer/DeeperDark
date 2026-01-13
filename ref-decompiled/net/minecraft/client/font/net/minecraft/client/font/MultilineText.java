/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface MultilineText {
    public static final MultilineText EMPTY = new MultilineText(){

        @Override
        public int draw(Alignment alignment, int x, int y, int lineHeight, DrawnTextConsumer consumer) {
            return y;
        }

        @Override
        public int getLineCount() {
            return 0;
        }

        @Override
        public int getMaxWidth() {
            return 0;
        }
    };

    public static MultilineText create(TextRenderer renderer, Text ... texts) {
        return MultilineText.create(renderer, Integer.MAX_VALUE, Integer.MAX_VALUE, texts);
    }

    public static MultilineText create(TextRenderer renderer, int maxWidth, Text ... texts) {
        return MultilineText.create(renderer, maxWidth, Integer.MAX_VALUE, texts);
    }

    public static MultilineText create(TextRenderer renderer, Text text, int maxWidth) {
        return MultilineText.create(renderer, maxWidth, Integer.MAX_VALUE, text);
    }

    public static MultilineText create(final TextRenderer textRenderer, final int maxWidth, final int maxLines, final Text ... texts) {
        if (texts.length == 0) {
            return EMPTY;
        }
        return new MultilineText(){
            private @Nullable List<Line> lines;
            private @Nullable Language language;

            @Override
            public int draw(Alignment alignment, int x, int y, int lineHeight, DrawnTextConsumer consumer) {
                int i = y;
                for (Line line : this.getLines()) {
                    int j = alignment.getAdjustedX(x, line.width);
                    consumer.text(j, i, line.text);
                    i += lineHeight;
                }
                return i;
            }

            private List<Line> getLines() {
                Language language = Language.getInstance();
                if (this.lines != null && language == this.language) {
                    return this.lines;
                }
                this.language = language;
                ArrayList<StringVisitable> list = new ArrayList<StringVisitable>();
                for (Text text : texts) {
                    list.addAll(textRenderer.wrapLinesWithoutLanguage(text, maxWidth));
                }
                this.lines = new ArrayList<Line>();
                int i = Math.min(list.size(), maxLines);
                List list2 = list.subList(0, i);
                for (int j = 0; j < list2.size(); ++j) {
                    StringVisitable stringVisitable = (StringVisitable)list2.get(j);
                    OrderedText orderedText = Language.getInstance().reorder(stringVisitable);
                    if (j == list2.size() - 1 && i == maxLines && i != list.size()) {
                        StringVisitable stringVisitable2 = textRenderer.trimToWidth(stringVisitable, textRenderer.getWidth(stringVisitable) - textRenderer.getWidth(ScreenTexts.ELLIPSIS));
                        StringVisitable stringVisitable3 = StringVisitable.concat(stringVisitable2, ScreenTexts.ELLIPSIS.copy().fillStyle(texts[texts.length - 1].getStyle()));
                        this.lines.add(new Line(Language.getInstance().reorder(stringVisitable3), textRenderer.getWidth(stringVisitable3)));
                        continue;
                    }
                    this.lines.add(new Line(orderedText, textRenderer.getWidth(orderedText)));
                }
                return this.lines;
            }

            @Override
            public int getLineCount() {
                return this.getLines().size();
            }

            @Override
            public int getMaxWidth() {
                return Math.min(maxWidth, this.getLines().stream().mapToInt(Line::width).max().orElse(0));
            }
        };
    }

    public int draw(Alignment var1, int var2, int var3, int var4, DrawnTextConsumer var5);

    public int getLineCount();

    public int getMaxWidth();

    @Environment(value=EnvType.CLIENT)
    public static final class Line
    extends Record {
        final OrderedText text;
        final int width;

        public Line(OrderedText text, int width) {
            this.text = text;
            this.width = width;
        }

        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{Line.class, "text;width", "text", "width"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{Line.class, "text;width", "text", "width"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{Line.class, "text;width", "text", "width"}, this, object);
        }

        public OrderedText text() {
            return this.text;
        }

        public int width() {
            return this.width;
        }
    }
}
