/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class AbuseReportReasonScreen.ReasonListWidget
extends AlwaysSelectedEntryListWidget<ReasonEntry> {
    public AbuseReportReasonScreen.ReasonListWidget(MinecraftClient client) {
        super(client, AbuseReportReasonScreen.this.width, AbuseReportReasonScreen.this.getReasonListHeight(), AbuseReportReasonScreen.this.layout.getHeaderHeight(), 18);
        for (AbuseReportReason abuseReportReason : AbuseReportReason.values()) {
            if (AbuseReportReason.getExcludedReasonsForType(AbuseReportReasonScreen.this.reportType).contains((Object)abuseReportReason)) continue;
            this.addEntry(new ReasonEntry(abuseReportReason));
        }
    }

    public @Nullable ReasonEntry getEntry(AbuseReportReason reason) {
        return this.children().stream().filter(entry -> entry.reason == reason).findFirst().orElse(null);
    }

    @Override
    public int getRowWidth() {
        return 320;
    }

    @Override
    public void setSelected(@Nullable ReasonEntry reasonEntry) {
        super.setSelected(reasonEntry);
        AbuseReportReasonScreen.this.reason = reasonEntry != null ? reasonEntry.getReason() : null;
    }

    @Environment(value=EnvType.CLIENT)
    public class ReasonEntry
    extends AlwaysSelectedEntryListWidget.Entry<ReasonEntry> {
        final AbuseReportReason reason;

        public ReasonEntry(AbuseReportReason reason) {
            this.reason = reason;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
            int i = this.getContentX() + 1;
            int j = this.getContentY() + (this.getContentHeight() - ((AbuseReportReasonScreen)AbuseReportReasonScreen.this).textRenderer.fontHeight) / 2 + 1;
            context.drawTextWithShadow(AbuseReportReasonScreen.this.textRenderer, this.reason.getText(), i, j, -1);
        }

        @Override
        public Text getNarration() {
            return Text.translatable("gui.abuseReport.reason.narration", this.reason.getText(), this.reason.getDescription());
        }

        @Override
        public boolean mouseClicked(Click click, boolean doubled) {
            ReasonListWidget.this.setSelected(this);
            return super.mouseClicked(click, doubled);
        }

        public AbuseReportReason getReason() {
            return this.reason;
        }
    }
}
