/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ButtonTextures
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.dialog.DialogBodyHandlers
 *  net.minecraft.client.gui.screen.dialog.DialogControls
 *  net.minecraft.client.gui.screen.dialog.DialogNetworkAccess
 *  net.minecraft.client.gui.screen.dialog.DialogScreen
 *  net.minecraft.client.gui.screen.dialog.DialogScreen$1
 *  net.minecraft.client.gui.screen.dialog.DialogScreen$WarningScreen
 *  net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen
 *  net.minecraft.client.gui.tooltip.Tooltip
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.DirectionalLayoutWidget
 *  net.minecraft.client.gui.widget.GridWidget
 *  net.minecraft.client.gui.widget.LayoutWidget
 *  net.minecraft.client.gui.widget.ScrollableLayoutWidget
 *  net.minecraft.client.gui.widget.TextWidget
 *  net.minecraft.client.gui.widget.TexturedButtonWidget
 *  net.minecraft.client.gui.widget.ThreePartsLayoutWidget
 *  net.minecraft.client.gui.widget.Widget
 *  net.minecraft.dialog.AfterAction
 *  net.minecraft.dialog.body.DialogBody
 *  net.minecraft.dialog.type.Dialog
 *  net.minecraft.dialog.type.DialogInput
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.text.ClickEvent
 *  net.minecraft.text.ClickEvent$Custom
 *  net.minecraft.text.ClickEvent$RunCommand
 *  net.minecraft.text.ClickEvent$ShowDialog
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.dialog;

