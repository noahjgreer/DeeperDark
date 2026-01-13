/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.data.tag;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

@FunctionalInterface
public static interface TagProvider.TagLookup<T>
extends Function<TagKey<T>, Optional<TagBuilder>> {
    public static <T> TagProvider.TagLookup<T> empty() {
        return tag -> Optional.empty();
    }

    default public boolean contains(TagKey<T> tag) {
        return ((Optional)this.apply(tag)).isPresent();
    }
}
