/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ResourceReloadLogger {
    private static final Logger LOGGER = LogUtils.getLogger();
    private @Nullable ReloadState reloadState;
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
            this.reloadState = new ReloadState(ReloadReason.UNKNOWN, (List<String>)ImmutableList.of());
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
        crashReportSection.add("Reload number", this.reloadCount);
        if (this.reloadState != null) {
            this.reloadState.addReloadSection(crashReportSection);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ReloadState {
        private final ReloadReason reason;
        private final List<String> packs;
        @Nullable RecoveryEntry recovery;
        boolean finished;

        ReloadState(ReloadReason reason, List<String> packs) {
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

    @Environment(value=EnvType.CLIENT)
    public static final class ReloadReason
    extends Enum<ReloadReason> {
        public static final /* enum */ ReloadReason INITIAL = new ReloadReason("initial");
        public static final /* enum */ ReloadReason MANUAL = new ReloadReason("manual");
        public static final /* enum */ ReloadReason UNKNOWN = new ReloadReason("unknown");
        final String name;
        private static final /* synthetic */ ReloadReason[] field_33706;

        public static ReloadReason[] values() {
            return (ReloadReason[])field_33706.clone();
        }

        public static ReloadReason valueOf(String string) {
            return Enum.valueOf(ReloadReason.class, string);
        }

        private ReloadReason(String name) {
            this.name = name;
        }

        private static /* synthetic */ ReloadReason[] method_36867() {
            return new ReloadReason[]{INITIAL, MANUAL, UNKNOWN};
        }

        static {
            field_33706 = ReloadReason.method_36867();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class RecoveryEntry {
        private final Throwable throwable;

        RecoveryEntry(Throwable throwable) {
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
}
