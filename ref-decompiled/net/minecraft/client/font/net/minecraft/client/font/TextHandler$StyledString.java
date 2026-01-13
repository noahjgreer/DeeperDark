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
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

@Environment(value=EnvType.CLIENT)
static class TextHandler.StyledString
implements StringVisitable {
    final String literal;
    final Style style;

    public TextHandler.StyledString(String literal, Style style) {
        this.literal = literal;
        this.style = style;
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.literal);
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return styledVisitor.accept(this.style.withParent(style), this.literal);
    }
}
