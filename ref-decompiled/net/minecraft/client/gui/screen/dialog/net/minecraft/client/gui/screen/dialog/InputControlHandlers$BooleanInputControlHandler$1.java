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
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

@Environment(value=EnvType.CLIENT)
class InputControlHandlers.BooleanInputControlHandler.1
implements DialogAction.ValueGetter {
    final /* synthetic */ CheckboxWidget field_61018;
    final /* synthetic */ BooleanInputControl field_61019;

    InputControlHandlers.BooleanInputControlHandler.1() {
        this.field_61018 = checkboxWidget;
        this.field_61019 = booleanInputControl;
    }

    @Override
    public String get() {
        return this.field_61018.isChecked() ? this.field_61019.onTrue() : this.field_61019.onFalse();
    }

    @Override
    public NbtElement getAsNbt() {
        return NbtByte.of(this.field_61018.isChecked());
    }
}
