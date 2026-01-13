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
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.dialog.input.NumberRangeInputControl;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
static class InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget
extends SliderWidget {
    private final NumberRangeInputControl inputControl;

    InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget(NumberRangeInputControl inputControl, double value) {
        super(0, 0, inputControl.width(), 20, InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.getFormattedLabel(inputControl, value), value);
        this.inputControl = inputControl;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.getFormattedLabel(this.inputControl, this.value));
    }

    @Override
    protected void applyValue() {
    }

    public String getLabel() {
        return InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.getLabel(this.inputControl, this.value);
    }

    public float getActualValue() {
        return InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.getActualValue(this.inputControl, this.value);
    }

    private static float getActualValue(NumberRangeInputControl inputControl, double sliderProgress) {
        return inputControl.rangeInfo().sliderProgressToValue((float)sliderProgress);
    }

    private static String getLabel(NumberRangeInputControl inputControl, double sliderProgress) {
        return InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.valueToString(InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.getActualValue(inputControl, sliderProgress));
    }

    private static Text getFormattedLabel(NumberRangeInputControl inputControl, double sliderProgress) {
        return inputControl.getFormattedLabel(InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget.getLabel(inputControl, sliderProgress));
    }

    private static String valueToString(float value) {
        int i = (int)value;
        if ((float)i == value) {
            return Integer.toString(i);
        }
        return Float.toString(value);
    }
}
