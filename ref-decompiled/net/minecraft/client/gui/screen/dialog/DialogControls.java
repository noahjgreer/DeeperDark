/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.DialogControls
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.screen.dialog.InputControlHandlers
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$Builder
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.dialog.DialogActionButtonData
 *  net.minecraft.dialog.DialogButtonData
 *  net.minecraft.dialog.action.DialogAction
 *  net.minecraft.dialog.action.DialogAction$ValueGetter
 *  net.minecraft.dialog.input.InputControl
 *  net.minecraft.dialog.type.DialogInput
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.Text
 */
package net.minecraft.client.gui.screen.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.InputControlHandlers;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.DialogActionButtonData;
import net.minecraft.dialog.DialogButtonData;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.input.InputControl;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class DialogControls {
    public static final Supplier<Optional<ClickEvent>> EMPTY_ACTION_CLICK_EVENT = Optional::empty;
    private final DialogScreen<?> screen;
    private final Map<String, DialogAction.ValueGetter> valueGetters = new HashMap();

    public DialogControls(DialogScreen<?> screen) {
        this.screen = screen;
    }

    public void addInput(DialogInput input, Consumer<Widget> widgetConsumer) {
        String string = input.key();
        InputControlHandlers.addControl((InputControl)input.control(), (Screen)this.screen, (widget, valueGetter) -> {
            this.valueGetters.put(string, valueGetter);
            widgetConsumer.accept(widget);
        });
    }

    private static ButtonWidget.Builder createButton(DialogButtonData data, ButtonWidget.PressAction pressAction) {
        ButtonWidget.Builder builder = ButtonWidget.builder((Text)data.label(), (ButtonWidget.PressAction)pressAction);
        builder.width(data.width());
        if (data.tooltip().isPresent()) {
            builder = builder.tooltip(Tooltip.of((Text)((Text)data.tooltip().get())));
        }
        return builder;
    }

    public Supplier<Optional<ClickEvent>> createClickEvent(Optional<DialogAction> action) {
        if (action.isPresent()) {
            DialogAction dialogAction = action.get();
            return () -> dialogAction.createClickEvent(this.valueGetters);
        }
        return EMPTY_ACTION_CLICK_EVENT;
    }

    public ButtonWidget.Builder createButton(DialogActionButtonData actionButtonData) {
        Supplier supplier = this.createClickEvent(actionButtonData.action());
        return DialogControls.createButton((DialogButtonData)actionButtonData.data(), arg_0 -> this.method_72140((Supplier)supplier, arg_0));
    }

    private /* synthetic */ void method_72140(Supplier supplier, ButtonWidget button) {
        this.screen.runAction((Optional)supplier.get());
    }
}

