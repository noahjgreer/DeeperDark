package net.minecraft.screen;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.InputSlotFiller;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;

public abstract class AbstractCraftingScreenHandler extends AbstractRecipeScreenHandler {
   private final int width;
   private final int height;
   protected final RecipeInputInventory craftingInventory;
   protected final CraftingResultInventory craftingResultInventory = new CraftingResultInventory();

   public AbstractCraftingScreenHandler(ScreenHandlerType type, int syncId, int width, int height) {
      super(type, syncId);
      this.width = width;
      this.height = height;
      this.craftingInventory = new CraftingInventory(this, width, height);
   }

   protected Slot addResultSlot(PlayerEntity player, int x, int y) {
      return this.addSlot(new CraftingResultSlot(player, this.craftingInventory, this.craftingResultInventory, 0, x, y));
   }

   protected void addInputSlots(int x, int y) {
      for(int i = 0; i < this.width; ++i) {
         for(int j = 0; j < this.height; ++j) {
            this.addSlot(new Slot(this.craftingInventory, j + i * this.width, x + j * 18, y + i * 18));
         }
      }

   }

   public AbstractRecipeScreenHandler.PostFillAction fillInputSlots(boolean craftAll, boolean creative, RecipeEntry recipe, ServerWorld world, PlayerInventory inventory) {
      RecipeEntry recipeEntry = recipe;
      this.onInputSlotFillStart();

      AbstractRecipeScreenHandler.PostFillAction var8;
      try {
         List list = this.getInputSlots();
         var8 = InputSlotFiller.fill(new InputSlotFiller.Handler() {
            public void populateRecipeFinder(RecipeFinder finder) {
               AbstractCraftingScreenHandler.this.populateRecipeFinder(finder);
            }

            public void clear() {
               AbstractCraftingScreenHandler.this.craftingResultInventory.clear();
               AbstractCraftingScreenHandler.this.craftingInventory.clear();
            }

            public boolean matches(RecipeEntry entry) {
               return ((CraftingRecipe)entry.value()).matches(AbstractCraftingScreenHandler.this.craftingInventory.createRecipeInput(), AbstractCraftingScreenHandler.this.getPlayer().getWorld());
            }
         }, this.width, this.height, list, list, inventory, recipeEntry, craftAll, creative);
      } finally {
         this.onInputSlotFillFinish(world, recipe);
      }

      return var8;
   }

   protected void onInputSlotFillStart() {
   }

   protected void onInputSlotFillFinish(ServerWorld world, RecipeEntry recipe) {
   }

   public abstract Slot getOutputSlot();

   public abstract List getInputSlots();

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   protected abstract PlayerEntity getPlayer();

   public void populateRecipeFinder(RecipeFinder finder) {
      this.craftingInventory.provideRecipeInputs(finder);
   }
}
