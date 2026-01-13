/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.report.AbuseReportReasonScreen
 *  net.minecraft.client.gui.screen.report.ChatReportScreen
 *  net.minecraft.client.gui.screen.report.ChatSelectionScreen
 *  net.minecraft.client.gui.screen.report.ReportScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.EditBoxWidget
 *  net.minecraft.client.gui.widget.LayoutWidgets
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.session.report.AbuseReport$Builder
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.client.session.report.AbuseReportReason
 *  net.minecraft.client.session.report.AbuseReportType
 *  net.minecraft.client.session.report.ChatAbuseReport
 *  net.minecraft.client.session.report.ChatAbuseReport$Builder
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.report;

import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Objects;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.report.AbuseReportReasonScreen;
import net.minecraft.client.gui.screen.report.ChatSelectionScreen;
import net.minecraft.client.gui.screen.report.ReportScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.client.session.report.AbuseReportReason;
import net.minecraft.client.session.report.AbuseReportType;
import net.minecraft.client.session.report.ChatAbuseReport;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class ChatReportScreen
extends ReportScreen<ChatAbuseReport.Builder> {
    private static final Text TITLE_TEXT = Text.translatable((String)"gui.chatReport.title");
    private static final Text SELECT_CHAT_TEXT = Text.translatable((String)"gui.chatReport.select_chat");
    private EditBoxWidget commentsBox;
    private ButtonWidget selectChatButton;
    private ButtonWidget selectReasonButton;

    private ChatReportScreen(Screen parent, AbuseReportContext context, ChatAbuseReport.Builder reportBuilder) {
        super(TITLE_TEXT, parent, context, (AbuseReport.Builder)reportBuilder);
    }

    public ChatReportScreen(Screen parent, AbuseReportContext reporter, UUID reportedPlayerUuid) {
        this(parent, reporter, new ChatAbuseReport.Builder(reportedPlayerUuid, reporter.getSender().getLimits()));
    }

    public ChatReportScreen(Screen parent, AbuseReportContext context, ChatAbuseReport report) {
        this(parent, context, new ChatAbuseReport.Builder(report, context.getSender().getLimits()));
    }

    protected void addContent() {
        this.selectChatButton = (ButtonWidget)this.layout.add((Widget)ButtonWidget.builder((Text)SELECT_CHAT_TEXT, button -> this.client.setScreen((Screen)new ChatSelectionScreen((Screen)this, this.context, (ChatAbuseReport.Builder)this.reportBuilder, updatedReportBuilder -> {
            this.reportBuilder = updatedReportBuilder;
            this.onChange();
        }))).width(280).build());
        this.selectReasonButton = ButtonWidget.builder((Text)SELECT_REASON_TEXT, button -> this.client.setScreen((Screen)new AbuseReportReasonScreen((Screen)this, ((ChatAbuseReport.Builder)this.reportBuilder).getReason(), AbuseReportType.CHAT, reason -> {
            ((ChatAbuseReport.Builder)this.reportBuilder).setReason(reason);
            this.onChange();
        }))).width(280).build();
        this.layout.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.selectReasonButton, (Text)OBSERVED_WHAT_TEXT));
        Objects.requireNonNull(this.textRenderer);
        this.commentsBox = this.createCommentsBox(280, 9 * 8, comments -> {
            ((ChatAbuseReport.Builder)this.reportBuilder).setOpinionComments(comments);
            this.onChange();
        });
        this.layout.add((Widget)LayoutWidgets.createLabeledWidget((TextRenderer)this.textRenderer, (Widget)this.commentsBox, (Text)MORE_COMMENTS_TEXT, positioner -> positioner.marginBottom(12)));
    }

    protected void onChange() {
        IntSet intSet = ((ChatAbuseReport.Builder)this.reportBuilder).getSelectedMessages();
        if (intSet.isEmpty()) {
            this.selectChatButton.setMessage(SELECT_CHAT_TEXT);
        } else {
            this.selectChatButton.setMessage((Text)Text.translatable((String)"gui.chatReport.selected_chat", (Object[])new Object[]{intSet.size()}));
        }
        AbuseReportReason abuseReportReason = ((ChatAbuseReport.Builder)this.reportBuilder).getReason();
        if (abuseReportReason != null) {
            this.selectReasonButton.setMessage(abuseReportReason.getText());
        } else {
            this.selectReasonButton.setMessage(SELECT_REASON_TEXT);
        }
        super.onChange();
    }

    public boolean mouseReleased(Click click) {
        if (super.mouseReleased(click)) {
            return true;
        }
        return this.commentsBox.mouseReleased(click);
    }
}

