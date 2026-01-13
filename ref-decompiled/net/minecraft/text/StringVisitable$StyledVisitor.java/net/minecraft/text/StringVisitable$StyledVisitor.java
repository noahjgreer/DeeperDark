/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.Optional;
import net.minecraft.text.Style;

public static interface StringVisitable.StyledVisitor<T> {
    public Optional<T> accept(Style var1, String var2);
}
