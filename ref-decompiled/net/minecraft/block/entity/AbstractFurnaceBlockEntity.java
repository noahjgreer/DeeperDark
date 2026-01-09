package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractFurnaceBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
   protected static final int INPUT_SLOT_INDEX = 0;
   protected static final int FUEL_SLOT_INDEX = 1;
   protected static final int OUTPUT_SLOT_INDEX = 2;
   public static final int BURN_TIME_PROPERTY_INDEX = 0;
   private static final int[] TOP_SLOTS = new int[]{0};
   private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
   private static final int[] SIDE_SLOTS = new int[]{1};
   public static final int FUEL_TIME_PROPERTY_INDEX = 1;
   public static final int COOK_TIME_PROPERTY_INDEX = 2;
   public static final int COOK_TIME_TOTAL_PROPERTY_INDEX = 3;
   public static final int PROPERTY_COUNT = 4;
   public static final int DEFAULT_COOK_TIME = 200;
   public static final int field_31295 = 2;
   private static final Codec CODEC;
   private static final short DEFAULT_LIT_TIME_REMAINING = 0;
   private static final short DEFAULT_LIT_TOTAL_TIME = 0;
   private static final short DEFAULT_COOKING_TIME_SPENT = 0;
   private static final short DEFAULT_COOKING_TOTAL_TIME = 0;
   protected DefaultedList inventory;
   int litTimeRemaining;
   int litTotalTime;
   int cookingTimeSpent;
   int cookingTotalTime;
   protected final PropertyDelegate propertyDelegate;
   private final Reference2IntOpenHashMap recipesUsed;
   private final ServerRecipeManager.MatchGetter matchGetter;

   protected AbstractFurnaceBlockEntity(BlockEntityType blockEntityType, BlockPos pos, BlockState state, RecipeType recipeType) {
      super(blockEntityType, pos, state);
      this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
      this.propertyDelegate = new PropertyDelegate() {
         public int get(int index) {
            switch (index) {
               case 0:
                  return AbstractFurnaceBlockEntity.this.litTimeRemaining;
               case 1:
                  return AbstractFurnaceBlockEntity.this.litTotalTime;
               case 2:
                  return AbstractFurnaceBlockEntity.this.cookingTimeSpent;
               case 3:
                  return AbstractFurnaceBlockEntity.this.cookingTotalTime;
               default:
                  return 0;
            }
         }

         public void set(int index, int value) {
            switch (index) {
               case 0:
                  AbstractFurnaceBlockEntity.this.litTimeRemaining = value;
                  break;
               case 1:
                  AbstractFurnaceBlockEntity.this.litTotalTime = value;
                  break;
               case 2:
                  AbstractFurnaceBlockEntity.this.cookingTimeSpent = value;
                  break;
               case 3:
                  AbstractFurnaceBlockEntity.this.cookingTotalTime = value;
            }

         }

         public int size() {
            return 4;
         }
      };
      this.recipesUsed = new Reference2IntOpenHashMap();
      this.matchGetter = ServerRecipeManager.createCachedMatchGetter(recipeType);
   }

   private boolean isBurning() {
      return this.litTimeRemaining > 0;
   }

   protected void readData(ReadView view) {
      super.readData(view);
      this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
      Inventories.readData(view, this.inventory);
      this.cookingTimeSpent = view.getShort("cooking_time_spent", (short)0);
      this.cookingTotalTime = view.getShort("cooking_total_time", (short)0);
      this.litTimeRemaining = view.getShort("lit_time_remaining", (short)0);
      this.litTotalTime = view.getShort("lit_total_time", (short)0);
      this.recipesUsed.clear();
      this.recipesUsed.putAll((Map)view.read("RecipesUsed", CODEC).orElse(Map.of()));
   }

   protected void writeData(WriteView view) {
      super.writeData(view);
      view.putShort("cooking_time_spent", (short)this.cookingTimeSpent);
      view.putShort("cooking_total_time", (short)this.cookingTotalTime);
      view.putShort("lit_time_remaining", (short)this.litTimeRemaining);
      view.putShort("lit_total_time", (short)this.litTotalTime);
      Inventories.writeData(view, this.inventory);
      view.put("RecipesUsed", CODEC, this.recipesUsed);
   }

   public static void tick(ServerWorld world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity) {
      boolean bl = blockEntity.isBurning();
      boolean bl2 = false;
      if (blockEntity.isBurning()) {
         --blockEntity.litTimeRemaining;
      }

      ItemStack itemStack = (ItemStack)blockEntity.inventory.get(1);
      ItemStack itemStack2 = (ItemStack)blockEntity.inventory.get(0);
      boolean bl3 = !itemStack2.isEmpty();
      boolean bl4 = !itemStack.isEmpty();
      if (blockEntity.isBurning() || bl4 && bl3) {
         SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(itemStack2);
         RecipeEntry recipeEntry;
         if (bl3) {
            recipeEntry = (RecipeEntry)blockEntity.matchGetter.getFirstMatch(singleStackRecipeInput, world).orElse((Object)null);
         } else {
            recipeEntry = null;
         }

         int i = blockEntity.getMaxCountPerStack();
         if (!blockEntity.isBurning() && canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, singleStackRecipeInput, blockEntity.inventory, i)) {
            blockEntity.litTimeRemaining = blockEntity.getFuelTime(world.getFuelRegistry(), itemStack);
            blockEntity.litTotalTime = blockEntity.litTimeRemaining;
            if (blockEntity.isBurning()) {
               bl2 = true;
               if (bl4) {
                  Item item = itemStack.getItem();
                  itemStack.decrement(1);
                  if (itemStack.isEmpty()) {
                     blockEntity.inventory.set(1, item.getRecipeRemainder());
                  }
               }
            }
         }

         if (blockEntity.isBurning() && canAcceptRecipeOutput(world.getRegistryManager(), recipeEntry, singleStackRecipeInput, blockEntity.inventory, i)) {
            ++blockEntity.cookingTimeSpent;
            if (blockEntity.cookingTimeSpent == blockEntity.cookingTotalTime) {
               blockEntity.cookingTimeSpent = 0;
               blockEntity.cookingTotalTime = getCookTime(world, blockEntity);
               if (craftRecipe(world.getRegistryManager(), recipeEntry, singleStackRecipeInput, blockEntity.inventory, i)) {
                  blockEntity.setLastRecipe(recipeEntry);
               }

               bl2 = true;
            }
         } else {
            blockEntity.cookingTimeSpent = 0;
         }
      } else if (!blockEntity.isBurning() && blockEntity.cookingTimeSpent > 0) {
         blockEntity.cookingTimeSpent = MathHelper.clamp(blockEntity.cookingTimeSpent - 2, 0, blockEntity.cookingTotalTime);
      }

      if (bl != blockEntity.isBurning()) {
         bl2 = true;
         state = (BlockState)state.with(AbstractFurnaceBlock.LIT, blockEntity.isBurning());
         world.setBlockState(pos, state, 3);
      }

      if (bl2) {
         markDirty(world, pos, state);
      }

   }

   private static boolean canAcceptRecipeOutput(DynamicRegistryManager dynamicRegistryManager, @Nullable RecipeEntry recipe, SingleStackRecipeInput input, DefaultedList inventory, int maxCount) {
      if (!((ItemStack)inventory.get(0)).isEmpty() && recipe != null) {
         ItemStack itemStack = ((AbstractCookingRecipe)recipe.value()).craft(input, dynamicRegistryManager);
         if (itemStack.isEmpty()) {
            return false;
         } else {
            ItemStack itemStack2 = (ItemStack)inventory.get(2);
            if (itemStack2.isEmpty()) {
               return true;
            } else if (!ItemStack.areItemsAndComponentsEqual(itemStack2, itemStack)) {
               return false;
            } else if (itemStack2.getCount() < maxCount && itemStack2.getCount() < itemStack2.getMaxCount()) {
               return true;
            } else {
               return itemStack2.getCount() < itemStack.getMaxCount();
            }
         }
      } else {
         return false;
      }
   }

   private static boolean craftRecipe(DynamicRegistryManager dynamicRegistryManager, @Nullable RecipeEntry recipe, SingleStackRecipeInput input, DefaultedList inventory, int maxCount) {
      if (recipe != null && canAcceptRecipeOutput(dynamicRegistryManager, recipe, input, inventory, maxCount)) {
         ItemStack itemStack = (ItemStack)inventory.get(0);
         ItemStack itemStack2 = ((AbstractCookingRecipe)recipe.value()).craft(input, dynamicRegistryManager);
         ItemStack itemStack3 = (ItemStack)inventory.get(2);
         if (itemStack3.isEmpty()) {
            inventory.set(2, itemStack2.copy());
         } else if (ItemStack.areItemsAndComponentsEqual(itemStack3, itemStack2)) {
            itemStack3.increment(1);
         }

         if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !((ItemStack)inventory.get(1)).isEmpty() && ((ItemStack)inventory.get(1)).isOf(Items.BUCKET)) {
            inventory.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         itemStack.decrement(1);
         return true;
      } else {
         return false;
      }
   }

   protected int getFuelTime(FuelRegistry fuelRegistry, ItemStack stack) {
      return fuelRegistry.getFuelTicks(stack);
   }

   private static int getCookTime(ServerWorld world, AbstractFurnaceBlockEntity furnace) {
      SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(furnace.getStack(0));
      return (Integer)furnace.matchGetter.getFirstMatch(singleStackRecipeInput, world).map((recipe) -> {
         return ((AbstractCookingRecipe)recipe.value()).getCookingTime();
      }).orElse(200);
   }

   public int[] getAvailableSlots(Direction side) {
      if (side == Direction.DOWN) {
         return BOTTOM_SLOTS;
      } else {
         return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
      }
   }

   public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
      return this.isValid(slot, stack);
   }

   public boolean canExtract(int slot, ItemStack stack, Direction dir) {
      if (dir == Direction.DOWN && slot == 1) {
         return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET);
      } else {
         return true;
      }
   }

   public int size() {
      return this.inventory.size();
   }

   protected DefaultedList getHeldStacks() {
      return this.inventory;
   }

   protected void setHeldStacks(DefaultedList inventory) {
      this.inventory = inventory;
   }

   public void setStack(int slot, ItemStack stack) {
      ItemStack itemStack = (ItemStack)this.inventory.get(slot);
      boolean bl = !stack.isEmpty() && ItemStack.areItemsAndComponentsEqual(itemStack, stack);
      this.inventory.set(slot, stack);
      stack.capCount(this.getMaxCount(stack));
      if (slot == 0 && !bl) {
         World var6 = this.world;
         if (var6 instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)var6;
            this.cookingTotalTime = getCookTime(serverWorld, this);
            this.cookingTimeSpent = 0;
            this.markDirty();
         }
      }

   }

   public boolean isValid(int slot, ItemStack stack) {
      if (slot == 2) {
         return false;
      } else if (slot != 1) {
         return true;
      } else {
         ItemStack itemStack = (ItemStack)this.inventory.get(1);
         return this.world.getFuelRegistry().isFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
      }
   }

   public void setLastRecipe(@Nullable RecipeEntry recipe) {
      if (recipe != null) {
         RegistryKey registryKey = recipe.id();
         this.recipesUsed.addTo(registryKey, 1);
      }

   }

   @Nullable
   public RecipeEntry getLastRecipe() {
      return null;
   }

   public void unlockLastRecipe(PlayerEntity player, List ingredients) {
   }

   public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
      List list = this.getRecipesUsedAndDropExperience(player.getWorld(), player.getPos());
      player.unlockRecipes((Collection)list);
      java.util.Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         RecipeEntry recipeEntry = (RecipeEntry)var3.next();
         if (recipeEntry != null) {
            player.onRecipeCrafted(recipeEntry, this.inventory);
         }
      }

      this.recipesUsed.clear();
   }

   public List getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
      List list = Lists.newArrayList();
      ObjectIterator var4 = this.recipesUsed.reference2IntEntrySet().iterator();

      while(var4.hasNext()) {
         Reference2IntMap.Entry entry = (Reference2IntMap.Entry)var4.next();
         world.getRecipeManager().get((RegistryKey)entry.getKey()).ifPresent((recipe) -> {
            list.add(recipe);
            dropExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe.value()).getExperience());
         });
      }

      return list;
   }

   private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
      int i = MathHelper.floor((float)multiplier * experience);
      float f = MathHelper.fractionalPart((float)multiplier * experience);
      if (f != 0.0F && Math.random() < (double)f) {
         ++i;
      }

      ExperienceOrbEntity.spawn(world, pos, i);
   }

   public void provideRecipeInputs(RecipeFinder finder) {
      java.util.Iterator var2 = this.inventory.iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         finder.addInput(itemStack);
      }

   }

   public void onBlockReplaced(BlockPos pos, BlockState oldState) {
      super.onBlockReplaced(pos, oldState);
      World var4 = this.world;
      if (var4 instanceof ServerWorld serverWorld) {
         this.getRecipesUsedAndDropExperience(serverWorld, Vec3d.ofCenter(pos));
      }

   }

   static {
      CODEC = Codec.unboundedMap(Recipe.KEY_CODEC, Codec.INT);
   }
}
