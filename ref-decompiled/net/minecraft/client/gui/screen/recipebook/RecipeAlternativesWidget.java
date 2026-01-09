package net.minecraft.client.gui.screen.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.NetworkRecipeId;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.RecipeGridAligner;
import net.minecraft.recipe.display.FurnaceRecipeDisplay;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.ShapedCraftingRecipeDisplay;
import net.minecraft.recipe.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RecipeAlternativesWidget implements Drawable, Element {
   private static final Identifier OVERLAY_RECIPE_TEXTURE = Identifier.ofVanilla("recipe_book/overlay_recipe");
   private static final int field_32406 = 4;
   private static final int field_32407 = 5;
   private static final float field_33739 = 0.375F;
   public static final int field_42162 = 25;
   private final List alternativeButtons = Lists.newArrayList();
   private boolean visible;
   private int buttonX;
   private int buttonY;
   private RecipeResultCollection resultCollection;
   @Nullable
   private NetworkRecipeId lastClickedRecipe;
   final CurrentIndexProvider currentIndexProvider;
   private final boolean furnace;

   public RecipeAlternativesWidget(CurrentIndexProvider currentIndexProvider, boolean furnace) {
      this.resultCollection = RecipeResultCollection.EMPTY;
      this.currentIndexProvider = currentIndexProvider;
      this.furnace = furnace;
   }

   public void showAlternativesForResult(RecipeResultCollection resultCollection, ContextParameterMap context, boolean filteringCraftable, int buttonX, int buttonY, int areaCenterX, int areaCenterY, float delta) {
      this.resultCollection = resultCollection;
      List list = resultCollection.filter(RecipeResultCollection.RecipeFilterMode.CRAFTABLE);
      List list2 = filteringCraftable ? Collections.emptyList() : resultCollection.filter(RecipeResultCollection.RecipeFilterMode.NOT_CRAFTABLE);
      int i = list.size();
      int j = i + list2.size();
      int k = j <= 16 ? 4 : 5;
      int l = (int)Math.ceil((double)((float)j / (float)k));
      this.buttonX = buttonX;
      this.buttonY = buttonY;
      float f = (float)(this.buttonX + Math.min(j, k) * 25);
      float g = (float)(areaCenterX + 50);
      if (f > g) {
         this.buttonX = (int)((float)this.buttonX - delta * (float)((int)((f - g) / delta)));
      }

      float h = (float)(this.buttonY + l * 25);
      float m = (float)(areaCenterY + 50);
      if (h > m) {
         this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((h - m) / delta));
      }

      float n = (float)this.buttonY;
      float o = (float)(areaCenterY - 100);
      if (n < o) {
         this.buttonY = (int)((float)this.buttonY - delta * (float)MathHelper.ceil((n - o) / delta));
      }

      this.visible = true;
      this.alternativeButtons.clear();

      for(int p = 0; p < j; ++p) {
         boolean bl = p < i;
         RecipeDisplayEntry recipeDisplayEntry = bl ? (RecipeDisplayEntry)list.get(p) : (RecipeDisplayEntry)list2.get(p - i);
         int q = this.buttonX + 4 + 25 * (p % k);
         int r = this.buttonY + 5 + 25 * (p / k);
         if (this.furnace) {
            this.alternativeButtons.add(new FurnaceAlternativeButtonWidget(this, q, r, recipeDisplayEntry.id(), recipeDisplayEntry.display(), context, bl));
         } else {
            this.alternativeButtons.add(new CraftingAlternativeButtonWidget(this, q, r, recipeDisplayEntry.id(), recipeDisplayEntry.display(), context, bl));
         }
      }

      this.lastClickedRecipe = null;
   }

   public RecipeResultCollection getResults() {
      return this.resultCollection;
   }

   @Nullable
   public NetworkRecipeId getLastClickedRecipe() {
      return this.lastClickedRecipe;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else {
         Iterator var6 = this.alternativeButtons.iterator();

         AlternativeButtonWidget alternativeButtonWidget;
         do {
            if (!var6.hasNext()) {
               return false;
            }

            alternativeButtonWidget = (AlternativeButtonWidget)var6.next();
         } while(!alternativeButtonWidget.mouseClicked(mouseX, mouseY, button));

         this.lastClickedRecipe = alternativeButtonWidget.recipeId;
         return true;
      }
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return false;
   }

   public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
      if (this.visible) {
         int i = this.alternativeButtons.size() <= 16 ? 4 : 5;
         int j = Math.min(this.alternativeButtons.size(), i);
         int k = MathHelper.ceil((float)this.alternativeButtons.size() / (float)i);
         int l = true;
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, OVERLAY_RECIPE_TEXTURE, this.buttonX, this.buttonY, j * 25 + 8, k * 25 + 8);
         Iterator var9 = this.alternativeButtons.iterator();

         while(var9.hasNext()) {
            AlternativeButtonWidget alternativeButtonWidget = (AlternativeButtonWidget)var9.next();
            alternativeButtonWidget.render(context, mouseX, mouseY, deltaTicks);
         }

      }
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setFocused(boolean focused) {
   }

   public boolean isFocused() {
      return false;
   }

   @Environment(EnvType.CLIENT)
   private class FurnaceAlternativeButtonWidget extends AlternativeButtonWidget {
      private static final Identifier FURNACE_OVERLAY = Identifier.ofVanilla("recipe_book/furnace_overlay");
      private static final Identifier FURNACE_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_highlighted");
      private static final Identifier FURNACE_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled");
      private static final Identifier FURNACE_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/furnace_overlay_disabled_highlighted");

      public FurnaceAlternativeButtonWidget(final RecipeAlternativesWidget recipeAlternativesWidget, final int x, final int y, final NetworkRecipeId recipeId, final RecipeDisplay display, final ContextParameterMap context, final boolean craftable) {
         super(x, y, recipeId, craftable, alignRecipe(display, context));
      }

      private static List alignRecipe(RecipeDisplay display, ContextParameterMap context) {
         if (display instanceof FurnaceRecipeDisplay furnaceRecipeDisplay) {
            List list = furnaceRecipeDisplay.ingredient().getStacks(context);
            if (!list.isEmpty()) {
               return List.of(slot(1, 1, list));
            }
         }

         return List.of();
      }

      protected Identifier getOverlayTexture(boolean enabled) {
         if (enabled) {
            return this.isSelected() ? FURNACE_OVERLAY_HIGHLIGHTED : FURNACE_OVERLAY;
         } else {
            return this.isSelected() ? FURNACE_OVERLAY_DISABLED_HIGHLIGHTED : FURNACE_OVERLAY_DISABLED;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   class CraftingAlternativeButtonWidget extends AlternativeButtonWidget {
      private static final Identifier CRAFTING_OVERLAY = Identifier.ofVanilla("recipe_book/crafting_overlay");
      private static final Identifier CRAFTING_OVERLAY_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_highlighted");
      private static final Identifier CRAFTING_OVERLAY_DISABLED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled");
      private static final Identifier CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED = Identifier.ofVanilla("recipe_book/crafting_overlay_disabled_highlighted");
      private static final int field_54828 = 3;
      private static final int field_54829 = 3;

      public CraftingAlternativeButtonWidget(final RecipeAlternativesWidget recipeAlternativesWidget, final int x, final int y, final NetworkRecipeId recipeId, final RecipeDisplay display, final ContextParameterMap context, final boolean craftable) {
         super(x, y, recipeId, craftable, collectInputSlots(display, context));
      }

      private static List collectInputSlots(RecipeDisplay display, ContextParameterMap context) {
         List list = new ArrayList();
         Objects.requireNonNull(display);
         byte var4 = 0;
         switch (display.typeSwitch<invokedynamic>(display, var4)) {
            case 0:
               ShapedCraftingRecipeDisplay shapedCraftingRecipeDisplay = (ShapedCraftingRecipeDisplay)display;
               RecipeGridAligner.alignRecipeToGrid(3, 3, shapedCraftingRecipeDisplay.width(), shapedCraftingRecipeDisplay.height(), shapedCraftingRecipeDisplay.ingredients(), (slot, index, x, y) -> {
                  List list2 = slot.getStacks(context);
                  if (!list2.isEmpty()) {
                     list.add(slot(x, y, list2));
                  }

               });
               break;
            case 1:
               ShapelessCraftingRecipeDisplay shapelessCraftingRecipeDisplay = (ShapelessCraftingRecipeDisplay)display;
               List list2 = shapelessCraftingRecipeDisplay.ingredients();

               for(int i = 0; i < list2.size(); ++i) {
                  List list3 = ((SlotDisplay)list2.get(i)).getStacks(context);
                  if (!list3.isEmpty()) {
                     list.add(slot(i % 3, i / 3, list3));
                  }
               }
         }

         return list;
      }

      protected Identifier getOverlayTexture(boolean enabled) {
         if (enabled) {
            return this.isSelected() ? CRAFTING_OVERLAY_HIGHLIGHTED : CRAFTING_OVERLAY;
         } else {
            return this.isSelected() ? CRAFTING_OVERLAY_DISABLED_HIGHLIGHTED : CRAFTING_OVERLAY_DISABLED;
         }
      }
   }

   @Environment(EnvType.CLIENT)
   private abstract class AlternativeButtonWidget extends ClickableWidget {
      final NetworkRecipeId recipeId;
      private final boolean craftable;
      private final List inputSlots;

      public AlternativeButtonWidget(final int x, final int y, final NetworkRecipeId recipeId, final boolean craftable, final List inputSlots) {
         super(x, y, 24, 24, ScreenTexts.EMPTY);
         this.inputSlots = inputSlots;
         this.recipeId = recipeId;
         this.craftable = craftable;
      }

      protected static InputSlot slot(int x, int y, List stacks) {
         return new InputSlot(3 + x * 7, 3 + y * 7, stacks);
      }

      protected abstract Identifier getOverlayTexture(boolean enabled);

      public void appendClickableNarrations(NarrationMessageBuilder builder) {
         this.appendDefaultNarrations(builder);
      }

      public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getOverlayTexture(this.craftable), this.getX(), this.getY(), this.width, this.height);
         float f = (float)(this.getX() + 2);
         float g = (float)(this.getY() + 2);
         Iterator var7 = this.inputSlots.iterator();

         while(var7.hasNext()) {
            InputSlot inputSlot = (InputSlot)var7.next();
            context.getMatrices().pushMatrix();
            context.getMatrices().translate(f + (float)inputSlot.y, g + (float)inputSlot.x);
            context.getMatrices().scale(0.375F, 0.375F);
            context.getMatrices().translate(-8.0F, -8.0F);
            context.drawItem(inputSlot.get(RecipeAlternativesWidget.this.currentIndexProvider.currentIndex()), 0, 0);
            context.getMatrices().popMatrix();
         }

      }

      @Environment(EnvType.CLIENT)
      protected static record InputSlot(int y, int x, List stacks) {
         final int y;
         final int x;

         public InputSlot(int i, int y, List list) {
            if (list.isEmpty()) {
               throw new IllegalArgumentException("Ingredient list must be non-empty");
            } else {
               this.y = i;
               this.x = y;
               this.stacks = list;
            }
         }

         public ItemStack get(int index) {
            return (ItemStack)this.stacks.get(index % this.stacks.size());
         }

         public int y() {
            return this.y;
         }

         public int x() {
            return this.x;
         }

         public List stacks() {
            return this.stacks;
         }
      }
   }
}
