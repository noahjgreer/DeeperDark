package net.minecraft.client.gui.screen.ingame;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.CommandBlockExecutor;

@Environment(EnvType.CLIENT)
public abstract class AbstractCommandBlockScreen extends Screen {
   private static final Text SET_COMMAND_TEXT = Text.translatable("advMode.setCommand");
   private static final Text COMMAND_TEXT = Text.translatable("advMode.command");
   private static final Text PREVIOUS_OUTPUT_TEXT = Text.translatable("advMode.previousOutput");
   protected TextFieldWidget consoleCommandTextField;
   protected TextFieldWidget previousOutputTextField;
   protected ButtonWidget doneButton;
   protected ButtonWidget cancelButton;
   protected CyclingButtonWidget toggleTrackingOutputButton;
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
      this.doneButton = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.commitAndClose();
      }).dimensions(this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20).build());
      this.cancelButton = (ButtonWidget)this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.close();
      }).dimensions(this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20).build());
      boolean bl = this.getCommandExecutor().isTrackingOutput();
      this.toggleTrackingOutputButton = (CyclingButtonWidget)this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.literal("O"), Text.literal("X")).initially(bl).omitKeyText().build(this.width / 2 + 150 - 20, this.getTrackOutputButtonHeight(), 20, 20, Text.translatable("advMode.trackOutput"), (button, trackOutput) -> {
         CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
         commandBlockExecutor.setTrackOutput(trackOutput);
         this.setPreviousOutputText(trackOutput);
      }));
      this.consoleCommandTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, 50, 300, 20, Text.translatable("advMode.command")) {
         protected MutableText getNarrationMessage() {
            return super.getNarrationMessage().append(AbstractCommandBlockScreen.this.commandSuggestor.getNarration());
         }
      };
      this.consoleCommandTextField.setMaxLength(32500);
      this.consoleCommandTextField.setChangedListener(this::onCommandChanged);
      this.addSelectableChild(this.consoleCommandTextField);
      this.previousOutputTextField = new TextFieldWidget(this.textRenderer, this.width / 2 - 150, this.getTrackOutputButtonHeight(), 276, 20, Text.translatable("advMode.previousOutput"));
      this.previousOutputTextField.setMaxLength(32500);
      this.previousOutputTextField.setEditable(false);
      this.previousOutputTextField.setText("-");
      this.addSelectableChild(this.previousOutputTextField);
      this.commandSuggestor = new ChatInputSuggestor(this.client, this, this.consoleCommandTextField, this.textRenderer, true, true, 0, 7, false, Integer.MIN_VALUE);
      this.commandSuggestor.setWindowActive(true);
      this.commandSuggestor.refresh();
      this.setPreviousOutputText(bl);
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.consoleCommandTextField);
   }

   protected Text getUsageNarrationText() {
      return this.commandSuggestor.isOpen() ? this.commandSuggestor.getSuggestionUsageNarrationText() : super.getUsageNarrationText();
   }

   public void resize(MinecraftClient client, int width, int height) {
      String string = this.consoleCommandTextField.getText();
      this.init(client, width, height);
      this.consoleCommandTextField.setText(string);
      this.commandSuggestor.refresh();
   }

   protected void setPreviousOutputText(boolean trackOutput) {
      this.previousOutputTextField.setText(trackOutput ? this.getCommandExecutor().getLastOutput().getString() : "-");
   }

   protected void commitAndClose() {
      CommandBlockExecutor commandBlockExecutor = this.getCommandExecutor();
      this.syncSettingsToServer(commandBlockExecutor);
      if (!commandBlockExecutor.isTrackingOutput()) {
         commandBlockExecutor.setLastOutput((Text)null);
      }

      this.client.setScreen((Screen)null);
   }

   protected abstract void syncSettingsToServer(CommandBlockExecutor commandExecutor);

   private void onCommandChanged(String text) {
      this.commandSuggestor.refresh();
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.commandSuggestor.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (super.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else if (keyCode != 257 && keyCode != 335) {
         return false;
      } else {
         this.commitAndClose();
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      return this.commandSuggestor.mouseScrolled(verticalAmount) ? true : super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.commandSuggestor.mouseClicked(mouseX, mouseY, button) ? true : super.mouseClicked(mouseX, mouseY, button);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      context.drawCenteredTextWithShadow(this.textRenderer, (Text)SET_COMMAND_TEXT, this.width / 2, 20, -1);
      context.drawTextWithShadow(this.textRenderer, (Text)COMMAND_TEXT, this.width / 2 - 150 + 1, 40, -6250336);
      this.consoleCommandTextField.render(context, mouseX, mouseY, deltaTicks);
      int i = 75;
      if (!this.previousOutputTextField.getText().isEmpty()) {
         Objects.requireNonNull(this.textRenderer);
         i += 5 * 9 + 1 + this.getTrackOutputButtonHeight() - 135;
         context.drawTextWithShadow(this.textRenderer, PREVIOUS_OUTPUT_TEXT, this.width / 2 - 150 + 1, i + 4, -6250336);
         this.previousOutputTextField.render(context, mouseX, mouseY, deltaTicks);
      }

      this.commandSuggestor.render(context, mouseX, mouseY);
   }

   public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      this.renderInGameBackground(context);
   }
}
