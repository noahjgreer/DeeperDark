/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class AbuseReportReasonScreen.ReasonListWidget.ReasonEntry
extends AlwaysSelectedEntryListWidget.Entry<AbuseReportReasonScreen.ReasonListWidget.ReasonEntry> {
    final AbuseReportReason reason;

    public AbuseReportReasonScreen.ReasonListWidget.ReasonEntry(AbuseReportReason reason) {
        this.reason = reason;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        int i = this.getContentX() + 1;
        int j = this.getContentY() + (this.getContentHeight() - ((AbuseReportReasonScreen)ReasonListWidget.this.field_39619).textRenderer.fontHeight) / 2 + 1;
        context.drawTextWithShadow(ReasonListWidget.this.field_39619.textRenderer, this.reason.getText(), i, j, -1);
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
