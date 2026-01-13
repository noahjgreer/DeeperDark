/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.Optional;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

class StringVisitable.1
implements StringVisitable {
    StringVisitable.1() {
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> styledVisitor, Style style) {
        return Optional.empty();
    }
}
