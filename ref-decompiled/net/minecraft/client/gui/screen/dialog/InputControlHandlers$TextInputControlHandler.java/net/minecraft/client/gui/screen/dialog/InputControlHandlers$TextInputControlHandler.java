/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.InputControlHandler;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.LayoutWidgets;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.input.TextInputControl;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.ScreenTexts;

@Environment(value=EnvType.CLIENT)
static class InputControlHandlers.TextInputControlHandler
implements InputControlHandler<TextInputControl> {
    InputControlHandlers.TextInputControlHandler() {
    }

    @Override
    public void addControl(TextInputControl textInputControl, Screen screen, InputControlHandler.Output output) {
        Supplier<String> supplier;
        ClickableWidget widget;
        TextRenderer textRenderer = screen.getTextRenderer();
        if (textInputControl.multiline().isPresent()) {
            TextInputControl.Multiline multiline = textInputControl.multiline().get();
            int i = multiline.height().orElseGet(() -> {
                int i = multiline.maxLines().orElse(4);
                return Math.min(textRenderer.fontHeight * i + 8, 512);
            });
            EditBoxWidget editBoxWidget = EditBoxWidget.builder().build(textRenderer, textInputControl.width(), i, ScreenTexts.EMPTY);
            editBoxWidget.setMaxLength(textInputControl.maxLength());
            multiline.maxLines().ifPresent(editBoxWidget::setMaxLines);
            editBoxWidget.setText(textInputControl.initial());
            widget = editBoxWidget;
            supplier = editBoxWidget::getText;
        } else {
            TextFieldWidget textFieldWidget = new TextFieldWidget(textRenderer, textInputControl.width(), 20, textInputControl.label());
            textFieldWidget.setMaxLength(textInputControl.maxLength());
            textFieldWidget.setText(textInputControl.initial());
            widget = textFieldWidget;
            supplier = textFieldWidget::getText;
        }
        TextFieldWidget widget2 = textInputControl.labelVisible() ? LayoutWidgets.createLabeledWidget(textRenderer, widget, textInputControl.label()) : widget;
        output.accept(widget2, new DialogAction.ValueGetter(){

            @Override
            public String get() {
                return NbtString.escapeUnquoted((String)supplier.get());
            }

            @Override
            public NbtElement getAsNbt() {
                return NbtString.of((String)supplier.get());
            }
        });
    }

    @Override
    public /* synthetic */ void addControl(InputControl inputControl, Screen screen, InputControlHandler.Output output) {
        this.addControl((TextInputControl)inputControl, screen, output);
    }
}
