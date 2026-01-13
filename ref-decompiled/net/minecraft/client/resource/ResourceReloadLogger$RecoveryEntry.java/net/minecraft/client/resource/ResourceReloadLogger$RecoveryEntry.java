/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.resource;

import java.io.PrintWriter;
import java.io.StringWriter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.crash.CrashReportSection;

@Environment(value=EnvType.CLIENT)
static class ResourceReloadLogger.RecoveryEntry {
    private final Throwable throwable;

    ResourceReloadLogger.RecoveryEntry(Throwable throwable) {
        this.throwable = throwable;
    }

    public void addRecoverySection(CrashReportSection section) {
        section.add("Recovery", "Yes");
        section.add("Recovery reason", () -> {
            StringWriter stringWriter = new StringWriter();
            this.throwable.printStackTrace(new PrintWriter(stringWriter));
            return stringWriter.toString();
        });
    }
}
