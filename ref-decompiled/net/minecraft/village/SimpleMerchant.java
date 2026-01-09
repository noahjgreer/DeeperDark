package net.minecraft.village;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class SimpleMerchant implements Merchant {
   private final PlayerEntity player;
   private TradeOfferList offers = new TradeOfferList();
   private int experience;

   public SimpleMerchant(PlayerEntity player) {
      this.player = player;
   }

   public PlayerEntity getCustomer() {
      return this.player;
   }

   public void setCustomer(@Nullable PlayerEntity customer) {
   }

   public TradeOfferList getOffers() {
      return this.offers;
   }

   public void setOffersFromServer(TradeOfferList offers) {
      this.offers = offers;
   }

   public void trade(TradeOffer offer) {
      offer.use();
   }

   public void onSellingItem(ItemStack stack) {
   }

   public boolean isClient() {
      return this.player.getWorld().isClient;
   }

   public boolean canInteract(PlayerEntity player) {
      return this.player == player;
   }

   public int getExperience() {
      return this.experience;
   }

   public void setExperienceFromServer(int experience) {
      this.experience = experience;
   }

   public boolean isLeveledMerchant() {
      return true;
   }

   public SoundEvent getYesSound() {
      return SoundEvents.ENTITY_VILLAGER_YES;
   }
}
