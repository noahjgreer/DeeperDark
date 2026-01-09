package net.minecraft.client.gui.screen.report;

import com.mojang.authlib.minecraft.report.AbuseReportLimits;
import com.mojang.logging.LogUtils;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TaskScreen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.session.report.AbuseReport;
import net.minecraft.client.session.report.AbuseReportContext;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Nullables;
import net.minecraft.util.TextifiedException;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class ReportScreen extends Screen {
   private static final Text REPORT_SENT_MESSAGE_TEXT = Text.translatable("gui.abuseReport.report_sent_msg");
   private static final Text SENDING_TITLE_TEXT;
   private static final Text SENT_TITLE_TEXT;
   private static final Text ERROR_TITLE_TEXT;
   private static final Text GENERIC_ERROR_TEXT;
   protected static final Text SEND_TEXT;
   protected static final Text OBSERVED_WHAT_TEXT;
   protected static final Text SELECT_REASON_TEXT;
   private static final Text DESCRIBE_TEXT;
   protected static final Text MORE_COMMENTS_TEXT;
   private static final Text COMMENTS_TEXT;
   private static final Text ATTESTATION_TEXT;
   protected static final int field_52303 = 120;
   protected static final int field_46016 = 20;
   protected static final int field_46017 = 280;
   protected static final int field_46018 = 8;
   private static final Logger LOGGER;
   protected final Screen parent;
   protected final AbuseReportContext context;
   protected final DirectionalLayoutWidget layout = DirectionalLayoutWidget.vertical().spacing(8);
   protected AbuseReport.Builder reportBuilder;
   private CheckboxWidget checkbox;
   protected ButtonWidget sendButton;

   protected ReportScreen(Text title, Screen parent, AbuseReportContext context, AbuseReport.Builder reportBuilder) {
      super(title);
      this.parent = parent;
      this.context = context;
      this.reportBuilder = reportBuilder;
   }

   protected EditBoxWidget createCommentsBox(int width, int height, Consumer changeListener) {
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
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
      this.refreshWidgetPositions();
   }

   protected void addTitle() {
      this.layout.add(new TextWidget(this.title, this.textRenderer));
   }

   protected abstract void addContent();

   protected void addAttestationCheckboxAndSendButton() {
      this.checkbox = (CheckboxWidget)this.layout.add(CheckboxWidget.builder(ATTESTATION_TEXT, this.textRenderer).checked(this.reportBuilder.isAttested()).maxWidth(280).callback((checkbox, attested) -> {
         this.reportBuilder.setAttested(attested);
         this.onChange();
      }).build());
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.add(DirectionalLayoutWidget.horizontal().spacing(8));
      directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, (button) -> {
         this.close();
      }).width(120).build());
      this.sendButton = (ButtonWidget)directionalLayoutWidget.add(ButtonWidget.builder(SEND_TEXT, (button) -> {
         this.trySend();
      }).width(120).build());
   }

   protected void onChange() {
      AbuseReport.ValidationError validationError = this.reportBuilder.validate();
      this.sendButton.active = validationError == null && this.checkbox.isChecked();
      this.sendButton.setTooltip((Tooltip)Nullables.map(validationError, AbuseReport.ValidationError::createTooltip));
   }

   protected void refreshWidgetPositions() {
      this.layout.refreshPositions();
      SimplePositioningWidget.setPos(this.layout, this.getNavigationFocus());
   }

   protected void trySend() {
      this.reportBuilder.build(this.context).ifLeft((reportWithId) -> {
         CompletableFuture completableFuture = this.context.getSender().send(reportWithId.id(), reportWithId.reportType(), reportWithId.report());
         this.client.setScreen(TaskScreen.createRunningScreen(SENDING_TITLE_TEXT, ScreenTexts.CANCEL, () -> {
            this.client.setScreen(this);
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
         }, this.client);
      }).ifRight((validationError) -> {
         this.showError(validationError.message());
      });
   }

   private void onSent() {
      this.resetDraft();
      this.client.setScreen(TaskScreen.createResultScreen(SENT_TITLE_TEXT, REPORT_SENT_MESSAGE_TEXT, ScreenTexts.DONE, () -> {
         this.client.setScreen((Screen)null);
      }));
   }

   private void onSendError(Throwable error) {
      LOGGER.error("Encountered error while sending abuse report", error);
      Throwable var4 = error.getCause();
      Text text;
      if (var4 instanceof TextifiedException textifiedException) {
         text = textifiedException.getMessageText();
      } else {
         text = GENERIC_ERROR_TEXT;
      }

      this.showError(text);
   }

   private void showError(Text errorMessage) {
      Text text = errorMessage.copy().formatted(Formatting.RED);
      this.client.setScreen(TaskScreen.createResultScreen(ERROR_TITLE_TEXT, text, ScreenTexts.BACK, () -> {
         this.client.setScreen(this);
      }));
   }

   void saveDraft() {
      if (this.reportBuilder.hasEnoughInfo()) {
         this.context.setDraft(this.reportBuilder.getReport().copy());
      }

   }

   void resetDraft() {
      this.context.setDraft((AbuseReport)null);
   }

   public void close() {
      if (this.reportBuilder.hasEnoughInfo()) {
         this.client.setScreen(new DiscardWarningScreen());
      } else {
         this.client.setScreen(this.parent);
      }

   }

   public void removed() {
      this.saveDraft();
      super.removed();
   }

   static {
      SENDING_TITLE_TEXT = Text.translatable("gui.abuseReport.sending.title").formatted(Formatting.BOLD);
      SENT_TITLE_TEXT = Text.translatable("gui.abuseReport.sent.title").formatted(Formatting.BOLD);
      ERROR_TITLE_TEXT = Text.translatable("gui.abuseReport.error.title").formatted(Formatting.BOLD);
      GENERIC_ERROR_TEXT = Text.translatable("gui.abuseReport.send.generic_error");
      SEND_TEXT = Text.translatable("gui.abuseReport.send");
      OBSERVED_WHAT_TEXT = Text.translatable("gui.abuseReport.observed_what");
      SELECT_REASON_TEXT = Text.translatable("gui.abuseReport.select_reason");
      DESCRIBE_TEXT = Text.translatable("gui.abuseReport.describe");
      MORE_COMMENTS_TEXT = Text.translatable("gui.abuseReport.more_comments");
      COMMENTS_TEXT = Text.translatable("gui.abuseReport.comments");
      ATTESTATION_TEXT = Text.translatable("gui.abuseReport.attestation");
      LOGGER = LogUtils.getLogger();
   }

   @Environment(EnvType.CLIENT)
   private class DiscardWarningScreen extends WarningScreen {
      private static final Text TITLE;
      private static final Text MESSAGE;
      private static final Text RETURN_BUTTON_TEXT;
      private static final Text DRAFT_BUTTON_TEXT;
      private static final Text DISCARD_BUTTON_TEXT;

      protected DiscardWarningScreen() {
         super(TITLE, MESSAGE, MESSAGE);
      }

      protected LayoutWidget getLayout() {
         DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(8);
         directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
         DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
         directionalLayoutWidget2.add(ButtonWidget.builder(RETURN_BUTTON_TEXT, (button) -> {
            this.close();
         }).build());
         directionalLayoutWidget2.add(ButtonWidget.builder(DRAFT_BUTTON_TEXT, (button) -> {
            ReportScreen.this.saveDraft();
            this.client.setScreen(ReportScreen.this.parent);
         }).build());
         directionalLayoutWidget.add(ButtonWidget.builder(DISCARD_BUTTON_TEXT, (button) -> {
            ReportScreen.this.resetDraft();
            this.client.setScreen(ReportScreen.this.parent);
         }).build());
         return directionalLayoutWidget;
      }

      public void close() {
         this.client.setScreen(ReportScreen.this);
      }

      public boolean shouldCloseOnEsc() {
         return false;
      }

      static {
         TITLE = Text.translatable("gui.abuseReport.discard.title").formatted(Formatting.BOLD);
         MESSAGE = Text.translatable("gui.abuseReport.discard.content");
         RETURN_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.return");
         DRAFT_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.draft");
         DISCARD_BUTTON_TEXT = Text.translatable("gui.abuseReport.discard.discard");
      }
   }
}
