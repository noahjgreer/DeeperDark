/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Optional;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;

public record PlainTextContent.Literal(String string) implements PlainTextContent
{
    @Override
    public <T> Optional<T> visit(StringVisitable.Visitor<T> visitor) {
        return visitor.accept(this.string);
    }

    @Override
    public <T> Optional<T> visit(StringVisitable.StyledVisitor<T> visitor, Style style) {
        return visitor.accept(style, this.string);
    }

    @Override
    public String toString() {
        return "literal{" + this.string + "}";
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{PlainTextContent.Literal.class, "text", "string"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{PlainTextContent.Literal.class, "text", "string"}, this, object);
    }
}
