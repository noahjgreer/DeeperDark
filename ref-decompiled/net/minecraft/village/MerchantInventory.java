package net.minecraft.village;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class MerchantInventory implements Inventory {
   private final Merchant merchant;
   private final DefaultedList inventory;
   @Nullable
   private TradeOffer tradeOffer;
   private int offerIndex;
   private int merchantRewardedExperience;

   public MerchantInventory(Merchant merchant) {
      this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
      this.merchant = merchant;
   }

   public int size() {
      return this.inventory.size();
   }

   public boolean isEmpty() {
      java.util.Iterator var1 = this.inventory.iterator();

      ItemStack itemStack;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         itemStack = (ItemStack)var1.next();
      } while(itemStack.isEmpty());

      return false;
   }

   public ItemStack getStack(int slot) {
      return (ItemStack)this.inventory.get(slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      ItemStack itemStack = (ItemStack)this.inventory.get(slot);
      if (slot == 2 && !itemStack.isEmpty()) {
         return Inventories.splitStack(this.inventory, slot, itemStack.getCount());
      } else {
         ItemStack itemStack2 = Inventories.splitStack(this.inventory, slot, amount);
         if (!itemStack2.isEmpty() && this.needsOfferUpdate(slot)) {
            this.updateOffers();
         }

         return itemStack2;
      }
   }

   private boolean needsOfferUpdate(int slot) {
      return slot == 0 || slot == 1;
   }

   public ItemStack removeStack(int slot) {
      return Inventories.removeStack(this.inventory, slot);
   }

   public void setStack(int slot, ItemStack stack) {
      this.inventory.set(slot, stack);
      stack.capCount(this.getMaxCount(stack));
      if (this.needsOfferUpdate(slot)) {
         this.updateOffers();
      }

   }

   public boolean canPlayerUse(PlayerEntity player) {
      return this.merchant.getCustomer() == player;
   }

   public void markDirty() {
      this.updateOffers();
   }

   public void updateOffers() {
      this.tradeOffer = null;
      ItemStack itemStack;
      ItemStack itemStack2;
      if (((ItemStack)this.inventory.get(0)).isEmpty()) {
         itemStack = (ItemStack)this.inventory.get(1);
         itemStack2 = ItemStack.EMPTY;
      } else {
         itemStack = (ItemStack)this.inventory.get(0);
         itemStack2 = (ItemStack)this.inventory.get(1);
      }

      if (itemStack.isEmpty()) {
         this.setStack(2, ItemStack.EMPTY);
         this.merchantRewardedExperience = 0;
      } else {
         TradeOfferList tradeOfferList = this.merchant.getOffers();
         if (!tradeOfferList.isEmpty()) {
            TradeOffer tradeOffer = tradeOfferList.getValidOffer(itemStack, itemStack2, this.offerIndex);
            if (tradeOffer == null || tradeOffer.isDisabled()) {
               this.tradeOffer = tradeOffer;
               tradeOffer = tradeOfferList.getValidOffer(itemStack2, itemStack, this.offerIndex);
            }

            if (tradeOffer != null && !tradeOffer.isDisabled()) {
               this.tradeOffer = tradeOffer;
               this.setStack(2, tradeOffer.copySellItem());
               this.merchantRewardedExperience = tradeOffer.getMerchantExperience();
            } else {
               this.setStack(2, ItemStack.EMPTY);
               this.merchantRewardedExperience = 0;
            }
         }

         this.merchant.onSellingItem(this.getStack(2));
      }
   }

   @Nullable
   public TradeOffer getTradeOffer() {
      return this.tradeOffer;
   }

   public void setOfferIndex(int index) {
      this.offerIndex = index;
      this.updateOffers();
   }

   public void clear() {
      this.inventory.clear();
   }

   public int getMerchantRewardedExperience() {
      return this.merchantRewardedExperience;
   }
}
