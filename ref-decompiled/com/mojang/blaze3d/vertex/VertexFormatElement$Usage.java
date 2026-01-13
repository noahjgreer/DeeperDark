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
public static enum VertexFormatElement.Usage {
    POSITION("Position"),
    NORMAL("Normal"),
    COLOR("Vertex Color"),
    UV("UV"),
    GENERIC("Generic");

    private final String name;

    private VertexFormatElement.Usage(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
