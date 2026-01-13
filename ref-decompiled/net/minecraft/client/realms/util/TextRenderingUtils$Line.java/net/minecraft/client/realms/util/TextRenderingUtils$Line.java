/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.TextRenderingUtils;

@Environment(value=EnvType.CLIENT)
public static class TextRenderingUtils.Line {
    public final List<TextRenderingUtils.LineSegment> segments;

    TextRenderingUtils.Line(TextRenderingUtils.LineSegment ... segments) {
        this(Arrays.asList(segments));
    }

    TextRenderingUtils.Line(List<TextRenderingUtils.LineSegment> segments) {
        this.segments = segments;
    }

    public String toString() {
        return "Line{segments=" + String.valueOf(this.segments) + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TextRenderingUtils.Line line = (TextRenderingUtils.Line)o;
        return Objects.equals(this.segments, line.segments);
    }

    public int hashCode() {
        return Objects.hash(this.segments);
    }
}
