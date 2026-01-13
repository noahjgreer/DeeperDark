/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;

@Environment(value=EnvType.CLIENT)
public static class Defines.Builder {
    private final ImmutableMap.Builder<String, String> values = ImmutableMap.builder();
    private final ImmutableSet.Builder<String> flags = ImmutableSet.builder();

    Defines.Builder() {
    }

    public Defines.Builder define(String key, String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("Cannot define empty string");
        }
        this.values.put((Object)key, (Object)Defines.Builder.escapeLinebreak(value));
        return this;
    }

    private static String escapeLinebreak(String string) {
        return string.replaceAll("\n", "\\\\\n");
    }

    public Defines.Builder define(String key, float value) {
        this.values.put((Object)key, (Object)String.valueOf(value));
        return this;
    }

    public Defines.Builder define(String name, int value) {
        this.values.put((Object)name, (Object)String.valueOf(value));
        return this;
    }

    public Defines.Builder flag(String flag) {
        this.flags.add((Object)flag);
        return this;
    }

    public Defines build() {
        return new Defines((Map<String, String>)this.values.build(), (Set<String>)this.flags.build());
    }
}
