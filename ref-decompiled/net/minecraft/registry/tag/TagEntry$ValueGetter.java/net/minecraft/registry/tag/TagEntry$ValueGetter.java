/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry.tag;

import java.util.Collection;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static interface TagEntry.ValueGetter<T> {
    public @Nullable T direct(Identifier var1, boolean var2);

    public @Nullable Collection<T> tag(Identifier var1);
}
