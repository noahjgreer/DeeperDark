/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.List;
import java.util.Optional;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

static class StringVisitable.4
implements StringVisitable {
    final /* synthetic */ List field_25314;

    StringVisitable.4(List list) {
        this.field_25314 = list;
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        for (StringVisitable stringVisitable : this.field_25314) {
            Optional<T> optional = stringVisitable.visit(visitor);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        for (StringVisitable stringVisitable : this.field_25314) {
            Optional<T> optional = stringVisitable.visit(styledVisitor, style);
            if (!optional.isPresent()) continue;
            return optional;
        }
        return Optional.empty();
    }
}
