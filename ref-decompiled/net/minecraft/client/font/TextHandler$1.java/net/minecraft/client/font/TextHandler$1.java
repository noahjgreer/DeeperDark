/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.util.TextCollector;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;

@Environment(value=EnvType.CLIENT)
class TextHandler.1
implements StringVisitable.StyledVisitor<StringVisitable> {
    private final TextCollector collector = new TextCollector();
    final /* synthetic */ TextHandler.WidthLimitingVisitor field_24217;

    TextHandler.1() {
        this.field_24217 = widthLimitingVisitor;
    }

    @Override
    public Optional<StringVisitable> accept(Style style, String string) {
        this.field_24217.resetLength();
        if (!TextVisitFactory.visitFormatted(string, style, (CharacterVisitor)this.field_24217)) {
            String string2 = string.substring(0, this.field_24217.getLength());
            if (!string2.isEmpty()) {
                this.collector.add(StringVisitable.styled(string2, style));
            }
            return Optional.of(this.collector.getCombined());
        }
        if (!string.isEmpty()) {
            this.collector.add(StringVisitable.styled(string, style));
        }
        return Optional.empty();
    }
}
