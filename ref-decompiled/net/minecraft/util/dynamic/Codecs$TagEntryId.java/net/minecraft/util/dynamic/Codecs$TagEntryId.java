/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.dynamic;

import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record Codecs.TagEntryId(Identifier id, boolean tag) {
    @Override
    public String toString() {
        return this.asString();
    }

    private String asString() {
        return this.tag ? Codecs.HEX_PREFIX + String.valueOf(this.id) : this.id.toString();
    }
}
