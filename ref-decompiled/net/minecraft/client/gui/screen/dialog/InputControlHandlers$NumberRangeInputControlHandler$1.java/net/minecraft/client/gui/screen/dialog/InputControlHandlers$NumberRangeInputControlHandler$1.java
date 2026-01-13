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
import net.minecraft.client.gui.screen.dialog.InputControlHandlers;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;

@Environment(value=EnvType.CLIENT)
class InputControlHandlers.NumberRangeInputControlHandler.1
implements DialogAction.ValueGetter {
    final /* synthetic */ InputControlHandlers.NumberRangeInputControlHandler.RangeSliderWidget field_61020;

    InputControlHandlers.NumberRangeInputControlHandler.1() {
        this.field_61020 = rangeSliderWidget;
    }

    @Override
    public String get() {
        return this.field_61020.getLabel();
    }

    @Override
    public NbtElement getAsNbt() {
        return NbtFloat.of(this.field_61020.getActualValue());
    }
}
