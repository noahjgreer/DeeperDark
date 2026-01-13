/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.Alignment;
import net.minecraft.client.font.DrawnTextConsumer;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class MultilineText.2
implements MultilineText {
    private @Nullable List<MultilineText.Line> lines;
    private @Nullable Language language;
    final /* synthetic */ Text[] field_52295;
    final /* synthetic */ TextRenderer field_26529;
    final /* synthetic */ int field_52296;
    final /* synthetic */ int field_52297;

    MultilineText.2() {
        this.field_52295 = texts;
        this.field_26529 = textRenderer;
        this.field_52296 = i;
        this.field_52297 = j;
    }

    @Override
    public int draw(Alignment alignment, int x, int y, int lineHeight, DrawnTextConsumer consumer) {
        int i = y;
        for (MultilineText.Line line : this.getLines()) {
            int j = alignment.getAdjustedX(x, line.width);
            consumer.text(j, i, line.text);
            i += lineHeight;
        }
        return i;
    }

    private List<MultilineText.Line> getLines() {
        Language language = Language.getInstance();
        if (this.lines != null && language == this.language) {
            return this.lines;
        }
        this.language = language;
        ArrayList<StringVisitable> list = new ArrayList<StringVisitable>();
        for (Text text : this.field_52295) {
            list.addAll(this.field_26529.wrapLinesWithoutLanguage(text, this.field_52296));
        }
        this.lines = new ArrayList<MultilineText.Line>();
        int i = Math.min(list.size(), this.field_52297);
        List list2 = list.subList(0, i);
        for (int j = 0; j < list2.size(); ++j) {
            StringVisitable stringVisitable = (StringVisitable)list2.get(j);
            OrderedText orderedText = Language.getInstance().reorder(stringVisitable);
            if (j == list2.size() - 1 && i == this.field_52297 && i != list.size()) {
                StringVisitable stringVisitable2 = this.field_26529.trimToWidth(stringVisitable, this.field_26529.getWidth(stringVisitable) - this.field_26529.getWidth(ScreenTexts.ELLIPSIS));
                StringVisitable stringVisitable3 = StringVisitable.concat(stringVisitable2, ScreenTexts.ELLIPSIS.copy().fillStyle(this.field_52295[this.field_52295.length - 1].getStyle()));
                this.lines.add(new MultilineText.Line(Language.getInstance().reorder(stringVisitable3), this.field_26529.getWidth(stringVisitable3)));
                continue;
            }
            this.lines.add(new MultilineText.Line(orderedText, this.field_26529.getWidth(orderedText)));
        }
        return this.lines;
    }

    @Override
    public int getLineCount() {
        return this.getLines().size();
    }

    @Override
    public int getMaxWidth() {
        return Math.min(this.field_52296, this.getLines().stream().mapToInt(MultilineText.Line::width).max().orElse(0));
    }
}
