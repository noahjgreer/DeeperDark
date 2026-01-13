/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.minecraft.report.AbuseReportLimits
 *  com.mojang.logging.LogUtils
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.ScreenRect
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.TaskScreen
 *  net.minecraft.client.gui.screen.report.ReportScreen
 *  net.minecraft.client.gui.screen.report.ReportScreen$DiscardWarningScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CheckboxWidget
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.EditBoxWidget
 *  net.minecraft.client.gui.widget.SimplePositioningWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.client.session.report.AbuseReport$Builder
 *  net.minecraft.client.session.report.AbuseReport$ValidationError
 *  net.minecraft.client.session.report.AbuseReportContext
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.Nullables
 *  net.minecraft.util.TextifiedException
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TaskScreen;
import net.minecraft.client.gui.screen.report.ReportScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.TextifiedException;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class ReportScreen<B extends AbuseReport.Builder<?>>
extends Screen {
    private static final Text REPORT_SENT_MESSAGE_TEXT = Text.translatable((String)"gui.abuseReport.report_sent_msg");
    private static final Text SENDING_TITLE_TEXT = Text.translatable((String)"gui.abuseReport.sending.title").formatted(Formatting.BOLD);
    private static final Text SENT_TITLE_TEXT = Text.translatable((String)"gui.abuseReport.sent.title").formatted(Formatting.BOLD);
    private static final Text ERROR_TITLE_TEXT = Text.translatable((String)"gui.abuseReport.error.title").formatted(Formatting.BOLD);
    private static final Text GENERIC_ERROR_TEXT = Text.translatable((String)"gui.abuseReport.send.generic_error");
    protected static final Text SEND_TEXT = Text.translatable((String)"gui.abuseReport.send");
    protected static final Text OBSERVED_WHAT_TEXT = Text.translatable((String)"gui.abuseReport.observed_what");
    protected static final Text SELECT_REASON_TEXT = Text.translatable((String)"gui.abuseReport.select_reason");
    private static final Text DESCRIBE_TEXT = Text.translatable((String)"gui.abuseReport.describe");
    protected static final Text MORE_COMMENTS_TEXT = Text.translatable((String)"gui.abuseReport.more_comments");
    private static final Text COMMENTS_TEXT = Text.translatable((String)"gui.abuseReport.comments");
    private static final Text ATTESTATION_TEXT = Text.translatable((String)"gui.abuseReport.attestation").withColor(-2039584);
    protected static final int field_52303 = 120;
    protected static final int field_46016 = 20;
    protected static final int field_46017 = 280;
    protected static final int field_46018 = 8;
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Screen parent;
    protected final AbuseReportContext context;
    protected final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(8);
    protected B reportBuilder;
    private CheckboxWidget checkbox;
    protected ButtonWidget sendButton;

    protected ReportScreen(Text title, Screen parent, AbuseReportContext context, B reportBuilder) {
        super(title);
        this.parent = parent;
        this.context = context;
        this.reportBuilder = reportBuilder;
    }

    protected EditBoxWidget createCommentsBox(int width, int height, Consumer<String> changeListener) {
        AbuseReportLimits abuseReportLimits = this.context.getSender().getLimits();
        EditBoxWidget editBoxWidget = EditBoxWidget.builder().placeholder(DESCRIBE_TEXT).build(this.textRenderer, width, height, COMMENTS_TEXT);
        editBoxWidget.setText(this.reportBuilder.getOpinionComments());
        editBoxWidget.setMaxLength(abuseReportLimits.maxOpinionCommentsLength());
        editBoxWidget.setChangeListener(changeListener);
        return editBoxWidget;
    }

    protected void init() {
        this.layout.getMainPositioner().alignHorizontalCenter();
        this.addTitle();
        this.addContent();
        this.addAttestationCheckboxAndSendButton();
        this.onChange();
        this.layout.forEachChild(child -> {
            ClickableWidget cfr_ignored_0 = (ClickableWidget)this.addDrawableChild(child);
        });
        this.refreshWidgetPositions();
    }

    protected void addTitle() {
        this.layout.add((Widget)new TextWidget(this.title, this.textRenderer));
    }

    protected abstract void addContent();

    protected void addAttestationCheckboxAndSendButton() {
        this.checkbox = (CheckboxWidget)this.layout.add((Widget)CheckboxWidget.builder((Text)ATTESTATION_TEXT, (TextRenderer)this.textRenderer).checked(this.reportBuilder.isAttested()).maxWidth(280).callback((checkbox, attested) -> {
            this.reportBuilder.setAttested(attested);
            this.onChange();
        }).build());
        DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.add((Widget)DirectionalLayoutWidget.horizontal().spacing(8));
        directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)ScreenTexts.BACK, button -> this.close()).width(120).build());
        this.sendButton = (ButtonWidget)directionalLayoutWidget.add((Widget)ButtonWidget.builder((Text)SEND_TEXT, button -> this.trySend()).width(120).build());
    }

    protected void onChange() {
        AbuseReport.ValidationError validationError = this.reportBuilder.validate();
        this.sendButton.active = validationError == null && this.checkbox.isChecked();
        this.sendButton.setTooltip((Tooltip)Nullables.map((Object)validationError, AbuseReport.ValidationError::createTooltip));
    }

    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        SimplePositioningWidget.setPos((Widget)this.layout, (ScreenRect)this.getNavigationFocus());
    }

    protected void trySend() {
        this.reportBuilder.build(this.context).ifLeft(reportWithId -> {
            CompletableFuture completableFuture = this.context.getSender().send(reportWithId.id(), reportWithId.reportType(), reportWithId.report());
            this.client.setScreen((Screen)TaskScreen.createRunningScreen((Text)SENDING_TITLE_TEXT, (Text)ScreenTexts.CANCEL, () -> {
                this.client.setScreen((Screen)this);
                completableFuture.cancel(true);
            }));
            completableFuture.handleAsync((v, throwable) -> {
                if (throwable == null) {
                    this.onSent();
                } else {
                    if (throwable instanceof CancellationException) {
                        return null;
                    }
                    this.onSendError(throwable);
                }
                return null;
            }, (Executor)this.client);
        }).ifRight(validationError -> this.showError(validationError.message()));
    }

    private void onSent() {
        this.resetDraft();
        this.client.setScreen((Screen)TaskScreen.createResultScreen((Text)SENT_TITLE_TEXT, (Text)REPORT_SENT_MESSAGE_TEXT, (Text)ScreenTexts.DONE, () -> this.client.setScreen(null)));
    }

    private void onSendError(Throwable error) {
        Text text;
        LOGGER.error("Encountered error while sending abuse report", error);
        Throwable throwable = error.getCause();
        if (throwable instanceof TextifiedException) {
            TextifiedException textifiedException = (TextifiedException)throwable;
            text = textifiedException.getMessageText();
        } else {
            text = GENERIC_ERROR_TEXT;
        }
        this.showError(text);
    }

    private void showError(Text errorMessage) {
        MutableText text = errorMessage.copy().formatted(Formatting.RED);
        this.client.setScreen((Screen)TaskScreen.createResultScreen((Text)ERROR_TITLE_TEXT, (Text)text, (Text)ScreenTexts.BACK, () -> this.client.setScreen((Screen)this)));
    }

    void saveDraft() {
        if (this.reportBuilder.hasEnoughInfo()) {
            this.context.setDraft(this.reportBuilder.getReport().copy());
        }
    }

    void resetDraft() {
        this.context.setDraft(null);
    }

    public void close() {
        if (this.reportBuilder.hasEnoughInfo()) {
            this.client.setScreen((Screen)new DiscardWarningScreen(this));
        } else {
            this.client.setScreen(this.parent);
        }
    }

    public void removed() {
        this.saveDraft();
        super.removed();
    }
}

