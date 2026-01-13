/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.session.report;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public record AbuseReport.ValidationError(Text message) {
    public static final AbuseReport.ValidationError NO_REASON = new AbuseReport.ValidationError(Text.translatable("gui.abuseReport.send.no_reason"));
    public static final AbuseReport.ValidationError NO_REPORTED_MESSAGES = new AbuseReport.ValidationError(Text.translatable("gui.chatReport.send.no_reported_messages"));
    public static final AbuseReport.ValidationError TOO_MANY_MESSAGES = new AbuseReport.ValidationError(Text.translatable("gui.chatReport.send.too_many_messages"));
    public static final AbuseReport.ValidationError COMMENTS_TOO_LONG = new AbuseReport.ValidationError(Text.translatable("gui.abuseReport.send.comment_too_long"));
    public static final AbuseReport.ValidationError NOT_ATTESTED = new AbuseReport.ValidationError(Text.translatable("gui.abuseReport.send.not_attested"));

    public Tooltip createTooltip() {
        return Tooltip.of(this.message);
    }
}
