package net.minecraft.client.gui.screen.option;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.ScrollableTextFieldWidget;
import net.minecraft.client.session.telemetry.TelemetryEventProperty;
import net.minecraft.client.session.telemetry.TelemetryEventType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TelemetryEventWidget extends ScrollableTextFieldWidget {
   private static final int MARGIN_X = 32;
   private static final String REQUIRED_TRANSLATION_KEY = "telemetry.event.required";
   private static final String OPTIONAL_TRANSLATION_KEY = "telemetry.event.optional";
   private static final String DISABLED_TRANSLATION_KEY = "telemetry.event.optional.disabled";
   private static final Text PROPERTY_TITLE_TEXT;
   private final TextRenderer textRenderer;
   private Contents contents;
   @Nullable
   private DoubleConsumer scrollConsumer;

   public TelemetryEventWidget(int x, int y, int width, int height, TextRenderer textRenderer) {
      super(x, y, width, height, Text.empty());
      this.textRenderer = textRenderer;
      this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
   }

   public void refresh(boolean optionalTelemetryEnabled) {
      this.contents = this.collectContents(optionalTelemetryEnabled);
      this.refreshScroll();
   }

   public void initContents() {
      this.contents = this.collectContents(MinecraftClient.getInstance().isOptionalTelemetryEnabled());
      this.refreshScroll();
   }

   private Contents collectContents(boolean optionalTelemetryEnabled) {
      ContentsBuilder contentsBuilder = new ContentsBuilder(this.getGridWidth());
      List list = new ArrayList(TelemetryEventType.getTypes());
      list.sort(Comparator.comparing(TelemetryEventType::isOptional));

      for(int i = 0; i < list.size(); ++i) {
         TelemetryEventType telemetryEventType = (TelemetryEventType)list.get(i);
         boolean bl = telemetryEventType.isOptional() && !optionalTelemetryEnabled;
         this.appendEventInfo(contentsBuilder, telemetryEventType, bl);
         if (i < list.size() - 1) {
            Objects.requireNonNull(this.textRenderer);
            contentsBuilder.appendSpace(9);
         }
      }

      return contentsBuilder.build();
   }

   public void setScrollConsumer(@Nullable DoubleConsumer scrollConsumer) {
      this.scrollConsumer = scrollConsumer;
   }

   public void setScrollY(double scrollY) {
      super.setScrollY(scrollY);
      if (this.scrollConsumer != null) {
         this.scrollConsumer.accept(this.getScrollY());
      }

   }

   protected int getContentsHeight() {
      return this.contents.grid().getHeight();
   }

   protected double getDeltaYPerScroll() {
      Objects.requireNonNull(this.textRenderer);
      return 9.0;
   }

   protected void renderContents(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      int i = this.getTextY();
      int j = this.getTextX();
      context.getMatrices().pushMatrix();
      context.getMatrices().translate((float)j, (float)i);
      this.contents.grid().forEachChild((widget) -> {
         widget.render(context, mouseX, mouseY, deltaTicks);
      });
      context.getMatrices().popMatrix();
   }

   protected void appendClickableNarrations(NarrationMessageBuilder builder) {
      builder.put(NarrationPart.TITLE, this.contents.narration());
   }

   private Text formatTitleText(Text title, boolean disabled) {
      return (Text)(disabled ? title.copy().formatted(Formatting.GRAY) : title);
   }

   private void appendEventInfo(ContentsBuilder builder, TelemetryEventType eventType, boolean disabled) {
      String string = eventType.isOptional() ? (disabled ? "telemetry.event.optional.disabled" : "telemetry.event.optional") : "telemetry.event.required";
      builder.appendText(this.textRenderer, this.formatTitleText(Text.translatable(string, eventType.getTitle()), disabled));
      builder.appendText(this.textRenderer, eventType.getDescription().formatted(Formatting.GRAY));
      Objects.requireNonNull(this.textRenderer);
      builder.appendSpace(9 / 2);
      builder.appendTitle(this.textRenderer, this.formatTitleText(PROPERTY_TITLE_TEXT, disabled), 2);
      this.appendProperties(eventType, builder, disabled);
   }

   private void appendProperties(TelemetryEventType eventType, ContentsBuilder builder, boolean disabled) {
      Iterator var4 = eventType.getProperties().iterator();

      while(var4.hasNext()) {
         TelemetryEventProperty telemetryEventProperty = (TelemetryEventProperty)var4.next();
         builder.appendTitle(this.textRenderer, this.formatTitleText(telemetryEventProperty.getTitle(), disabled));
      }

   }

   private int getGridWidth() {
      return this.width - this.getPadding();
   }

   static {
      PROPERTY_TITLE_TEXT = Text.translatable("telemetry_info.property_title").formatted(Formatting.UNDERLINE);
   }

   @Environment(EnvType.CLIENT)
   static record Contents(LayoutWidget grid, Text narration) {
      Contents(LayoutWidget layoutWidget, Text text) {
         this.grid = layoutWidget;
         this.narration = text;
      }

      public LayoutWidget grid() {
         return this.grid;
      }

      public Text narration() {
         return this.narration;
      }
   }

   @Environment(EnvType.CLIENT)
   static class ContentsBuilder {
      private final int gridWidth;
      private final DirectionalLayoutWidget layout;
      private final MutableText narration = Text.empty();

      public ContentsBuilder(int gridWidth) {
         this.gridWidth = gridWidth;
         this.layout = DirectionalLayoutWidget.vertical();
         this.layout.getMainPositioner().alignLeft();
         this.layout.add(EmptyWidget.ofWidth(gridWidth));
      }

      public void appendTitle(TextRenderer textRenderer, Text title) {
         this.appendTitle(textRenderer, title, 0);
      }

      public void appendTitle(TextRenderer textRenderer, Text title, int marginBottom) {
         this.layout.add((new MultilineTextWidget(title, textRenderer)).setMaxWidth(this.gridWidth), (Consumer)((positioner) -> {
            positioner.marginBottom(marginBottom);
         }));
         this.narration.append(title).append("\n");
      }

      public void appendText(TextRenderer textRenderer, Text text) {
         this.layout.add((new MultilineTextWidget(text, textRenderer)).setMaxWidth(this.gridWidth - 64).setCentered(true), (Consumer)((positioner) -> {
            positioner.alignHorizontalCenter().marginX(32);
         }));
         this.narration.append(text).append("\n");
      }

      public void appendSpace(int height) {
         this.layout.add(EmptyWidget.ofHeight(height));
      }

      public Contents build() {
         this.layout.refreshPositions();
         return new Contents(this.layout, this.narration);
      }
   }
}
