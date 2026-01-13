/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.dialog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class InputControlHandlers.NumberRangeInputControlHandler
implements InputControlHandler<NumberRangeInputControl> {
    InputControlHandlers.NumberRangeInputControlHandler() {
    }

    @Override
    public void addControl(NumberRangeInputControl numberRangeInputControl, Screen screen, InputControlHandler.Output output) {
        float f = numberRangeInputControl.rangeInfo().getInitialSliderProgress();
        final RangeSliderWidget rangeSliderWidget = new RangeSliderWidget(numberRangeInputControl, f);
        output.accept(rangeSliderWidget, new DialogAction.ValueGetter(){

            @Override
            public String get() {
                return rangeSliderWidget.getLabel();
            }

            @Override
            public NbtElement getAsNbt() {
                return NbtFloat.of(rangeSliderWidget.getActualValue());
            }
        });
    }

    @Override
    public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
        this.addControl((NumberRangeInputControl)inputControl, screen, output);
    }

    @Environment(value=EnvType.CLIENT)
    static class RangeSliderWidget
    extends SliderWidget {
        private final NumberRangeInputControl inputControl;

        RangeSliderWidget(NumberRangeInputControl inputControl, double value) {
            super(0, 0, inputControl.width(), 20, RangeSliderWidget.getFormattedLabel(inputControl, value), value);
            this.inputControl = inputControl;
        }

        @Override
        protected void updateMessage() {
            this.setMessage(RangeSliderWidget.getFormattedLabel(this.inputControl, this.value));
        }

        @Override
        protected void applyValue() {
        }

        public String getLabel() {
            return RangeSliderWidget.getLabel(this.inputControl, this.value);
        }

        public float getActualValue() {
            return RangeSliderWidget.getActualValue(this.inputControl, this.value);
        }

        private static float getActualValue(NumberRangeInputControl inputControl, double sliderProgress) {
            return inputControl.rangeInfo().sliderProgressToValue((float)sliderProgress);
        }

        private static String getLabel(NumberRangeInputControl inputControl, double sliderProgress) {
            return RangeSliderWidget.valueToString(RangeSliderWidget.getActualValue(inputControl, sliderProgress));
        }

        private static Text getFormattedLabel(NumberRangeInputControl inputControl, double sliderProgress) {
            return inputControl.getFormattedLabel(RangeSliderWidget.getLabel(inputControl, sliderProgress));
        }

        private static String valueToString(float value) {
            int i = (int)value;
            if ((float)i == value) {
                return Integer.toString(i);
            }
            return Float.toString(value);
        }
    }
}
