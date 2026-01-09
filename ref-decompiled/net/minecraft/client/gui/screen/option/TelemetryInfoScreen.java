package net.minecraft.client.gui.screen.option;

import java.net.URI;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Urls;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TelemetryInfoScreen extends Screen {
   private static final Text TITLE_TEXT = Text.translatable("telemetry_info.screen.title");
   private static final Text DESCRIPTION_TEXT = Text.translatable("telemetry_info.screen.description").withColor(-4539718);
   private static final Text PRIVACY_STATEMENT_TEXT = Text.translatable("telemetry_info.button.privacy_statement");
   private static final Text GIVE_FEEDBACK_TEXT = Text.translatable("telemetry_info.button.give_feedback");
   private static final Text SHOW_DATA_TEXT = Text.translatable("telemetry_info.button.show_data");
   private static final Text OPT_IN_DESCRIPTION_TEXT = Text.translatable("telemetry_info.opt_in.description");
   private static final int MARGIN = 8;
   private static final boolean OPTIONAL_TELEMETRY_ENABLED_BY_API = MinecraftClient.getInstance().isOptionalTelemetryEnabledByApi();
   private final Screen parent;
   private final GameOptions options;
   private final ThreePartsLayoutWidget layout;
   @Nullable
   private TelemetryEventWidget telemetryEventWidget;
   @Nullable
   private MultilineTextWidget textWidget;
   private double scroll;

   public TelemetryInfoScreen(Screen parent, GameOptions options) {
      super(TITLE_TEXT);
      Objects.requireNonNull(MinecraftClient.getInstance().textRenderer);
      this.layout = new ThreePartsLayoutWidget(this, 16 + 9 * 5 + 20, OPTIONAL_TELEMETRY_ENABLED_BY_API ? 33 + CheckboxWidget.getCheckboxSize(MinecraftClient.getInstance().textRenderer) : 33);
      this.parent = parent;
      this.options = options;
   }

   public Text getNarratedTitle() {
      return ScreenTexts.joinSentences(super.getNarratedTitle(), DESCRIPTION_TEXT);
   }

   protected void init() {
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(4));
      directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
      directionalLayoutWidget.add(new TextWidget(TITLE_TEXT, this.textRenderer));
      this.textWidget = (MultilineTextWidget)directionalLayoutWidget.add((new MultilineTextWidget(DESCRIPTION_TEXT, this.textRenderer)).setCentered(true));
      DirectionalLayoutWidget directionalLayoutWidget2 = (DirectionalLayoutWidget)directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal().spacing(8));
      directionalLayoutWidget2.add(ButtonWidget.builder(PRIVACY_STATEMENT_TEXT, this::openPrivacyStatementPage).build());
      directionalLayoutWidget2.add(ButtonWidget.builder(GIVE_FEEDBACK_TEXT, this::openFeedbackPage).build());
      DirectionalLayoutWidget directionalLayoutWidget3 = (DirectionalLayoutWidget)this.layout.addFooter(DirectionalLayoutWidget.vertical().spacing(4));
      if (OPTIONAL_TELEMETRY_ENABLED_BY_API) {
         directionalLayoutWidget3.add(this.createOptInCheckbox());
      }

      DirectionalLayoutWidget directionalLayoutWidget4 = (DirectionalLayoutWidget)directionalLayoutWidget3.add(DirectionalLayoutWidget.horizontal().spacing(8));
      directionalLayoutWidget4.add(ButtonWidget.builder(SHOW_DATA_TEXT, this::openLogDirectory).build());
      directionalLayoutWidget4.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.close();
      }).build());
      DirectionalLayoutWidget directionalLayoutWidget5 = (DirectionalLayoutWidget)this.layout.addBody(DirectionalLayoutWidget.vertical().spacing(8));
      this.telemetryEventWidget = (TelemetryEventWidget)directionalLayoutWidget5.add(new TelemetryEventWidget(0, 0, this.width - 40, this.layout.getContentHeight(), this.textRenderer));
      this.telemetryEventWidget.setScrollConsumer((scroll) -> {
         this.scroll = scroll;
      });
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      if (this.telemetryEventWidget != null) {
         this.telemetryEventWidget.setScrollY(this.scroll);
         this.telemetryEventWidget.setWidth(this.width - 40);
         this.telemetryEventWidget.setHeight(this.layout.getContentHeight());
         this.telemetryEventWidget.initContents();
      }

      if (this.textWidget != null) {
         this.textWidget.setMaxWidth(this.width - 16);
      }

      this.layout.refreshPositions();
   }

   protected void setInitialFocus() {
      if (this.telemetryEventWidget != null) {
         this.setInitialFocus(this.telemetryEventWidget);
      }

   }

   private ClickableWidget createOptInCheckbox() {
      SimpleOption simpleOption = this.options.getTelemetryOptInExtra();
      return CheckboxWidget.builder(OPT_IN_DESCRIPTION_TEXT, this.textRenderer).option(simpleOption).callback(this::updateOptIn).build();
   }

   private void updateOptIn(ClickableWidget checkbox, boolean checked) {
      if (this.telemetryEventWidget != null) {
         this.telemetryEventWidget.refresh(checked);
      }

   }

   private void openPrivacyStatementPage(ButtonWidget button) {
      ConfirmLinkScreen.open(this, (URI)Urls.PRIVACY_STATEMENT);
   }

   private void openFeedbackPage(ButtonWidget button) {
      ConfirmLinkScreen.open(this, (URI)Urls.JAVA_FEEDBACK);
   }

   private void openLogDirectory(ButtonWidget button) {
      Util.getOperatingSystem().open(this.client.getTelemetryManager().getLogManager());
   }

   public void close() {
      this.client.setScreen(this.parent);
   }
}
