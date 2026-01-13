/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.Optional;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

static class StringVisitable.3
implements StringVisitable {
    final /* synthetic */ String field_25312;
    final /* synthetic */ Style field_25313;

    StringVisitable.3() {
        this.field_25312 = string;
        this.field_25313 = style;
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.field_25312);
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return styledVisitor.accept(this.field_25313.withParent(style), this.field_25312);
    }
}
