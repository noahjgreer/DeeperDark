/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.registry.tag;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.minecraft.registry.tag.TagEntry;

public static final class TagGroupLoader.TrackedEntry
extends Record {
    final TagEntry entry;
    private final String source;

    public TagGroupLoader.TrackedEntry(TagEntry entry, String source) {
        this.entry = entry;
        this.source = source;
    }

    @Override
    public String toString() {
        return String.valueOf(this.entry) + " (from " + this.source + ")";
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{TagGroupLoader.TrackedEntry.class, "entry;source", "entry", "source"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{TagGroupLoader.TrackedEntry.class, "entry;source", "entry", "source"}, this, object);
    }

    public TagEntry entry() {
        return this.entry;
    }

    public String source() {
        return this.source;
    }
}
