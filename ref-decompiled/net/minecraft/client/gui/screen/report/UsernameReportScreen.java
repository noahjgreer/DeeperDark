/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.ReportScreen
 *  net.minecraft.client.gui.screen.report.UsernameReportScreen
 *  net.minecraft.client.gui.widget.EditBoxWidget
 *  net.minecraft.client.gui.widget.LayoutWidgets
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.session.report.AbuseReport$Builder
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.UsernameAbuseReport
 *  net.minecraft.client.session.report.UsernameAbuseReport$Builder
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.report;

import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.ReportScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.UsernameAbuseReport;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class UsernameReportScreen
extends ReportScreen<UsernameAbuseReport.Builder> {
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.abuseReport.name.title");
    private static final Text field_52851 = Text.translatable((String)"gui.abuseReport.name.comment_box_label");
    private @Nullable EditBoxWidget commentsBox;

    private UsernameReportScreen(Screen parent, AbuseReportContext context, UsernameAbuseReport.Builder reportBuilder) {
        super(TITLE_TEXT, parent, context, (AbuseReport.Builder)reportBuilder);
    }

    public UsernameReportScreen(Screen parent, AbuseReportContext context, UUID reportedPlayerUuid, String username) {
        this(parent, context, new UsernameAbuseReport.Builder(reportedPlayerUuid, username, context.getSender().getLimits()));
    }

    public UsernameReportScreen(Screen parent, AbuseReportContext context, UsernameAbuseReport report) {
        this(parent, context, new UsernameAbuseReport.Builder(report, context.getSender().getLimits()));
    }

    protected void addContent() {
        MutableText text = Text.literal((String)((UsernameAbuseReport)((UsernameAbuseReport.Builder)this.reportBuilder).getReport()).getUsername()).formatted(Formatting.YELLOW);
        this.layout.add((Widget)new TextWidget((Text)Text.translatable((String)"gui.abuseReport.name.reporting", (Object[])new Object[]{text}), this.textRenderer), positioner -> positioner.alignHorizontalCenter().margin(0, 8));
        Objects.requireNonNull(this.textRenderer);
        this.commentsBox = this.createCommentsBox(280, 9 * 8, comments -> {
            ((UsernameAbuseReport.Builder)this.reportBuilder).setOpinionComments(comments);
            this.onChange();
        });
        this.layout.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.commentsBox, (Text)field_52851, positioner -> positioner.marginBottom(12)));
    }

    public boolean mouseReleased(Click click) {
        if (super.mouseReleased(click)) {
            return true;
        }
        if (this.commentsBox != null) {
            return this.commentsBox.mouseReleased(click);
        }
        return false;
    }
}

