/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.ResourceReloadLogger
 *  net.minecraft.client.resource.ResourceReloadLogger$RecoveryEntry
 *  net.minecraft.client.resource.ResourceReloadLogger$ReloadReason
 *  net.minecraft.client.resource.ResourceReloadLogger$ReloadState
 *  net.minecraft.resource.ResourcePack
 *  net.minecraft.util.crash.CrashReport
 *  net.minecraft.util.crash.CrashReportSection
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.ResourceReloadLogger;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ResourceReloadLogger {
    private static final Logger LOGGER = LogUtils.getLogger();
    private // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable ResourceReloadLogger.ReloadState reloadState;
    private int reloadCount;

    public void reload(ReloadReason reason, List<ResourcePack> packs) {
        ++this.reloadCount;
        if (this.reloadState != null && !this.reloadState.finished) {
            LOGGER.warn("Reload already ongoing, replacing");
        }
        this.reloadState = new ReloadState(reason, (List)packs.stream().map(ResourcePack::getId).collect(ImmutableList.toImmutableList()));
    }

    public void recover(Throwable throwable) {
        if (this.reloadState == null) {
            LOGGER.warn("Trying to signal reload recovery, but nothing was started");
            this.reloadState = new ReloadState(ReloadReason.UNKNOWN, (List)ImmutableList.of());
        }
        this.reloadState.recovery = new RecoveryEntry(throwable);
    }

    public void finish() {
        if (this.reloadState == null) {
            LOGGER.warn("Trying to finish reload, but nothing was started");
        } else {
            this.reloadState.finished = true;
        }
    }

    public void addReloadSection(CrashReport report) {
        CrashReportSection crashReportSection = report.addElement("Last reload");
        crashReportSection.add("Reload number", (Object)this.reloadCount);
        if (this.reloadState != null) {
            this.reloadState.addReloadSection(crashReportSection);
        }
    }
}

