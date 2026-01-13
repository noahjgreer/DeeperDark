/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.SingleOptionInputControl;

@Environment(value=EnvType.CLIENT)
static class InputControlHandlers.SimpleOptionInputControlHandler
implements InputControlHandler<SingleOptionInputControl> {
    InputControlHandlers.SimpleOptionInputControlHandler() {
    }

    @Override
    public void addControl(SingleOptionInputControl singleOptionInputControl, Screen screen, InputControlHandler.Output output) {
        SingleOptionInputControl.Entry entry = singleOptionInputControl.getInitialEntry().orElse(singleOptionInputControl.entries().getFirst());
        CyclingButtonWidget.Builder<SingleOptionInputControl.Entry> builder = CyclingButtonWidget.builder(SingleOptionInputControl.Entry::getDisplay, entry).values((Collection<SingleOptionInputControl.Entry>)singleOptionInputControl.entries()).labelType(!singleOptionInputControl.labelVisible() ? CyclingButtonWidget.LabelType.VALUE : CyclingButtonWidget.LabelType.NAME_AND_VALUE);
        CyclingButtonWidget<SingleOptionInputControl.Entry> cyclingButtonWidget = builder.build(0, 0, singleOptionInputControl.width(), 20, singleOptionInputControl.label());
        output.accept(cyclingButtonWidget, DialogAction.ValueGetter.of(() -> ((SingleOptionInputControl.Entry)cyclingButtonWidget.getValue()).id()));
    }

    @Override
    public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
        this.addControl((SingleOptionInputControl)inputControl, screen, output);
    }
}
