package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ButtonWidget extends PressableWidget {
   public static final int DEFAULT_WIDTH_SMALL = 120;
   public static final int DEFAULT_WIDTH = 150;
   public static final int field_49479 = 200;
   public static final int DEFAULT_HEIGHT = 20;
   public static final int field_46856 = 8;
   protected static final NarrationSupplier DEFAULT_NARRATION_SUPPLIER = (textSupplier) -> {
      return (MutableText)textSupplier.get();
   };
   protected final PressAction onPress;
   protected final NarrationSupplier narrationSupplier;

   public static Builder builder(Text message, PressAction onPress) {
      return new Builder(message, onPress);
   }

   protected ButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
      super(x, y, width, height, message);
      this.onPress = onPress;
      this.narrationSupplier = narrationSupplier;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   protected MutableText getNarrationMessage() {
      return this.narrationSupplier.createNarrationMessage(() -> {
         return super.getNarrationMessage();
      });
   }

   public void appendClickableNarrations(NarrationMessageBuilder builder) {
      this.appendDefaultNarrations(builder);
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final Text message;
      private final PressAction onPress;
      @Nullable
      private Tooltip tooltip;
      private int x;
      private int y;
      private int width = 150;
      private int height = 20;
      private NarrationSupplier narrationSupplier;

      public Builder(Text message, PressAction onPress) {
         this.narrationSupplier = ButtonWidget.DEFAULT_NARRATION_SUPPLIER;
         this.message = message;
         this.onPress = onPress;
      }

      public Builder position(int x, int y) {
         this.x = x;
         this.y = y;
         return this;
      }

      public Builder width(int width) {
         this.width = width;
         return this;
      }

      public Builder size(int width, int height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public Builder dimensions(int x, int y, int width, int height) {
         return this.position(x, y).size(width, height);
      }

      public Builder tooltip(@Nullable Tooltip tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public Builder narrationSupplier(NarrationSupplier narrationSupplier) {
         this.narrationSupplier = narrationSupplier;
         return this;
      }

      public ButtonWidget build() {
         ButtonWidget buttonWidget = new ButtonWidget(this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier);
         buttonWidget.setTooltip(this.tooltip);
         return buttonWidget;
      }
   }

   @Environment(EnvType.CLIENT)
   public interface PressAction {
      void onPress(ButtonWidget button);
   }

   @Environment(EnvType.CLIENT)
   public interface NarrationSupplier {
      MutableText createNarrationMessage(Supplier textSupplier);
   }
}
