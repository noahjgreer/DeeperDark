/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.vertex;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.annotation.DeobfuscateClass;

@Environment(value=EnvType.CLIENT)
@DeobfuscateClass
public static enum VertexFormatElement.Type {
    FLOAT(4, "Float"),
    UBYTE(1, "Unsigned Byte"),
    BYTE(1, "Byte"),
    USHORT(2, "Unsigned Short"),
    SHORT(2, "Short"),
    UINT(4, "Unsigned Int"),
    INT(4, "Int");

    private final int size;
    private final String name;

    private VertexFormatElement.Type(int size, String name) {
        this.size = size;
        this.name = name;
    }

    public int size() {
        return this.size;
    }

    public String toString() {
        return this.name;
    }
}
