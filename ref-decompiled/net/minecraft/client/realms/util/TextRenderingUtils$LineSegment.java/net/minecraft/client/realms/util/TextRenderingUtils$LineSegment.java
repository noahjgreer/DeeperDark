/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.realms.util;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static class TextRenderingUtils.LineSegment {
    private final String fullText;
    private final @Nullable String linkTitle;
    private final @Nullable String linkUrl;

    private TextRenderingUtils.LineSegment(String fullText) {
        this.fullText = fullText;
        this.linkTitle = null;
        this.linkUrl = null;
    }

    private TextRenderingUtils.LineSegment(String fullText, @Nullable String linkTitle, @Nullable String linkUrl) {
        this.fullText = fullText;
        this.linkTitle = linkTitle;
        this.linkUrl = linkUrl;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TextRenderingUtils.LineSegment lineSegment = (TextRenderingUtils.LineSegment)o;
        return Objects.equals(this.fullText, lineSegment.fullText) && Objects.equals(this.linkTitle, lineSegment.linkTitle) && Objects.equals(this.linkUrl, lineSegment.linkUrl);
    }

    public int hashCode() {
        return Objects.hash(this.fullText, this.linkTitle, this.linkUrl);
    }

    public String toString() {
        return "Segment{fullText='" + this.fullText + "', linkTitle='" + this.linkTitle + "', linkUrl='" + this.linkUrl + "'}";
    }

    public String renderedText() {
        return this.isLink() ? this.linkTitle : this.fullText;
    }

    public boolean isLink() {
        return this.linkTitle != null;
    }

    public String getLinkUrl() {
        if (!this.isLink()) {
            throw new IllegalStateException("Not a link: " + String.valueOf(this));
        }
        return this.linkUrl;
    }

    public static TextRenderingUtils.LineSegment link(String linkTitle, String linkUrl) {
        return new TextRenderingUtils.LineSegment(null, linkTitle, linkUrl);
    }

    @VisibleForTesting
    protected static TextRenderingUtils.LineSegment text(String fullText) {
        return new TextRenderingUtils.LineSegment(fullText);
    }
}
