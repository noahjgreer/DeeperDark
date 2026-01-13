/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.util.crash;

import org.jspecify.annotations.Nullable;

static class CrashReportSection.Element {
    private final String name;
    private final String detail;

    public CrashReportSection.Element(String name, @Nullable Object detail) {
        this.name = name;
        if (detail == null) {
            this.detail = "~~NULL~~";
        } else if (detail instanceof Throwable) {
            Throwable throwable = (Throwable)detail;
            this.detail = "~~ERROR~~ " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage();
        } else {
            this.detail = detail.toString();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getDetail() {
        return this.detail;
    }
}
