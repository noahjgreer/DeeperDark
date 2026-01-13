/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public static class InvalidHierarchicalFileException.File {
    @Nullable String name;
    private final List<String> keys = Lists.newArrayList();

    InvalidHierarchicalFileException.File() {
    }

    void addKey(String key) {
        this.keys.add(0, key);
    }

    public @Nullable String getName() {
        return this.name;
    }

    public String joinKeys() {
        return StringUtils.join(this.keys, (String)"->");
    }

    public String toString() {
        if (this.name != null) {
            if (this.keys.isEmpty()) {
                return this.name;
            }
            return this.name + " " + this.joinKeys();
        }
        if (this.keys.isEmpty()) {
            return "(Unknown file)";
        }
        return "(Unknown file) " + this.joinKeys();
    }
}
