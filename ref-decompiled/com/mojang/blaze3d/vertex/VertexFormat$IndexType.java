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

@Environment(value=EnvType.CLIENT)
public static final class VertexFormat.IndexType
extends Enum<VertexFormat.IndexType> {
    public static final /* enum */ VertexFormat.IndexType SHORT = new VertexFormat.IndexType(2);
    public static final /* enum */ VertexFormat.IndexType INT = new VertexFormat.IndexType(4);
    public final int size;
    private static final /* synthetic */ VertexFormat.IndexType[] field_27376;

    public static VertexFormat.IndexType[] values() {
        return (VertexFormat.IndexType[])field_27376.clone();
    }

    public static VertexFormat.IndexType valueOf(String string) {
        return Enum.valueOf(VertexFormat.IndexType.class, string);
    }

    private VertexFormat.IndexType(int size) {
        this.size = size;
    }

    public static VertexFormat.IndexType smallestFor(int i) {
        if ((i & 0xFFFF0000) != 0) {
            return INT;
        }
        return SHORT;
    }

    private static /* synthetic */ VertexFormat.IndexType[] method_36816() {
        return new VertexFormat.IndexType[]{SHORT, INT};
    }

    static {
        field_27376 = VertexFormat.IndexType.method_36816();
    }
}
