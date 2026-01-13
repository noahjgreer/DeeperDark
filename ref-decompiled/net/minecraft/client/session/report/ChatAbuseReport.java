/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  it.unimi.dsi.fastutil.ints.IntCollection
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.ChatReportScreen
 *  net.minecraft.client.session.report.AbuseReport
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.ChatAbuseReport
 */
package net.minecraft.client.session.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.time.Instant;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ChatReportScreen;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;

@Environment(value=EnvType.CLIENT)
public class ChatAbuseReport
extends AbuseReport {
    final IntSet selectedMessages = new IntOpenHashSet();

    ChatAbuseReport(UUID uUID, Instant instant, UUID uUID2) {
        super(uUID, instant, uUID2);
    }

    public void toggleMessageSelection(int index, AbuseReportLimits limits) {
        if (this.selectedMessages.contains(index)) {
            this.selectedMessages.remove(index);
        } else if (this.selectedMessages.size() < limits.maxReportedMessageCount()) {
            this.selectedMessages.add(index);
        }
    }

    public ChatAbuseReport copy() {
        ChatAbuseReport chatAbuseReport = new ChatAbuseReport(this.reportId, this.currentTime, this.reportedPlayerUuid);
        chatAbuseReport.selectedMessages.addAll((IntCollection)this.selectedMessages);
        chatAbuseReport.opinionComments = this.opinionComments;
        chatAbuseReport.reason = this.reason;
        chatAbuseReport.attested = this.attested;
        return chatAbuseReport;
    }

    public Screen createReportScreen(Screen parent, AbuseReportContext context) {
        return new ChatReportScreen(parent, context, this);
    }

    public /* synthetic */ AbuseReport copy() {
        return this.copy();
    }
}

