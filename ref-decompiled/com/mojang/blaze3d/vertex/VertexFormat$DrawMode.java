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
public static final class VertexFormat.DrawMode
extends Enum<VertexFormat.DrawMode> {
    public static final /* enum */ VertexFormat.DrawMode LINES = new VertexFormat.DrawMode(2, 2, false);
    public static final /* enum */ VertexFormat.DrawMode DEBUG_LINES = new VertexFormat.DrawMode(2, 2, false);
    public static final /* enum */ VertexFormat.DrawMode DEBUG_LINE_STRIP = new VertexFormat.DrawMode(2, 1, true);
    public static final /* enum */ VertexFormat.DrawMode POINTS = new VertexFormat.DrawMode(1, 1, false);
    public static final /* enum */ VertexFormat.DrawMode TRIANGLES = new VertexFormat.DrawMode(3, 3, false);
    public static final /* enum */ VertexFormat.DrawMode TRIANGLE_STRIP = new VertexFormat.DrawMode(3, 1, true);
    public static final /* enum */ VertexFormat.DrawMode TRIANGLE_FAN = new VertexFormat.DrawMode(3, 1, true);
    public static final /* enum */ VertexFormat.DrawMode QUADS = new VertexFormat.DrawMode(4, 4, false);
    public final int firstVertexCount;
    public final int additionalVertexCount;
    public final boolean shareVertices;
    private static final /* synthetic */ VertexFormat.DrawMode[] field_27386;

    public static VertexFormat.DrawMode[] values() {
        return (VertexFormat.DrawMode[])field_27386.clone();
    }

    public static VertexFormat.DrawMode valueOf(String string) {
        return Enum.valueOf(VertexFormat.DrawMode.class, string);
    }

    private VertexFormat.DrawMode(int firstVertexCount, int additionalVertexCount, boolean shareVertices) {
        this.firstVertexCount = firstVertexCount;
        this.additionalVertexCount = additionalVertexCount;
        this.shareVertices = shareVertices;
    }

    public int getIndexCount(int vertexCount) {
        return switch (this.ordinal()) {
            case 1, 2, 3, 4, 5, 6 -> vertexCount;
            case 0, 7 -> vertexCount / 4 * 6;
            default -> 0;
        };
    }

    private static /* synthetic */ VertexFormat.DrawMode[] method_36817() {
        return new VertexFormat.DrawMode[]{LINES, DEBUG_LINES, DEBUG_LINE_STRIP, POINTS, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN, QUADS};
    }

    static {
        field_27386 = VertexFormat.DrawMode.method_36817();
    }
}
