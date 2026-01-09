package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
enum AdvancementTabType {
   ABOVE(new Textures(Identifier.ofVanilla("advancements/tab_above_left_selected"), Identifier.ofVanilla("advancements/tab_above_middle_selected"), Identifier.ofVanilla("advancements/tab_above_right_selected")), new Textures(Identifier.ofVanilla("advancements/tab_above_left"), Identifier.ofVanilla("advancements/tab_above_middle"), Identifier.ofVanilla("advancements/tab_above_right")), 28, 32, 8),
   BELOW(new Textures(Identifier.ofVanilla("advancements/tab_below_left_selected"), Identifier.ofVanilla("advancements/tab_below_middle_selected"), Identifier.ofVanilla("advancements/tab_below_right_selected")), new Textures(Identifier.ofVanilla("advancements/tab_below_left"), Identifier.ofVanilla("advancements/tab_below_middle"), Identifier.ofVanilla("advancements/tab_below_right")), 28, 32, 8),
   LEFT(new Textures(Identifier.ofVanilla("advancements/tab_left_top_selected"), Identifier.ofVanilla("advancements/tab_left_middle_selected"), Identifier.ofVanilla("advancements/tab_left_bottom_selected")), new Textures(Identifier.ofVanilla("advancements/tab_left_top"), Identifier.ofVanilla("advancements/tab_left_middle"), Identifier.ofVanilla("advancements/tab_left_bottom")), 32, 28, 5),
   RIGHT(new Textures(Identifier.ofVanilla("advancements/tab_right_top_selected"), Identifier.ofVanilla("advancements/tab_right_middle_selected"), Identifier.ofVanilla("advancements/tab_right_bottom_selected")), new Textures(Identifier.ofVanilla("advancements/tab_right_top"), Identifier.ofVanilla("advancements/tab_right_middle"), Identifier.ofVanilla("advancements/tab_right_bottom")), 32, 28, 5);

   private final Textures selectedTextures;
   private final Textures unselectedTextures;
   private final int width;
   private final int height;
   private final int tabCount;

   private AdvancementTabType(final Textures selectedTextures, final Textures unselectedTextures, final int width, final int height, final int tabCount) {
      this.selectedTextures = selectedTextures;
      this.unselectedTextures = unselectedTextures;
      this.width = width;
      this.height = height;
      this.tabCount = tabCount;
   }

   public int getTabCount() {
      return this.tabCount;
   }

   public void drawBackground(DrawContext context, int x, int y, boolean selected, int index) {
      Textures textures = selected ? this.selectedTextures : this.unselectedTextures;
      Identifier identifier;
      if (index == 0) {
         identifier = textures.first();
      } else if (index == this.tabCount - 1) {
         identifier = textures.last();
      } else {
         identifier = textures.middle();
      }

      context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, x + this.getTabX(index), y + this.getTabY(index), this.width, this.height);
   }

   public void drawIcon(DrawContext context, int x, int y, int index, ItemStack stack) {
      int i = x + this.getTabX(index);
      int j = y + this.getTabY(index);
      switch (this.ordinal()) {
         case 0:
            i += 6;
            j += 9;
            break;
         case 1:
            i += 6;
            j += 6;
            break;
         case 2:
            i += 10;
            j += 5;
            break;
         case 3:
            i += 6;
            j += 5;
      }

      context.drawItemWithoutEntity(stack, i, j);
   }

   public int getTabX(int index) {
      switch (this.ordinal()) {
         case 0:
            return (this.width + 4) * index;
         case 1:
            return (this.width + 4) * index;
         case 2:
            return -this.width + 4;
         case 3:
            return 248;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf(this));
      }
   }

   public int getTabY(int index) {
      switch (this.ordinal()) {
         case 0:
            return -this.height + 4;
         case 1:
            return 136;
         case 2:
            return this.height * index;
         case 3:
            return this.height * index;
         default:
            throw new UnsupportedOperationException("Don't know what this tab type is!" + String.valueOf(this));
      }
   }

   public boolean isClickOnTab(int screenX, int screenY, int index, double mouseX, double mouseY) {
      int i = screenX + this.getTabX(index);
      int j = screenY + this.getTabY(index);
      return mouseX > (double)i && mouseX < (double)(i + this.width) && mouseY > (double)j && mouseY < (double)(j + this.height);
   }

   // $FF: synthetic method
   private static AdvancementTabType[] method_36883() {
      return new AdvancementTabType[]{ABOVE, BELOW, LEFT, RIGHT};
   }

   @Environment(EnvType.CLIENT)
   private static record Textures(Identifier first, Identifier middle, Identifier last) {
      Textures(Identifier identifier, Identifier identifier2, Identifier identifier3) {
         this.first = identifier;
         this.middle = identifier2;
         this.last = identifier3;
      }

      public Identifier first() {
         return this.first;
      }

      public Identifier middle() {
         return this.middle;
      }

      public Identifier last() {
         return this.last;
      }
   }
}
