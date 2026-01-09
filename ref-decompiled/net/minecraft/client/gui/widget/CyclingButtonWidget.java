package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CyclingButtonWidget extends PressableWidget {
   public static final BooleanSupplier HAS_ALT_DOWN = Screen::hasAltDown;
   private static final List BOOLEAN_VALUES;
   private final Text optionText;
   private int index;
   private Object value;
   private final Values values;
   private final Function valueToText;
   private final Function narrationMessageFactory;
   private final UpdateCallback callback;
   private final boolean optionTextOmitted;
   private final SimpleOption.TooltipFactory tooltipFactory;

   CyclingButtonWidget(int x, int y, int width, int height, Text message, Text optionText, int index, Object value, Values values, Function valueToText, Function narrationMessageFactory, UpdateCallback callback, SimpleOption.TooltipFactory tooltipFactory, boolean optionTextOmitted) {
      super(x, y, width, height, message);
      this.optionText = optionText;
      this.index = index;
      this.value = value;
      this.values = values;
      this.valueToText = valueToText;
      this.narrationMessageFactory = narrationMessageFactory;
      this.callback = callback;
      this.optionTextOmitted = optionTextOmitted;
      this.tooltipFactory = tooltipFactory;
      this.refreshTooltip();
   }

   private void refreshTooltip() {
      this.setTooltip(this.tooltipFactory.apply(this.value));
   }

   public void onPress() {
      if (Screen.hasShiftDown()) {
         this.cycle(-1);
      } else {
         this.cycle(1);
      }

   }

   private void cycle(int amount) {
      List list = this.values.getCurrent();
      this.index = MathHelper.floorMod(this.index + amount, list.size());
      Object object = list.get(this.index);
      this.internalSetValue(object);
      this.callback.onValueChange(this, object);
   }

   private Object getValue(int offset) {
      List list = this.values.getCurrent();
      return list.get(MathHelper.floorMod(this.index + offset, list.size()));
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      if (verticalAmount > 0.0) {
         this.cycle(-1);
      } else if (verticalAmount < 0.0) {
         this.cycle(1);
      }

      return true;
   }

   public void setValue(Object value) {
      List list = this.values.getCurrent();
      int i = list.indexOf(value);
      if (i != -1) {
         this.index = i;
      }

      this.internalSetValue(value);
   }

   private void internalSetValue(Object value) {
      Text text = this.composeText(value);
      this.setMessage(text);
      this.value = value;
      this.refreshTooltip();
   }

   private Text composeText(Object value) {
      return (Text)(this.optionTextOmitted ? (Text)this.valueToText.apply(value) : this.composeGenericOptionText(value));
   }

   private MutableText composeGenericOptionText(Object value) {
      return ScreenTexts.composeGenericOptionText(this.optionText, (Text)this.valueToText.apply(value));
   }

   public Object getValue() {
      return this.value;
   }

   protected MutableText getNarrationMessage() {
      return (MutableText)this.narrationMessageFactory.apply(this);
   }

   public void appendClickableNarrations(NarrationMessageBuilder builder) {
      builder.put(NarrationPart.TITLE, (Text)this.getNarrationMessage());
      if (this.active) {
         Object object = this.getValue(1);
         Text text = this.composeText(object);
         if (this.isFocused()) {
            builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.cycle_button.usage.focused", text));
         } else {
            builder.put(NarrationPart.USAGE, (Text)Text.translatable("narration.cycle_button.usage.hovered", text));
         }
      }

   }

   public MutableText getGenericNarrationMessage() {
      return getNarrationMessage((Text)(this.optionTextOmitted ? this.composeGenericOptionText(this.value) : this.getMessage()));
   }

   public static Builder builder(Function valueToText) {
      return new Builder(valueToText);
   }

   public static Builder onOffBuilder(Text on, Text off) {
      return (new Builder((value) -> {
         return value ? on : off;
      })).values((Collection)BOOLEAN_VALUES);
   }

   public static Builder onOffBuilder() {
      return (new Builder((value) -> {
         return value ? ScreenTexts.ON : ScreenTexts.OFF;
      })).values((Collection)BOOLEAN_VALUES);
   }

   public static Builder onOffBuilder(boolean initialValue) {
      return onOffBuilder().initially(initialValue);
   }

   static {
      BOOLEAN_VALUES = ImmutableList.of(Boolean.TRUE, Boolean.FALSE);
   }

   @Environment(EnvType.CLIENT)
   public interface Values {
      List getCurrent();

      List getDefaults();

      static Values of(Collection values) {
         final List list = ImmutableList.copyOf(values);
         return new Values() {
            public List getCurrent() {
               return list;
            }

            public List getDefaults() {
               return list;
            }
         };
      }

      static Values of(final BooleanSupplier alternativeToggle, List defaults, List alternatives) {
         final List list = ImmutableList.copyOf(defaults);
         final List list2 = ImmutableList.copyOf(alternatives);
         return new Values() {
            public List getCurrent() {
               return alternativeToggle.getAsBoolean() ? list2 : list;
            }

            public List getDefaults() {
               return list;
            }
         };
      }
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   public interface UpdateCallback {
      void onValueChange(CyclingButtonWidget button, Object value);
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private int initialIndex;
      @Nullable
      private Object value;
      private final Function valueToText;
      private SimpleOption.TooltipFactory tooltipFactory = (value) -> {
         return null;
      };
      private Function narrationMessageFactory = CyclingButtonWidget::getGenericNarrationMessage;
      private Values values = CyclingButtonWidget.Values.of(ImmutableList.of());
      private boolean optionTextOmitted;

      public Builder(Function valueToText) {
         this.valueToText = valueToText;
      }

      public Builder values(Collection values) {
         return this.values(CyclingButtonWidget.Values.of(values));
      }

      @SafeVarargs
      public final Builder values(Object... values) {
         return this.values((Collection)ImmutableList.copyOf(values));
      }

      public Builder values(List defaults, List alternatives) {
         return this.values(CyclingButtonWidget.Values.of(CyclingButtonWidget.HAS_ALT_DOWN, defaults, alternatives));
      }

      public Builder values(BooleanSupplier alternativeToggle, List defaults, List alternatives) {
         return this.values(CyclingButtonWidget.Values.of(alternativeToggle, defaults, alternatives));
      }

      public Builder values(Values values) {
         this.values = values;
         return this;
      }

      public Builder tooltip(SimpleOption.TooltipFactory tooltipFactory) {
         this.tooltipFactory = tooltipFactory;
         return this;
      }

      public Builder initially(Object value) {
         this.value = value;
         int i = this.values.getDefaults().indexOf(value);
         if (i != -1) {
            this.initialIndex = i;
         }

         return this;
      }

      public Builder narration(Function narrationMessageFactory) {
         this.narrationMessageFactory = narrationMessageFactory;
         return this;
      }

      public Builder optionTextOmitted(boolean optionTextOmitted) {
         this.optionTextOmitted = optionTextOmitted;
         return this;
      }

      public Builder omitKeyText() {
         return this.optionTextOmitted(true);
      }

      public CyclingButtonWidget build(Text optionText, UpdateCallback callback) {
         return this.build(0, 0, 150, 20, optionText, callback);
      }

      public CyclingButtonWidget build(int x, int y, int width, int height, Text optionText) {
         return this.build(x, y, width, height, optionText, (button, value) -> {
         });
      }

      public CyclingButtonWidget build(int x, int y, int width, int height, Text optionText, UpdateCallback callback) {
         List list = this.values.getDefaults();
         if (list.isEmpty()) {
            throw new IllegalStateException("No values for cycle button");
         } else {
            Object object = this.value != null ? this.value : list.get(this.initialIndex);
            Text text = (Text)this.valueToText.apply(object);
            Text text2 = this.optionTextOmitted ? text : ScreenTexts.composeGenericOptionText(optionText, text);
            return new CyclingButtonWidget(x, y, width, height, (Text)text2, optionText, this.initialIndex, object, this.values, this.valueToText, this.narrationMessageFactory, callback, this.tooltipFactory, this.optionTextOmitted);
         }
      }
   }
}
