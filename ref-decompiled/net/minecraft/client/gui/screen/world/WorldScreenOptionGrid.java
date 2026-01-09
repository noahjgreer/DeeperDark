package net.minecraft.client.gui.screen.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
class WorldScreenOptionGrid {
   private static final int BUTTON_WIDTH = 44;
   private final List options;
   private final LayoutWidget layout;

   WorldScreenOptionGrid(List options, LayoutWidget layout) {
      this.options = options;
      this.layout = layout;
   }

   public LayoutWidget getLayout() {
      return this.layout;
   }

   public void refresh() {
      this.options.forEach(Option::refresh);
   }

   public static Builder builder(int width) {
      return new Builder(width);
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      final int width;
      private final List options = new ArrayList();
      int marginLeft;
      int rowSpacing = 4;
      int rows;
      Optional tooltipBoxDisplay = Optional.empty();

      public Builder(int width) {
         this.width = width;
      }

      void incrementRows() {
         ++this.rows;
      }

      public OptionBuilder add(Text text, BooleanSupplier getter, Consumer setter) {
         OptionBuilder optionBuilder = new OptionBuilder(text, getter, setter, 44);
         this.options.add(optionBuilder);
         return optionBuilder;
      }

      public Builder marginLeft(int marginLeft) {
         this.marginLeft = marginLeft;
         return this;
      }

      public Builder setRowSpacing(int rowSpacing) {
         this.rowSpacing = rowSpacing;
         return this;
      }

      public WorldScreenOptionGrid build() {
         GridWidget gridWidget = (new GridWidget()).setRowSpacing(this.rowSpacing);
         gridWidget.add(EmptyWidget.ofWidth(this.width - 44), 0, 0);
         gridWidget.add(EmptyWidget.ofWidth(44), 0, 1);
         List list = new ArrayList();
         this.rows = 0;
         Iterator var3 = this.options.iterator();

         while(var3.hasNext()) {
            OptionBuilder optionBuilder = (OptionBuilder)var3.next();
            list.add(optionBuilder.build(this, gridWidget, 0));
         }

         gridWidget.refreshPositions();
         WorldScreenOptionGrid worldScreenOptionGrid = new WorldScreenOptionGrid(list, gridWidget);
         worldScreenOptionGrid.refresh();
         return worldScreenOptionGrid;
      }

      public Builder withTooltipBox(int maxInfoRows, boolean alwaysMaxHeight) {
         this.tooltipBoxDisplay = Optional.of(new TooltipBoxDisplay(maxInfoRows, alwaysMaxHeight));
         return this;
      }
   }

   @Environment(EnvType.CLIENT)
   static record TooltipBoxDisplay(int maxInfoRows, boolean alwaysMaxHeight) {
      final int maxInfoRows;
      final boolean alwaysMaxHeight;

      TooltipBoxDisplay(int i, boolean bl) {
         this.maxInfoRows = i;
         this.alwaysMaxHeight = bl;
      }

      public int maxInfoRows() {
         return this.maxInfoRows;
      }

      public boolean alwaysMaxHeight() {
         return this.alwaysMaxHeight;
      }
   }

   @Environment(EnvType.CLIENT)
   static record Option(CyclingButtonWidget button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
      Option(CyclingButtonWidget button, BooleanSupplier getter, @Nullable BooleanSupplier toggleable) {
         this.button = button;
         this.getter = getter;
         this.toggleable = toggleable;
      }

      public void refresh() {
         this.button.setValue(this.getter.getAsBoolean());
         if (this.toggleable != null) {
            this.button.active = this.toggleable.getAsBoolean();
         }

      }

      public CyclingButtonWidget button() {
         return this.button;
      }

      public BooleanSupplier getter() {
         return this.getter;
      }

      @Nullable
      public BooleanSupplier toggleable() {
         return this.toggleable;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class OptionBuilder {
      private final Text text;
      private final BooleanSupplier getter;
      private final Consumer setter;
      @Nullable
      private Text tooltip;
      @Nullable
      private BooleanSupplier toggleable;
      private final int buttonWidth;

      OptionBuilder(Text text, BooleanSupplier getter, Consumer setter, int buttonWidth) {
         this.text = text;
         this.getter = getter;
         this.setter = setter;
         this.buttonWidth = buttonWidth;
      }

      public OptionBuilder toggleable(BooleanSupplier toggleable) {
         this.toggleable = toggleable;
         return this;
      }

      public OptionBuilder tooltip(Text tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      Option build(Builder gridBuilder, GridWidget gridWidget, int row) {
         gridBuilder.incrementRows();
         TextWidget textWidget = (new TextWidget(this.text, MinecraftClient.getInstance().textRenderer)).alignLeft();
         gridWidget.add(textWidget, gridBuilder.rows, row, (Positioner)gridWidget.copyPositioner().relative(0.0F, 0.5F).marginLeft(gridBuilder.marginLeft));
         Optional optional = gridBuilder.tooltipBoxDisplay;
         CyclingButtonWidget.Builder builder = CyclingButtonWidget.onOffBuilder(this.getter.getAsBoolean());
         builder.omitKeyText();
         boolean bl = this.tooltip != null && optional.isEmpty();
         if (bl) {
            Tooltip tooltip = Tooltip.of(this.tooltip);
            builder.tooltip((value) -> {
               return tooltip;
            });
         }

         if (this.tooltip != null && !bl) {
            builder.narration((button) -> {
               return ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage(), this.tooltip);
            });
         } else {
            builder.narration((button) -> {
               return ScreenTexts.joinSentences(this.text, button.getGenericNarrationMessage());
            });
         }

         CyclingButtonWidget cyclingButtonWidget = builder.build(0, 0, this.buttonWidth, 20, Text.empty(), (button, value) -> {
            this.setter.accept(value);
         });
         if (this.toggleable != null) {
            cyclingButtonWidget.active = this.toggleable.getAsBoolean();
         }

         gridWidget.add(cyclingButtonWidget, gridBuilder.rows, row + 1, (Positioner)gridWidget.copyPositioner().alignRight());
         if (this.tooltip != null) {
            optional.ifPresent((tooltipBoxDisplay) -> {
               Text text = this.tooltip.copy().formatted(Formatting.GRAY);
               TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
               MultilineTextWidget multilineTextWidget = new MultilineTextWidget(text, textRenderer);
               multilineTextWidget.setMaxWidth(gridBuilder.width - gridBuilder.marginLeft - this.buttonWidth);
               multilineTextWidget.setMaxRows(tooltipBoxDisplay.maxInfoRows());
               gridBuilder.incrementRows();
               int var10000;
               if (tooltipBoxDisplay.alwaysMaxHeight) {
                  Objects.requireNonNull(textRenderer);
                  var10000 = 9 * tooltipBoxDisplay.maxInfoRows - multilineTextWidget.getHeight();
               } else {
                  var10000 = 0;
               }

               int j = var10000;
               gridWidget.add(multilineTextWidget, gridBuilder.rows, row, (Positioner)gridWidget.copyPositioner().marginTop(-gridBuilder.rowSpacing).marginBottom(j));
            });
         }

         return new Option(cyclingButtonWidget, this.getter, this.toggleable);
      }
   }
}
