package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class AnvilScreen extends ForgingScreen {
   private static final Identifier TEXT_FIELD_TEXTURE = Identifier.ofVanilla("container/anvil/text_field");
   private static final Identifier TEXT_FIELD_DISABLED_TEXTURE = Identifier.ofVanilla("container/anvil/text_field_disabled");
   private static final Identifier ERROR_TEXTURE = Identifier.ofVanilla("container/anvil/error");
   private static final Identifier TEXTURE = Identifier.ofVanilla("textures/gui/container/anvil.png");
   private static final Text TOO_EXPENSIVE_TEXT = Text.translatable("container.repair.expensive");
   private TextFieldWidget nameField;
   private final PlayerEntity player;

   public AnvilScreen(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
      super(handler, inventory, title, TEXTURE);
      this.player = inventory.player;
      this.titleX = 60;
   }

   protected void setup() {
      int i = (this.width - this.backgroundWidth) / 2;
      int j = (this.height - this.backgroundHeight) / 2;
      this.nameField = new TextFieldWidget(this.textRenderer, i + 62, j + 24, 103, 12, Text.translatable("container.repair"));
      this.nameField.setFocusUnlocked(false);
      this.nameField.setEditableColor(-1);
      this.nameField.setUneditableColor(-1);
      this.nameField.setDrawsBackground(false);
      this.nameField.setMaxLength(50);
      this.nameField.setChangedListener(this::onRenamed);
      this.nameField.setText("");
      this.addDrawableChild(this.nameField);
      this.nameField.setEditable(((AnvilScreenHandler)this.handler).getSlot(0).hasStack());
   }

   protected void handledScreenTick() {
      super.handledScreenTick();
      this.client.player.experienceBarDisplayStartTime = this.client.player.age;
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.nameField);
   }

   public void resize(MinecraftClient client, int width, int height) {
      String string = this.nameField.getText();
      this.init(client, width, height);
      this.nameField.setText(string);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         this.client.player.closeHandledScreen();
      }

      return !this.nameField.keyPressed(keyCode, scanCode, modifiers) && !this.nameField.isActive() ? super.keyPressed(keyCode, scanCode, modifiers) : true;
   }

   private void onRenamed(String name) {
      Slot slot = ((AnvilScreenHandler)this.handler).getSlot(0);
      if (slot.hasStack()) {
         String string = name;
         if (!slot.getStack().contains(DataComponentTypes.CUSTOM_NAME) && name.equals(slot.getStack().getName().getString())) {
            string = "";
         }

         if (((AnvilScreenHandler)this.handler).setNewItemName(string)) {
            this.client.player.networkHandler.sendPacket(new RenameItemC2SPacket(string));
         }

      }
   }

   protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
      super.drawForeground(context, mouseX, mouseY);
      int i = ((AnvilScreenHandler)this.handler).getLevelCost();
      if (i > 0) {
         int j = -8323296;
         Object text;
         if (i >= 40 && !this.client.player.isInCreativeMode()) {
            text = TOO_EXPENSIVE_TEXT;
            j = -40864;
         } else if (!((AnvilScreenHandler)this.handler).getSlot(2).hasStack()) {
            text = null;
         } else {
            text = Text.translatable("container.repair.cost", i);
            if (!((AnvilScreenHandler)this.handler).getSlot(2).canTakeItems(this.player)) {
               j = -40864;
            }
         }

         if (text != null) {
            int k = this.backgroundWidth - 8 - this.textRenderer.getWidth((StringVisitable)text) - 2;
            int l = true;
            context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
            context.drawTextWithShadow(this.textRenderer, (Text)text, k, 69, j);
         }
      }

   }

   protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
      super.drawBackground(context, deltaTicks, mouseX, mouseY);
      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ((AnvilScreenHandler)this.handler).getSlot(0).hasStack() ? TEXT_FIELD_TEXTURE : TEXT_FIELD_DISABLED_TEXTURE, this.x + 59, this.y + 20, 110, 16);
   }

   protected void drawInvalidRecipeArrow(DrawContext context, int x, int y) {
      if ((((AnvilScreenHandler)this.handler).getSlot(0).hasStack() || ((AnvilScreenHandler)this.handler).getSlot(1).hasStack()) && !((AnvilScreenHandler)this.handler).getSlot(((AnvilScreenHandler)this.handler).getResultSlotIndex()).hasStack()) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, ERROR_TEXTURE, x + 99, y + 45, 28, 21);
      }

   }

   public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
      if (slotId == 0) {
         this.nameField.setText(stack.isEmpty() ? "" : stack.getName().getString());
         this.nameField.setEditable(!stack.isEmpty());
         this.setFocused(this.nameField);
      }

   }
}
