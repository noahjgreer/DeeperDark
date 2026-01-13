/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.Optional;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

static class StringVisitable.2
implements StringVisitable {
    final /* synthetic */ String field_25311;

    StringVisitable.2(String string) {
        this.field_25311 = string;
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.field_25311);
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return styledVisitor.accept(style, this.field_25311);
    }
}