import java.lang.runtime.SwitchBootstraps;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogBodyHandlers;
import net.minecraft.client.gui.screen.dialog.DialogControls;
import net.minecraft.client.gui.screen.dialog.DialogNetworkAccess;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.dialog.WaitingForResponseScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.client.gui.widget.ScrollableLayoutWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.dialog.AfterAction;
import net.minecraft.dialog.body.DialogBody;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.DialogInput;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class DialogScreen<T extends Dialog>
extends Screen {
    public static final Text CUSTOM_SCREEN_REJECTED_DISCONNECT_TEXT = Text.translatable((String)"menu.custom_screen_info.disconnect");
    private static final int field_60758 = 20;
    private static final ButtonTextures WARNING_BUTTON_TEXTURES = new ButtonTextures(Identifier.ofVanilla((String)"dialog/warning_button"), Identifier.ofVanilla((String)"dialog/warning_button_disabled"), Identifier.ofVanilla((String)"dialog/warning_button_highlighted"));
    private final T dialog;
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget((Screen)this);
    private final @Nullable Screen parent;
    private @Nullable ScrollableLayoutWidget contents;
    private ButtonWidget warningButton;
    private final DialogNetworkAccess networkAccess;
    private Supplier<Optional<ClickEvent>> cancelAction = DialogControls.EMPTY_ACTION_CLICK_EVENT;

    public DialogScreen(@Nullable Screen parent, T dialog, DialogNetworkAccess networkAccess) {
        super(dialog.common().title());
        this.dialog = dialog;
        this.parent = parent;
        this.networkAccess = networkAccess;
    }

    protected final void init() {
        super.init();
        this.warningButton = this.createWarningButton();
        this.warningButton.setNavigationOrder(-10);
        DialogControls dialogControls = new DialogControls(this);
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.vertical().spacing(10);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        this.layout.addHeader(this.createHeader());
        for (DialogBody dialogBody : this.dialog.common().body()) {
            Widget widget = DialogBodyHandlers.createWidget((DialogScreen)this, (DialogBody)dialogBody);
            if (widget == null) continue;
            directionalLayoutWidget.add(widget);
        }
        for (DialogInput dialogInput : this.dialog.common().inputs()) {
            dialogControls.addInput(dialogInput, arg_0 -> ((DirectionalLayoutWidget)directionalLayoutWidget).add(arg_0));
        }
        this.initBody(directionalLayoutWidget, dialogControls, this.dialog, this.networkAccess);
        this.contents = new ScrollableLayoutWidget(this.client, (LayoutWidget)directionalLayoutWidget, this.layout.getContentHeight());
        this.layout.addBody((Widget)this.contents);
        this.initHeaderAndFooter(this.layout, dialogControls, this.dialog, this.networkAccess);
        this.cancelAction = dialogControls.createClickEvent(this.dialog.getCancelAction());
        this.layout.forEachChild(child -> {
            if (child != this.warningButton) {
                this.addDrawableChild((Element)child);
            }
        });
        this.addDrawableChild((Element)this.warningButton);
        this.refreshWidgetPositions();
    }

    protected void initBody(DirectionalLayoutWidget bodyLayout, DialogControls controls, T dialog, DialogNetworkAccess networkAccess) {
    }

    protected void initHeaderAndFooter(ThreePartsLayoutWidget layout, DialogControls controls, T dialog, DialogNetworkAccess networkAccess) {
    }

    protected void refreshWidgetPositions() {
        this.contents.setHeight(this.layout.getContentHeight());
        this.layout.refreshPositions();
        this.resetWarningButtonPosition();
    }

    protected Widget createHeader() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(10);
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter().alignVerticalCenter();
        directionalLayoutWidget.add((Widget)new TextWidget(this.title, this.textRenderer));
        directionalLayoutWidget.add((Widget)this.warningButton);
        return directionalLayoutWidget;
    }

    protected void resetWarningButtonPosition() {
        int i = this.warningButton.getX();
        int j = this.warningButton.getY();
        if (i < 0 || j < 0 || i > this.width - 20 || j > this.height - 20) {
            this.warningButton.setX(Math.max(0, this.width - 40));
            this.warningButton.setY(Math.min(5, this.height));
        }
    }

    private ButtonWidget createWarningButton() {
        TexturedButtonWidget texturedButtonWidget = new TexturedButtonWidget(0, 0, 20, 20, WARNING_BUTTON_TEXTURES, button -> this.client.setScreen(WarningScreen.create((MinecraftClient)this.client, (DialogNetworkAccess)this.networkAccess, (Screen)this)), (Text)Text.translatable((String)"menu.custom_screen_info.button_narration"));
        texturedButtonWidget.setTooltip(Tooltip.of((Text)Text.translatable((String)"menu.custom_screen_info.tooltip")));
        return texturedButtonWidget;
    }

    public boolean shouldPause() {
        return this.dialog.common().pause();
    }

    public boolean shouldCloseOnEsc() {
        return this.dialog.common().canCloseWithEscape();
    }

    public void close() {
        this.runAction((Optional)this.cancelAction.get(), AfterAction.CLOSE);
    }

    public void runAction(Optional<ClickEvent> clickEvent) {
        this.runAction(clickEvent, this.dialog.common().afterAction());
    }

    public void runAction(Optional<ClickEvent> clickEvent, AfterAction afterAction) {
        DialogScreen screen;
        switch (1.field_61009[afterAction.ordinal()]) {
            default: {
                throw new MatchException(null, null);
            }
            case 1: {
                DialogScreen dialogScreen = this;
                break;
            }
            case 2: {
                DialogScreen dialogScreen = this.parent;
                break;
            }
            case 3: {
                DialogScreen dialogScreen = screen = new WaitingForResponseScreen(this.parent);
            }
        }
        if (clickEvent.isPresent()) {
            this.handleClickEvent(clickEvent.get(), (Screen)screen);
        } else {
            this.client.setScreen((Screen)screen);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void handleClickEvent(ClickEvent clickEvent, @Nullable Screen afterActionScreen) {
        ClickEvent clickEvent2 = clickEvent;
        Objects.requireNonNull(clickEvent2);
        ClickEvent clickEvent3 = clickEvent2;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ClickEvent.RunCommand.class, ClickEvent.ShowDialog.class, ClickEvent.Custom.class}, (Object)clickEvent3, n)) {
            case 0: {
                String string2;
                ClickEvent.RunCommand runCommand = (ClickEvent.RunCommand)clickEvent3;
                try {
                    String string;
                    string2 = string = runCommand.command();
                }
                catch (Throwable throwable) {
                    throw new MatchException(throwable.toString(), throwable);
                }
                this.networkAccess.runClickEventCommand(CommandManager.stripLeadingSlash((String)string2), afterActionScreen);
                return;
            }
            case 1: {
                ClickEvent.ShowDialog showDialog = (ClickEvent.ShowDialog)clickEvent3;
                this.networkAccess.showDialog(showDialog.dialog(), afterActionScreen);
                return;
            }
            case 2: {
                ClickEvent.Custom custom = (ClickEvent.Custom)clickEvent3;
                this.networkAccess.sendCustomClickActionPacket(custom.id(), custom.payload());
                this.client.setScreen(afterActionScreen);
                return;
            }
        }
        DialogScreen.handleBasicClickEvent((ClickEvent)clickEvent, (MinecraftClient)this.client, (Screen)afterActionScreen);
    }

    public @Nullable Screen getParentScreen() {
        return this.parent;
    }

    protected static Widget createGridWidget(List<? extends Widget> widgets, int columns) {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().alignHorizontalCenter();
        gridWidget.setColumnSpacing(2).setRowSpacing(2);
        int i = widgets.size();
        int j = i / columns;
        int k = j * columns;
        for (int l = 0; l < k; ++l) {
            gridWidget.add(widgets.get(l), l / columns, l % columns);
        }
        if (i != k) {
            DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(2);
            directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
            for (int m = k; m < i; ++m) {
                directionalLayoutWidget.add(widgets.get(m));
            }
            gridWidget.add((Widget)directionalLayoutWidget, j, 0, 1, columns);
        }
        return gridWidget;
    }
}

