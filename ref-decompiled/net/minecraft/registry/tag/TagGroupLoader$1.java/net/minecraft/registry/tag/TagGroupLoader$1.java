/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry.tag;

import java.util.Collection;
import java.util.Map;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

class TagGroupLoader.1
implements TagEntry.ValueGetter<T> {
    final /* synthetic */ Map field_39270;

    TagGroupLoader.1(Map map) {
        this.field_39270 = map;
    }

    @Override
    public @Nullable T direct(Identifier id, boolean required) {
        return TagGroupLoader.this.entrySupplier.get(id, required).orElse(null);
    }

    @Override
    public @Nullable Collection<T> tag(Identifier id) {
        return (Collection)this.field_39270.get(id);
    }
}
