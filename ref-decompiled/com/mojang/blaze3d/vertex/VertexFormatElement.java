/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Type
 *  com.mojang.blaze3d.vertex.VertexFormatElement$Usage
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.annotation.DeobfuscateClass
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public record VertexFormatElement(int id, int index, Type type, Usage usage, int count) {
    private final int id;
    private final int index;
    private final Type type;
    private final Usage usage;
    private final int count;
    public static final int MAX_COUNT = 32;
    private static final @Nullable VertexFormatElement[] BY_ID = new VertexFormatElement[32];
    private static final List<VertexFormatElement> ELEMENTS = new ArrayList(32);
    public static final VertexFormatElement POSITION = VertexFormatElement.register((int)0, (int)0, (Type)Type.FLOAT, (Usage)Usage.POSITION, (int)3);
    public static final VertexFormatElement COLOR = VertexFormatElement.register((int)1, (int)0, (Type)Type.UBYTE, (Usage)Usage.COLOR, (int)4);
    public static final VertexFormatElement UV0;
    public static final VertexFormatElement UV;
    public static final VertexFormatElement UV1;
    public static final VertexFormatElement UV2;
    public static final VertexFormatElement NORMAL;
    public static final VertexFormatElement LINE_WIDTH;

    public VertexFormatElement(int id, int index, Type type, Usage usage, int count) {
        if (id < 0 || id >= BY_ID.length) {
            throw new IllegalArgumentException("Element ID must be in range [0; " + BY_ID.length + ")");
        }
        if (!this.supportsUsage(index, usage)) {
            throw new IllegalStateException("Multiple vertex elements of the same type other than UVs are not supported");
        }
        this.id = id;
        this.index = index;
        this.type = type;
        this.usage = usage;
        this.count = count;
    }

    public static VertexFormatElement register(int id, int index, Type type, Usage usage, int count) {
        VertexFormatElement vertexFormatElement = new VertexFormatElement(id, index, type, usage, count);
        if (BY_ID[id] != null) {
            throw new IllegalArgumentException("Duplicate element registration for: " + id);
        }
        VertexFormatElement.BY_ID[id] = vertexFormatElement;
        ELEMENTS.add(vertexFormatElement);
        return vertexFormatElement;
    }

    private boolean supportsUsage(int uvIndex, Usage usage) {
        return uvIndex == 0 || usage == Usage.UV;
    }

    @Override
    public String toString() {
        return this.count + "," + String.valueOf(this.usage) + "," + String.valueOf(this.type) + " (" + this.id + ")";
    }

    public int mask() {
        return 1 << this.id;
    }

    public int byteSize() {
        return this.type.size() * this.count;
    }

    public static @Nullable VertexFormatElement byId(int id) {
        return BY_ID[id];
    }

    public static Stream<VertexFormatElement> elementsFromMask(int mask) {
        return ELEMENTS.stream().filter(element -> (mask & element.mask()) != 0);
    }

    public int id() {
        return this.id;
    }

    public int index() {
        return this.index;
    }

    public Type type() {
        return this.type;
    }

    public Usage usage() {
        return this.usage;
    }

    public int count() {
        return this.count;
    }

    static {
        UV = UV0 = VertexFormatElement.register((int)2, (int)0, (Type)Type.FLOAT, (Usage)Usage.UV, (int)2);
        UV1 = VertexFormatElement.register((int)3, (int)1, (Type)Type.SHORT, (Usage)Usage.UV, (int)2);
        UV2 = VertexFormatElement.register((int)4, (int)2, (Type)Type.SHORT, (Usage)Usage.UV, (int)2);
        NORMAL = VertexFormatElement.register((int)5, (int)0, (Type)Type.BYTE, (Usage)Usage.NORMAL, (int)3);
        LINE_WIDTH = VertexFormatElement.register((int)6, (int)0, (Type)Type.FLOAT, (Usage)Usage.GENERIC, (int)1);
    }
}

