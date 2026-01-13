/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.resource;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.util.crash.CrashReportSection;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ResourceReloadLogger.ReloadState {
    private final ResourceReloadLogger.ReloadReason reason;
    private final List<String> packs;
     @Nullable ResourceReloadLogger.RecoveryEntry recovery;
    boolean finished;

    ResourceReloadLogger.ReloadState(ResourceReloadLogger.ReloadReason reason, List<String> packs) {
        this.reason = reason;
        this.packs = packs;
    }

    public void addReloadSection(CrashReportSection section) {
        section.add("Reload reason", this.reason.name);
        section.add("Finished", this.finished ? "Yes" : "No");
        section.add("Packs", () -> String.join((CharSequence)", ", this.packs));
        if (this.recovery != null) {
            this.recovery.addRecoverySection(section);
        }
    }
}
