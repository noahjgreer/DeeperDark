package net.minecraft.client.gui.screen.dialog;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.dialog.input.SingleOptionInputControl;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class InputControlHandlers {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map INPUT_CONTROL_HANDLERS = new HashMap();

   private static void register(MapCodec inputControlCodec, InputControlHandler inputControlHandler) {
      INPUT_CONTROL_HANDLERS.put(inputControlCodec, inputControlHandler);
   }

   @Nullable
   private static InputControlHandler getHandler(InputControl inputControl) {
      return (InputControlHandler)INPUT_CONTROL_HANDLERS.get(inputControl.getCodec());
   }

   public static void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
      InputControlHandler inputControlHandler = getHandler(inputControl);
      if (inputControlHandler == null) {
         LOGGER.warn("Unrecognized input control {}", inputControl);
      } else {
         inputControlHandler.addControl(inputControl, screen, output);
      }
   }

   public static void bootstrap() {
      register(TextInputControl.CODEC, new TextInputControlHandler());
      register(SingleOptionInputControl.CODEC, new SimpleOptionInputControlHandler());
      register(BooleanInputControl.CODEC, new BooleanInputControlHandler());
      register(NumberRangeInputControl.CODEC, new NumberRangeInputControlHandler());
   }

   @Environment(EnvType.CLIENT)
   private static class TextInputControlHandler implements InputControlHandler {
      TextInputControlHandler() {
      }

      public void addControl(TextInputControl textInputControl, Screen screen, InputControlHandler.Output output) {
         TextRenderer textRenderer = screen.getTextRenderer();
         Object widget;
         final Supplier supplier;
         if (textInputControl.multiline().isPresent()) {
            TextInputControl.Multiline multiline = (TextInputControl.Multiline)textInputControl.multiline().get();
            int i = (Integer)multiline.height().orElseGet(() -> {
               int i = (Integer)multiline.maxLines().orElse(4);
               Objects.requireNonNull(textRenderer);
               return Math.min(9 * i + 8, 512);
            });
            EditBoxWidget editBoxWidget = EditBoxWidget.builder().build(textRenderer, textInputControl.width(), i, ScreenTexts.EMPTY);
            editBoxWidget.setMaxLength(textInputControl.maxLength());
            Optional var10000 = multiline.maxLines();
            Objects.requireNonNull(editBoxWidget);
            var10000.ifPresent(editBoxWidget::setMaxLines);
            editBoxWidget.setText(textInputControl.initial());
            widget = editBoxWidget;
            Objects.requireNonNull(editBoxWidget);
            supplier = editBoxWidget::getText;
         } else {
            TextFieldWidget textFieldWidget = new TextFieldWidget(textRenderer, textInputControl.width(), 20, textInputControl.label());
            textFieldWidget.setMaxLength(textInputControl.maxLength());
            textFieldWidget.setText(textInputControl.initial());
            widget = textFieldWidget;
            Objects.requireNonNull(textFieldWidget);
            supplier = textFieldWidget::getText;
         }

         Widget widget2 = textInputControl.labelVisible() ? LayoutWidgets.createLabeledWidget(textRenderer, (Widget)widget, textInputControl.label()) : widget;
         output.accept((Widget)widget2, new DialogAction.ValueGetter(this) {
            public String get() {
               return NbtString.method_72226((String)supplier.get());
            }

            public NbtElement getAsNbt() {
               return NbtString.of((String)supplier.get());
            }
         });
      }

      // $FF: synthetic method
      public void addControl(final InputControl inputControl, final Screen screen, final InputControlHandler.Output output) {
         this.addControl((TextInputControl)inputControl, screen, output);
      }
   }

   @Environment(EnvType.CLIENT)
   static class SimpleOptionInputControlHandler implements InputControlHandler {
      public void addControl(SingleOptionInputControl singleOptionInputControl, Screen screen, InputControlHandler.Output output) {
         CyclingButtonWidget.Builder builder = CyclingButtonWidget.builder(SingleOptionInputControl.Entry::getDisplay).values((Collection)singleOptionInputControl.entries()).optionTextOmitted(!singleOptionInputControl.labelVisible());
         Optional optional = singleOptionInputControl.getInitialEntry();
         if (optional.isPresent()) {
            builder = builder.initially((SingleOptionInputControl.Entry)optional.get());
         }

         CyclingButtonWidget cyclingButtonWidget = builder.build(0, 0, singleOptionInputControl.width(), 20, singleOptionInputControl.label());
         output.accept(cyclingButtonWidget, DialogAction.ValueGetter.of(() -> {
            return ((SingleOptionInputControl.Entry)cyclingButtonWidget.getValue()).id();
         }));
      }

      // $FF: synthetic method
      public void addControl(final InputControl inputControl, final Screen screen, final InputControlHandler.Output output) {
         this.addControl((SingleOptionInputControl)inputControl, screen, output);
      }
   }

   @Environment(EnvType.CLIENT)
   private static class BooleanInputControlHandler implements InputControlHandler {
      BooleanInputControlHandler() {
      }

      public void addControl(final BooleanInputControl booleanInputControl, Screen screen, InputControlHandler.Output output) {
         TextRenderer textRenderer = screen.getTextRenderer();
         final CheckboxWidget checkboxWidget = CheckboxWidget.builder(booleanInputControl.label(), textRenderer).checked(booleanInputControl.initial()).build();
         output.accept(checkboxWidget, new DialogAction.ValueGetter(this) {
            public String get() {
               return checkboxWidget.isChecked() ? booleanInputControl.onTrue() : booleanInputControl.onFalse();
            }

            public NbtElement getAsNbt() {
               return NbtByte.of(checkboxWidget.isChecked());
            }
         });
      }

      // $FF: synthetic method
      public void addControl(final InputControl inputControl, final Screen screen, final InputControlHandler.Output output) {
         this.addControl((BooleanInputControl)inputControl, screen, output);
      }
   }

   @Environment(EnvType.CLIENT)
   static class NumberRangeInputControlHandler implements InputControlHandler {
      public void addControl(NumberRangeInputControl numberRangeInputControl, Screen screen, InputControlHandler.Output output) {
         float f = numberRangeInputControl.rangeInfo().getInitialSliderProgress();
         final RangeSliderWidget rangeSliderWidget = new RangeSliderWidget(numberRangeInputControl, (double)f);
         output.accept(rangeSliderWidget, new DialogAction.ValueGetter(this) {
            public String get() {
               return rangeSliderWidget.getLabel();
            }

            public NbtElement getAsNbt() {
               return NbtFloat.of(rangeSliderWidget.getActualValue());
            }
         });
      }

      // $FF: synthetic method
      public void addControl(final InputControl inputControl, final Screen screen, final InputControlHandler.Output output) {
         this.addControl((NumberRangeInputControl)inputControl, screen, output);
      }

      @Environment(EnvType.CLIENT)
      private static class RangeSliderWidget extends SliderWidget {
         private final NumberRangeInputControl inputControl;

         RangeSliderWidget(NumberRangeInputControl inputControl, double value) {
            super(0, 0, inputControl.width(), 20, getFormattedLabel(inputControl, value), value);
            this.inputControl = inputControl;
         }

         protected void updateMessage() {
            this.setMessage(getFormattedLabel(this.inputControl, this.value));
         }

         protected void applyValue() {
         }

         public String getLabel() {
            return getLabel(this.inputControl, this.value);
         }

         public float getActualValue() {
            return getActualValue(this.inputControl, this.value);
         }

         private static float getActualValue(NumberRangeInputControl inputControl, double sliderProgress) {
            return inputControl.rangeInfo().sliderProgressToValue((float)sliderProgress);
         }

         private static String getLabel(NumberRangeInputControl inputControl, double sliderProgress) {
            return valueToString(getActualValue(inputControl, sliderProgress));
         }

         private static Text getFormattedLabel(NumberRangeInputControl inputControl, double sliderProgress) {
            return inputControl.getFormattedLabel(getLabel(inputControl, sliderProgress));
         }

         private static String valueToString(float value) {
            int i = (int)value;
            return (float)i == value ? Integer.toString(i) : Float.toString(value);
         }
      }
   }
}
