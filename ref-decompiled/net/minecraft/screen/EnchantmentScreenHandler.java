package net.minecraft.screen;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class EnchantmentScreenHandler extends ScreenHandler {
   static final Identifier EMPTY_LAPIS_LAZULI_SLOT_TEXTURE = Identifier.ofVanilla("container/slot/lapis_lazuli");
   private final Inventory inventory;
   private final ScreenHandlerContext context;
   private final Random random;
   private final Property seed;
   public final int[] enchantmentPower;
   public final int[] enchantmentId;
   public final int[] enchantmentLevel;

   public EnchantmentScreenHandler(int syncId, PlayerInventory playerInventory) {
      this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
   }

   public EnchantmentScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
      super(ScreenHandlerType.ENCHANTMENT, syncId);
      this.inventory = new SimpleInventory(2) {
         public void markDirty() {
            super.markDirty();
            EnchantmentScreenHandler.this.onContentChanged(this);
         }
      };
      this.random = Random.create();
      this.seed = Property.create();
      this.enchantmentPower = new int[3];
      this.enchantmentId = new int[]{-1, -1, -1};
      this.enchantmentLevel = new int[]{-1, -1, -1};
      this.context = context;
      this.addSlot(new Slot(this, this.inventory, 0, 15, 47) {
         public int getMaxItemCount() {
            return 1;
         }
      });
      this.addSlot(new Slot(this, this.inventory, 1, 35, 47) {
         public boolean canInsert(ItemStack stack) {
            return stack.isOf(Items.LAPIS_LAZULI);
         }

         public Identifier getBackgroundSprite() {
            return EnchantmentScreenHandler.EMPTY_LAPIS_LAZULI_SLOT_TEXTURE;
         }
      });
      this.addPlayerSlots(playerInventory, 8, 84);
      this.addProperty(Property.create((int[])this.enchantmentPower, 0));
      this.addProperty(Property.create((int[])this.enchantmentPower, 1));
      this.addProperty(Property.create((int[])this.enchantmentPower, 2));
      this.addProperty(this.seed).set(playerInventory.player.getEnchantingTableSeed());
      this.addProperty(Property.create((int[])this.enchantmentId, 0));
      this.addProperty(Property.create((int[])this.enchantmentId, 1));
      this.addProperty(Property.create((int[])this.enchantmentId, 2));
      this.addProperty(Property.create((int[])this.enchantmentLevel, 0));
      this.addProperty(Property.create((int[])this.enchantmentLevel, 1));
      this.addProperty(Property.create((int[])this.enchantmentLevel, 2));
   }

   public void onContentChanged(Inventory inventory) {
      if (inventory == this.inventory) {
         ItemStack itemStack = inventory.getStack(0);
         if (!itemStack.isEmpty() && itemStack.isEnchantable()) {
            this.context.run((world, pos) -> {
               IndexedIterable indexedIterable = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getIndexedEntries();
               int i = 0;
               Iterator var6 = EnchantingTableBlock.POWER_PROVIDER_OFFSETS.iterator();

               while(var6.hasNext()) {
                  BlockPos blockPos = (BlockPos)var6.next();
                  if (EnchantingTableBlock.canAccessPowerProvider(world, pos, blockPos)) {
                     ++i;
                  }
               }

               this.random.setSeed((long)this.seed.get());

               int j;
               for(j = 0; j < 3; ++j) {
                  this.enchantmentPower[j] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, j, i, itemStack);
                  this.enchantmentId[j] = -1;
                  this.enchantmentLevel[j] = -1;
                  if (this.enchantmentPower[j] < j + 1) {
                     this.enchantmentPower[j] = 0;
                  }
               }

               for(j = 0; j < 3; ++j) {
                  if (this.enchantmentPower[j] > 0) {
                     List list = this.generateEnchantments(world.getRegistryManager(), itemStack, j, this.enchantmentPower[j]);
                     if (list != null && !list.isEmpty()) {
                        EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)list.get(this.random.nextInt(list.size()));
                        this.enchantmentId[j] = indexedIterable.getRawId(enchantmentLevelEntry.enchantment());
                        this.enchantmentLevel[j] = enchantmentLevelEntry.level();
                     }
                  }
               }

               this.sendContentUpdates();
            });
         } else {
            for(int i = 0; i < 3; ++i) {
               this.enchantmentPower[i] = 0;
               this.enchantmentId[i] = -1;
               this.enchantmentLevel[i] = -1;
            }
         }
      }

   }

   public boolean onButtonClick(PlayerEntity player, int id) {
      if (id >= 0 && id < this.enchantmentPower.length) {
         ItemStack itemStack = this.inventory.getStack(0);
         ItemStack itemStack2 = this.inventory.getStack(1);
         int i = id + 1;
         if ((itemStack2.isEmpty() || itemStack2.getCount() < i) && !player.isInCreativeMode()) {
            return false;
         } else if (this.enchantmentPower[id] <= 0 || itemStack.isEmpty() || (player.experienceLevel < i || player.experienceLevel < this.enchantmentPower[id]) && !player.isInCreativeMode()) {
            return false;
         } else {
            this.context.run((world, pos) -> {
               ItemStack itemStack3 = itemStack;
               List list = this.generateEnchantments(world.getRegistryManager(), itemStack, id, this.enchantmentPower[id]);
               if (!list.isEmpty()) {
                  player.applyEnchantmentCosts(itemStack, i);
                  if (itemStack.isOf(Items.BOOK)) {
                     itemStack3 = itemStack.withItem(Items.ENCHANTED_BOOK);
                     this.inventory.setStack(0, itemStack3);
                  }

                  Iterator var10 = list.iterator();

                  while(var10.hasNext()) {
                     EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)var10.next();
                     itemStack3.addEnchantment(enchantmentLevelEntry.enchantment(), enchantmentLevelEntry.level());
                  }

                  itemStack2.decrementUnlessCreative(i, player);
                  if (itemStack2.isEmpty()) {
                     this.inventory.setStack(1, ItemStack.EMPTY);
                  }

                  player.incrementStat(Stats.ENCHANT_ITEM);
                  if (player instanceof ServerPlayerEntity) {
                     Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)player, itemStack3, i);
                  }

                  this.inventory.markDirty();
                  this.seed.set(player.getEnchantingTableSeed());
                  this.onContentChanged(this.inventory);
                  world.playSound((Entity)null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.1F + 0.9F);
               }

            });
            return true;
         }
      } else {
         String var10000 = String.valueOf(player.getName());
         Util.logErrorOrPause(var10000 + " pressed invalid button id: " + id);
         return false;
      }
   }

   private List generateEnchantments(DynamicRegistryManager registryManager, ItemStack stack, int slot, int level) {
      this.random.setSeed((long)(this.seed.get() + slot));
      Optional optional = registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getOptional(EnchantmentTags.IN_ENCHANTING_TABLE);
      if (optional.isEmpty()) {
         return List.of();
      } else {
         List list = EnchantmentHelper.generateEnchantments(this.random, stack, level, ((RegistryEntryList.Named)optional.get()).stream());
         if (stack.isOf(Items.BOOK) && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
         }

         return list;
      }
   }

   public int getLapisCount() {
      ItemStack itemStack = this.inventory.getStack(1);
      return itemStack.isEmpty() ? 0 : itemStack.getCount();
   }

   public int getSeed() {
      return this.seed.get();
   }

   public void onClosed(PlayerEntity player) {
      super.onClosed(player);
      this.context.run((world, pos) -> {
         this.dropInventory(player, this.inventory);
      });
   }

   public boolean canUse(PlayerEntity player) {
      return canUse(this.context, player, Blocks.ENCHANTING_TABLE);
   }

   public ItemStack quickMove(PlayerEntity player, int slot) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot2 = (Slot)this.slots.get(slot);
      if (slot2 != null && slot2.hasStack()) {
         ItemStack itemStack2 = slot2.getStack();
         itemStack = itemStack2.copy();
         if (slot == 0) {
            if (!this.insertItem(itemStack2, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (slot == 1) {
            if (!this.insertItem(itemStack2, 2, 38, true)) {
               return ItemStack.EMPTY;
            }
         } else if (itemStack2.isOf(Items.LAPIS_LAZULI)) {
            if (!this.insertItem(itemStack2, 1, 2, true)) {
               return ItemStack.EMPTY;
            }
         } else {
            if (((Slot)this.slots.get(0)).hasStack() || !((Slot)this.slots.get(0)).canInsert(itemStack2)) {
               return ItemStack.EMPTY;
            }

            ItemStack itemStack3 = itemStack2.copyWithCount(1);
            itemStack2.decrement(1);
            ((Slot)this.slots.get(0)).setStack(itemStack3);
         }

         if (itemStack2.isEmpty()) {
            slot2.setStack(ItemStack.EMPTY);
         } else {
            slot2.markDirty();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot2.onTakeItem(player, itemStack2);
      }

      return itemStack;
   }
}
