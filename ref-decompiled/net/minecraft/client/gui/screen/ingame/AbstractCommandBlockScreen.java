/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.ChatInputSuggestor
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.CyclingButtonWidget
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.input.KeyInput
 *  net.minecraft.client.util.NarratorManager
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.world.CommandBlockExecutor
 */
package net.minecraft.client.gui.screen.ingame;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.world.CommandBlockExecutor;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen
extends Screen {
    private static final Text SET_COMMAND_TEXT = Text.translatable((String)"advMode.setCommand");
    private static final Text COMMAND_TEXT = Text.translatable((String)"advMode.command");
    private static final Text PREVIOUS_OUTPUT_TEXT = Text.translatable((String)"advMode.previousOutput");
    protected TextFieldWidget consoleCommandTextField;
    protected TextFieldWidget previousOutputTextField;
    protected ButtonWidget doneButton;
    protected ButtonWidget cancelButton;
    protected CyclingButtonWidget<Boolean> toggleTrackingOutputButton;
    ChatInputSuggestor commandSuggestor;

    public AbstractCommandBlockScreen() {
        super(NarratorManager.EMPTY);
    }

    public void tick() {
        if (!this.getCommandExecutor().isEditable()) {
            this.close();
        }
    }

    abstract CommandBlockExecutor getCommandExecutor();

    abstract int getTrackOutputButtonHeight();

    protected void init() {
        boolean bl = this.getCommandExecutor().isTrackingOutput();
        this.consoleCommandTextField = new /* Unavailable Anonymous Inner Class!! */;
        this.consoleCommandTextField.setMaxLength(32500);
        this.consoleCommandTextField.setChangedListener(arg_0 -> this.onCommandChanged(arg_0));
        this.addSelectableChild((Element)this.consoleCommandTextField);
        this.previousOutputTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, (Text)Text.translatable((String)"advMode.previousOutput"));
        this.previousOutputTextField.setMaxLength(32500);
        this.previousOutputTextField.setEditable(false);
        this.previousOutputTextField.setText("-");
        this.addSelectableChild((Element)this.previousOutputTextField);
        this.toggleTrackingOutputButton = (CyclingButtonWidget)this.addDrawableChild((Element)CyclingButtonWidget.onOffBuilder((Text)Text.literal((String)"O"), (Text)Text.literal((String)"X"), (boolean)bl).omitKeyText().build(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, (Text)Text.translatable((String)"advMode.trackOutput"), (button, trackOutput) -> {
            CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
            commandBlockExecutor.setTrackOutput(trackOutput.booleanValue());
            this.setPreviousOutputText(trackOutput.booleanValue());
        }));
        this.addAdditionalButtons();
        this.doneButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.DONE, button -> this.commitAndClose()).dimensions(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20).build());
        this.cancelButton = (ButtonWidget)this.addDrawableChild((Element)ButtonWidget.builder((Text)ScreenTexts.CANCEL, button -> this.close()).dimensions(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20).build());
        this.commandSuggestor = new ChatInputSuggestor(this.client, (Screen)this, this.consoleCommandTextField, this.textRenderer, true, true, 0, 7, false, Integer.MIN_VALUE);
        this.commandSuggestor.setWindowActive(true);
        this.commandSuggestor.refresh();
        this.setPreviousOutputText(bl);
    }

    protected void addAdditionalButtons() {
    }

    protected void setInitialFocus() {
        this.setInitialFocus((Element)this.consoleCommandTextField);
    }

    protected Text getUsageNarrationText() {
        if (this.commandSuggestor.isOpen()) {
            return this.commandSuggestor.getSuggestionUsageNarrationText();
        }
        return super.getUsageNarrationText();
    }

    public void resize(int width, int height) {
        String string = this.consoleCommandTextField.getText();
        this.init(width, height);
        this.consoleCommandTextField.setText(string);
        this.commandSuggestor.refresh();
    }

    protected void setPreviousOutputText(boolean trackOutput) {
        this.previousOutputTextField.setText(trackOutput ? this.getCommandExecutor().getLastOutput().getString() : "-");
    }

    protected void commitAndClose() {
        this.syncSettingsToServer();
        CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
        if (!commandBlockExecutor.isTrackingOutput()) {
            commandBlockExecutor.setLastOutput(null);
        }
        this.client.setScreen(null);
    }

    protected abstract void syncSettingsToServer();

    private void onCommandChanged(String text) {
        this.commandSuggestor.refresh();
    }

    public boolean deferSubtitles() {
        return true;
    }

    public boolean keyPressed(KeyInput input) {
        if (this.commandSuggestor.keyPressed(input)) {
            return true;
        }
        if (super.keyPressed(input)) {
            return true;
        }
        if (input.isEnter()) {
            this.commitAndClose();
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.commandSuggestor.mouseScrolled(verticalAmount)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    public boolean mouseClicked(Click click, boolean doubled) {
        if (this.commandSuggestor.mouseClicked(click)) {
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        context.drawCenteredTextWithShadow(this.textRenderer, SET_COMMAND_TEXT, this.width / 2, 20, -1);
        context.drawTextWithShadow(this.textRenderer, COMMAND_TEXT, this.width / 2 - 150 + 1, 40, -6250336);
        this.consoleCommandTextField.render(context, mouseX, mouseY, deltaTicks);
        int i = 75;
        if (!this.previousOutputTextField.getText().isEmpty()) {
            Objects.requireNonNull(this.textRenderer);
            context.drawTextWithShadow(this.textRenderer, PREVIOUS_OUTPUT_TEXT, this.width / 2 - 150 + 1, (i += 5 * 9 + 1 + this.getTrackOutputButtonHeight() - 135) + 4, -6250336);
            this.previousOutputTextField.render(context, mouseX, mouseY, deltaTicks);
        }
        this.commandSuggestor.render(context, mouseX, mouseY);
    }
}

