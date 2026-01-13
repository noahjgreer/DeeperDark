/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.vertex;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public static class VertexFormat.Builder {
    private final ImmutableMap.Builder<String, VertexFormatElement> elements = ImmutableMap.builder();
    private final IntList offsets = new IntArrayList();
    private int offset;

    VertexFormat.Builder() {
    }

    public VertexFormat.Builder add(String name, VertexFormatElement element) {
        this.elements.put((Object)name, (Object)element);
        this.offsets.add(this.offset);
        this.offset += element.byteSize();
        return this;
    }

    public VertexFormat.Builder padding(int padding) {
        this.offset += padding;
        return this;
    }

    public VertexFormat build() {
        ImmutableMap immutableMap = this.elements.buildOrThrow();
        ImmutableList immutableList = immutableMap.values().asList();
        ImmutableList immutableList2 = immutableMap.keySet().asList();
        return new VertexFormat((List<VertexFormatElement>)immutableList, (List<String>)immutableList2, this.offsets, this.offset);
    }
}
