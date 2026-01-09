package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record EquippableComponent(EquipmentSlot slot, RegistryEntry equipSound, Optional assetId, Optional cameraOverlay, Optional allowedEntities, boolean dispensable, boolean swappable, boolean damageOnHurt, boolean equipOnInteract, boolean canBeSheared, RegistryEntry shearingSound) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(EquipmentSlot.CODEC.fieldOf("slot").forGetter(EquippableComponent::slot), SoundEvent.ENTRY_CODEC.optionalFieldOf("equip_sound", SoundEvents.ITEM_ARMOR_EQUIP_GENERIC).forGetter(EquippableComponent::equipSound), RegistryKey.createCodec(EquipmentAssetKeys.REGISTRY_KEY).optionalFieldOf("asset_id").forGetter(EquippableComponent::assetId), Identifier.CODEC.optionalFieldOf("camera_overlay").forGetter(EquippableComponent::cameraOverlay), RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).optionalFieldOf("allowed_entities").forGetter(EquippableComponent::allowedEntities), Codec.BOOL.optionalFieldOf("dispensable", true).forGetter(EquippableComponent::dispensable), Codec.BOOL.optionalFieldOf("swappable", true).forGetter(EquippableComponent::swappable), Codec.BOOL.optionalFieldOf("damage_on_hurt", true).forGetter(EquippableComponent::damageOnHurt), Codec.BOOL.optionalFieldOf("equip_on_interact", false).forGetter(EquippableComponent::equipOnInteract), Codec.BOOL.optionalFieldOf("can_be_sheared", false).forGetter(EquippableComponent::canBeSheared), SoundEvent.ENTRY_CODEC.optionalFieldOf("shearing_sound", Registries.SOUND_EVENT.getEntry((Object)SoundEvents.ITEM_SHEARS_SNIP)).forGetter(EquippableComponent::shearingSound)).apply(instance, EquippableComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public EquippableComponent(EquipmentSlot equipmentSlot, RegistryEntry registryEntry, Optional optional, Optional optional2, Optional optional3, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, RegistryEntry registryEntry2) {
      this.slot = equipmentSlot;
      this.equipSound = registryEntry;
      this.assetId = optional;
      this.cameraOverlay = optional2;
      this.allowedEntities = optional3;
      this.dispensable = bl;
      this.swappable = bl2;
      this.damageOnHurt = bl3;
      this.equipOnInteract = bl4;
      this.canBeSheared = bl5;
      this.shearingSound = registryEntry2;
   }

   public static EquippableComponent ofCarpet(DyeColor color) {
      return builder(EquipmentSlot.BODY).equipSound(SoundEvents.ENTITY_LLAMA_SWAG).model((RegistryKey)EquipmentAssetKeys.CARPET_FROM_COLOR.get(color)).allowedEntities(EntityType.LLAMA, EntityType.TRADER_LLAMA).canBeSheared(true).shearingSound(SoundEvents.ITEM_LLAMA_CARPET_UNEQUIP).build();
   }

   public static EquippableComponent ofSaddle() {
      RegistryEntryLookup registryEntryLookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);
      return builder(EquipmentSlot.SADDLE).equipSound(SoundEvents.ENTITY_HORSE_SADDLE).model(EquipmentAssetKeys.SADDLE).allowedEntities((RegistryEntryList)registryEntryLookup.getOrThrow(EntityTypeTags.CAN_EQUIP_SADDLE)).equipOnInteract(true).canBeSheared(true).shearingSound(SoundEvents.ITEM_SADDLE_UNEQUIP).build();
   }

   public static EquippableComponent ofHarness(DyeColor color) {
      RegistryEntryLookup registryEntryLookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);
      return builder(EquipmentSlot.BODY).equipSound(SoundEvents.ENTITY_HAPPY_GHAST_EQUIP).model((RegistryKey)EquipmentAssetKeys.HARNESS_FROM_COLOR.get(color)).allowedEntities((RegistryEntryList)registryEntryLookup.getOrThrow(EntityTypeTags.CAN_EQUIP_HARNESS)).equipOnInteract(true).canBeSheared(true).shearingSound(Registries.SOUND_EVENT.getEntry((Object)SoundEvents.ENTITY_HAPPY_GHAST_UNEQUIP)).build();
   }

   public static Builder builder(EquipmentSlot slot) {
      return new Builder(slot);
   }

   public ActionResult equip(ItemStack stack, PlayerEntity player) {
      if (player.canUseSlot(this.slot) && this.allows(player.getType())) {
         ItemStack itemStack = player.getEquippedStack(this.slot);
         if ((!EnchantmentHelper.hasAnyEnchantmentsWith(itemStack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE) || player.isCreative()) && !ItemStack.areItemsAndComponentsEqual(stack, itemStack)) {
            if (!player.getWorld().isClient()) {
               player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            }

            ItemStack itemStack2;
            ItemStack itemStack3;
            if (stack.getCount() <= 1) {
               itemStack2 = itemStack.isEmpty() ? stack : itemStack.copyAndEmpty();
               itemStack3 = player.isCreative() ? stack.copy() : stack.copyAndEmpty();
               player.equipStack(this.slot, itemStack3);
               return ActionResult.SUCCESS.withNewHandStack(itemStack2);
            } else {
               itemStack2 = itemStack.copyAndEmpty();
               itemStack3 = stack.splitUnlessCreative(1, player);
               player.equipStack(this.slot, itemStack3);
               if (!player.getInventory().insertStack(itemStack2)) {
                  player.dropItem(itemStack2, false);
               }

               return ActionResult.SUCCESS.withNewHandStack(stack);
            }
         } else {
            return ActionResult.FAIL;
         }
      } else {
         return ActionResult.PASS;
      }
   }

   public ActionResult equipOnInteract(PlayerEntity player, LivingEntity entity, ItemStack stack) {
      if (entity.canEquip(stack, this.slot) && !entity.hasStackEquipped(this.slot) && entity.isAlive()) {
         if (!player.getWorld().isClient()) {
            entity.equipStack(this.slot, stack.split(1));
            if (entity instanceof MobEntity) {
               MobEntity mobEntity = (MobEntity)entity;
               mobEntity.setDropGuaranteed(this.slot);
            }
         }

         return ActionResult.SUCCESS;
      } else {
         return ActionResult.PASS;
      }
   }

   public boolean allows(EntityType entityType) {
      return this.allowedEntities.isEmpty() || ((RegistryEntryList)this.allowedEntities.get()).contains(entityType.getRegistryEntry());
   }

   public EquipmentSlot slot() {
      return this.slot;
   }

   public RegistryEntry equipSound() {
      return this.equipSound;
   }

   public Optional assetId() {
      return this.assetId;
   }

   public Optional cameraOverlay() {
      return this.cameraOverlay;
   }

   public Optional allowedEntities() {
      return this.allowedEntities;
   }

   public boolean dispensable() {
      return this.dispensable;
   }

   public boolean swappable() {
      return this.swappable;
   }

   public boolean damageOnHurt() {
      return this.damageOnHurt;
   }

   public boolean equipOnInteract() {
      return this.equipOnInteract;
   }

   public boolean canBeSheared() {
      return this.canBeSheared;
   }

   public RegistryEntry shearingSound() {
      return this.shearingSound;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(EquipmentSlot.PACKET_CODEC, EquippableComponent::slot, SoundEvent.ENTRY_PACKET_CODEC, EquippableComponent::equipSound, RegistryKey.createPacketCodec(EquipmentAssetKeys.REGISTRY_KEY).collect(PacketCodecs::optional), EquippableComponent::assetId, Identifier.PACKET_CODEC.collect(PacketCodecs::optional), EquippableComponent::cameraOverlay, PacketCodecs.registryEntryList(RegistryKeys.ENTITY_TYPE).collect(PacketCodecs::optional), EquippableComponent::allowedEntities, PacketCodecs.BOOLEAN, EquippableComponent::dispensable, PacketCodecs.BOOLEAN, EquippableComponent::swappable, PacketCodecs.BOOLEAN, EquippableComponent::damageOnHurt, PacketCodecs.BOOLEAN, EquippableComponent::equipOnInteract, PacketCodecs.BOOLEAN, EquippableComponent::canBeSheared, SoundEvent.ENTRY_PACKET_CODEC, EquippableComponent::shearingSound, EquippableComponent::new);
   }

   public static class Builder {
      private final EquipmentSlot slot;
      private RegistryEntry equipSound;
      private Optional model;
      private Optional cameraOverlay;
      private Optional allowedEntities;
      private boolean dispensable;
      private boolean swappable;
      private boolean damageOnHurt;
      private boolean equipOnInteract;
      private boolean canBeSheared;
      private RegistryEntry shearingSound;

      Builder(EquipmentSlot slot) {
         this.equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
         this.model = Optional.empty();
         this.cameraOverlay = Optional.empty();
         this.allowedEntities = Optional.empty();
         this.dispensable = true;
         this.swappable = true;
         this.damageOnHurt = true;
         this.shearingSound = Registries.SOUND_EVENT.getEntry((Object)SoundEvents.ITEM_SHEARS_SNIP);
         this.slot = slot;
      }

      public Builder equipSound(RegistryEntry equipSound) {
         this.equipSound = equipSound;
         return this;
      }

      public Builder model(RegistryKey model) {
         this.model = Optional.of(model);
         return this;
      }

      public Builder cameraOverlay(Identifier cameraOverlay) {
         this.cameraOverlay = Optional.of(cameraOverlay);
         return this;
      }

      public Builder allowedEntities(EntityType... allowedEntities) {
         return this.allowedEntities((RegistryEntryList)RegistryEntryList.of((Function)(EntityType::getRegistryEntry), (Object[])allowedEntities));
      }

      public Builder allowedEntities(RegistryEntryList allowedEntities) {
         this.allowedEntities = Optional.of(allowedEntities);
         return this;
      }

      public Builder dispensable(boolean dispensable) {
         this.dispensable = dispensable;
         return this;
      }

      public Builder swappable(boolean swappable) {
         this.swappable = swappable;
         return this;
      }

      public Builder damageOnHurt(boolean damageOnHurt) {
         this.damageOnHurt = damageOnHurt;
         return this;
      }

      public Builder equipOnInteract(boolean equipOnInteract) {
         this.equipOnInteract = equipOnInteract;
         return this;
      }

      public Builder canBeSheared(boolean canBeSheared) {
         this.canBeSheared = canBeSheared;
         return this;
      }

      public Builder shearingSound(RegistryEntry shearingSound) {
         this.shearingSound = shearingSound;
         return this;
      }

      public EquippableComponent build() {
         return new EquippableComponent(this.slot, this.equipSound, this.model, this.cameraOverlay, this.allowedEntities, this.dispensable, this.swappable, this.damageOnHurt, this.equipOnInteract, this.canBeSheared, this.shearingSound);
      }
   }
}
