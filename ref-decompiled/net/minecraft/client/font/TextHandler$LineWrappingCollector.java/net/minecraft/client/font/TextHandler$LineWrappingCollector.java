/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.font;

import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class TextHandler.LineWrappingCollector {
    final List<TextHandler.StyledString> parts;
    private String joined;

    public TextHandler.LineWrappingCollector(List<TextHandler.StyledString> parts) {
        this.parts = parts;
        this.joined = parts.stream().map(part -> part.literal).collect(Collectors.joining());
    }

    public char charAt(int index) {
        return this.joined.charAt(index);
    }

    public StringVisitable collectLine(int lineLength, int skippedLength, Style style) {
        TextCollector textCollector = new TextCollector();
        ListIterator<TextHandler.StyledString> listIterator = this.parts.listIterator();
        int i = lineLength;
        boolean bl = false;
        while (listIterator.hasNext()) {
            String string2;
            TextHandler.StyledString styledString = listIterator.next();
            String string = styledString.literal;
            int j = string.length();
            if (!bl) {
                if (i > j) {
                    textCollector.add(styledString);
                    listIterator.remove();
                    i -= j;
                } else {
                    string2 = string.substring(0, i);
                    if (!string2.isEmpty()) {
                        textCollector.add(StringVisitable.styled(string2, styledString.style));
                    }
                    i += skippedLength;
                    bl = true;
                }
            }
            if (!bl) continue;
            if (i > j) {
                listIterator.remove();
                i -= j;
                continue;
            }
            string2 = string.substring(i);
            if (string2.isEmpty()) {
                listIterator.remove();
                break;
            }
            listIterator.set(new TextHandler.StyledString(string2, style));
            break;
        }
        this.joined = this.joined.substring(lineLength + skippedLength);
        return textCollector.getCombined();
    }

    public @Nullable StringVisitable collectRemainders() {
        TextCollector textCollector = new TextCollector();
        this.parts.forEach(textCollector::add);
        this.parts.clear();
        return textCollector.getRawCombined();
    }
}
