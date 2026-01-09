package net.minecraft.client.gui.screen.recipebook;

import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.book.RecipeBookGroup;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class RecipeGroupButtonWidget extends ToggleButtonWidget {
   private static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.ofVanilla("recipe_book/tab"), Identifier.ofVanilla("recipe_book/tab_selected"));
   private final RecipeBookWidget.Tab tab;
   private static final float field_32412 = 15.0F;
   private float bounce;

   public RecipeGroupButtonWidget(RecipeBookWidget.Tab tab) {
      super(0, 0, 35, 27, false);
      this.tab = tab;
      this.setTextures(TEXTURES);
   }

   public void checkForNewRecipes(ClientRecipeBook recipeBook, boolean filteringCraftable) {
      RecipeResultCollection.RecipeFilterMode recipeFilterMode = filteringCraftable ? RecipeResultCollection.RecipeFilterMode.CRAFTABLE : RecipeResultCollection.RecipeFilterMode.ANY;
      List list = recipeBook.getResultsForCategory(this.tab.category());
      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         RecipeResultCollection recipeResultCollection = (RecipeResultCollection)var5.next();
         Iterator var7 = recipeResultCollection.filter(recipeFilterMode).iterator();

         while(var7.hasNext()) {
            RecipeDisplayEntry recipeDisplayEntry = (RecipeDisplayEntry)var7.next();
            if (recipeBook.isHighlighted(recipeDisplayEntry.id())) {
               this.bounce = 15.0F;
               return;
            }
         }
      }

   }

   public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (this.textures != null) {
         if (this.bounce > 0.0F) {
            float f = 1.0F + 0.1F * (float)Math.sin((double)(this.bounce / 15.0F * 3.1415927F));
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float)(this.getX() + 8), (float)(this.getY() + 12));
            context.getMatrices().scale(1.0F, f);
            context.getMatrices().translate((float)(-(this.getX() + 8)), (float)(-(this.getY() + 12)));
         }

         Identifier identifier = this.textures.get(true, this.toggled);
         int i = this.getX();
         if (this.toggled) {
            i -= 2;
         }

         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, i, this.getY(), this.width, this.height);
         this.renderIcons(context);
         if (this.bounce > 0.0F) {
            context.getMatrices().popMatrix();
            this.bounce -= deltaTicks;
         }

      }
   }

   private void renderIcons(DrawContext context) {
      int i = this.toggled ? -2 : 0;
      if (this.tab.secondaryIcon().isPresent()) {
         context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 3 + i, this.getY() + 5);
         context.drawItemWithoutEntity((ItemStack)this.tab.secondaryIcon().get(), this.getX() + 14 + i, this.getY() + 5);
      } else {
         context.drawItemWithoutEntity(this.tab.primaryIcon(), this.getX() + 9 + i, this.getY() + 5);
      }

   }

   public RecipeBookGroup getCategory() {
      return this.tab.category();
   }

   public boolean hasKnownRecipes(ClientRecipeBook recipeBook) {
      List list = recipeBook.getResultsForCategory(this.tab.category());
      this.visible = false;
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         RecipeResultCollection recipeResultCollection = (RecipeResultCollection)var3.next();
         if (recipeResultCollection.hasDisplayableRecipes()) {
            this.visible = true;
            break;
         }
      }

      return this.visible;
   }
}
