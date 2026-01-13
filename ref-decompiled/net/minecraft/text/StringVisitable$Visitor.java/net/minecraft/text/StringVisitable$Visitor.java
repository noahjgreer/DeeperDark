/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.text;

import java.util.Optional;

public static interface StringVisitable.Visitor<T> {
    public Optional<T> accept(String var1);
}
