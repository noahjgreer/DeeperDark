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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.BooleanInputControl;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtElement;

@Environment(value=EnvType.CLIENT)
static class InputControlHandlers.BooleanInputControlHandler
implements InputControlHandler<BooleanInputControl> {
    InputControlHandlers.BooleanInputControlHandler() {
    }

    @Override
    public void addControl(final BooleanInputControl booleanInputControl, Screen screen, InputControlHandler.Output output) {
        TextRenderer textRenderer = screen.getTextRenderer();
        final CheckboxWidget checkboxWidget = CheckboxWidget.builder(booleanInputControl.label(), textRenderer).checked(booleanInputControl.initial()).build();
        output.accept(checkboxWidget, new DialogAction.ValueGetter(){

            @Override
            public String get() {
                return checkboxWidget.isChecked() ? booleanInputControl.onTrue() : booleanInputControl.onFalse();
            }

            @Override
            public NbtElement getAsNbt() {
                return NbtByte.of(checkboxWidget.isChecked());
            }
        });
    }

    @Override
    public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
        this.addControl((BooleanInputControl)inputControl, screen, output);
    }
}
