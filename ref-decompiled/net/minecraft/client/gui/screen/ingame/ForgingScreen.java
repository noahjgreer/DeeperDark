package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class ForgingScreen extends HandledScreen implements ScreenHandlerListener {
   private final Identifier texture;

   public ForgingScreen(ForgingScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
      super(handler, playerInventory, title);
      this.texture = texture;
   }

   protected void setup() {
   }

   protected void init() {
      super.init();
      this.setup();
      ((ForgingScreenHandler)this.handler).addListener(this);
   }

   public void removed() {
      super.removed();
      ((ForgingScreenHandler)this.handler).removeListener(this);
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      super.render(context, mouseX, mouseY, deltaTicks);
      this.drawMouseoverTooltip(context, mouseX, mouseY);
   }

   protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
      context.drawTexture(RenderPipelines.GUI_TEXTURED, this.texture, this.x, this.y, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, 256, 256);
      this.drawInvalidRecipeArrow(context, this.x, this.y);
   }

   protected abstract void drawInvalidRecipeArrow(DrawContext context, int x, int y);

   public void onPropertyUpdate(ScreenHandler handler, int property, int value) {
   }

   public void onSlotUpdate(ScreenHandler handler, int slotId, ItemStack stack) {
   }
}
