package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.predicate.component.ComponentMapPredicate;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.village.raid.Raid;
import org.jetbrains.annotations.Nullable;

public record EntityEquipmentPredicate(Optional head, Optional chest, Optional legs, Optional feet, Optional body, Optional mainhand, Optional offhand) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ItemPredicate.CODEC.optionalFieldOf("head").forGetter(EntityEquipmentPredicate::head), ItemPredicate.CODEC.optionalFieldOf("chest").forGetter(EntityEquipmentPredicate::chest), ItemPredicate.CODEC.optionalFieldOf("legs").forGetter(EntityEquipmentPredicate::legs), ItemPredicate.CODEC.optionalFieldOf("feet").forGetter(EntityEquipmentPredicate::feet), ItemPredicate.CODEC.optionalFieldOf("body").forGetter(EntityEquipmentPredicate::body), ItemPredicate.CODEC.optionalFieldOf("mainhand").forGetter(EntityEquipmentPredicate::mainhand), ItemPredicate.CODEC.optionalFieldOf("offhand").forGetter(EntityEquipmentPredicate::offhand)).apply(instance, EntityEquipmentPredicate::new);
   });

   public EntityEquipmentPredicate(Optional optional, Optional optional2, Optional optional3, Optional optional4, Optional optional5, Optional optional6, Optional optional7) {
      this.head = optional;
      this.chest = optional2;
      this.legs = optional3;
      this.feet = optional4;
      this.body = optional5;
      this.mainhand = optional6;
      this.offhand = optional7;
   }

   public static EntityEquipmentPredicate ominousBannerOnHead(RegistryEntryLookup itemLookup, RegistryEntryLookup bannerPatternLookup) {
      return EntityEquipmentPredicate.Builder.create().head(ItemPredicate.Builder.create().items(itemLookup, Items.WHITE_BANNER).components(ComponentsPredicate.Builder.create().exact(ComponentMapPredicate.ofFiltered(Raid.createOminousBanner(bannerPatternLookup).getComponents(), DataComponentTypes.BANNER_PATTERNS, DataComponentTypes.ITEM_NAME)).build())).build();
   }

   public boolean test(@Nullable Entity entity) {
      if (entity instanceof LivingEntity livingEntity) {
         if (this.head.isPresent() && !((ItemPredicate)this.head.get()).test(livingEntity.getEquippedStack(EquipmentSlot.HEAD))) {
            return false;
         } else if (this.chest.isPresent() && !((ItemPredicate)this.chest.get()).test(livingEntity.getEquippedStack(EquipmentSlot.CHEST))) {
            return false;
         } else if (this.legs.isPresent() && !((ItemPredicate)this.legs.get()).test(livingEntity.getEquippedStack(EquipmentSlot.LEGS))) {
            return false;
         } else if (this.feet.isPresent() && !((ItemPredicate)this.feet.get()).test(livingEntity.getEquippedStack(EquipmentSlot.FEET))) {
            return false;
         } else if (this.body.isPresent() && !((ItemPredicate)this.body.get()).test(livingEntity.getEquippedStack(EquipmentSlot.BODY))) {
            return false;
         } else if (this.mainhand.isPresent() && !((ItemPredicate)this.mainhand.get()).test(livingEntity.getEquippedStack(EquipmentSlot.MAINHAND))) {
            return false;
         } else {
            return !this.offhand.isPresent() || ((ItemPredicate)this.offhand.get()).test(livingEntity.getEquippedStack(EquipmentSlot.OFFHAND));
         }
      } else {
         return false;
      }
   }

   public Optional head() {
      return this.head;
   }

   public Optional chest() {
      return this.chest;
   }

   public Optional legs() {
      return this.legs;
   }

   public Optional feet() {
      return this.feet;
   }

   public Optional body() {
      return this.body;
   }

   public Optional mainhand() {
      return this.mainhand;
   }

   public Optional offhand() {
      return this.offhand;
   }

   public static class Builder {
      private Optional head = Optional.empty();
      private Optional chest = Optional.empty();
      private Optional legs = Optional.empty();
      private Optional feet = Optional.empty();
      private Optional body = Optional.empty();
      private Optional mainhand = Optional.empty();
      private Optional offhand = Optional.empty();

      public static Builder create() {
         return new Builder();
      }

      public Builder head(ItemPredicate.Builder item) {
         this.head = Optional.of(item.build());
         return this;
      }

      public Builder chest(ItemPredicate.Builder item) {
         this.chest = Optional.of(item.build());
         return this;
      }

      public Builder legs(ItemPredicate.Builder item) {
         this.legs = Optional.of(item.build());
         return this;
      }

      public Builder feet(ItemPredicate.Builder item) {
         this.feet = Optional.of(item.build());
         return this;
      }

      public Builder body(ItemPredicate.Builder item) {
         this.body = Optional.of(item.build());
         return this;
      }

      public Builder mainhand(ItemPredicate.Builder item) {
         this.mainhand = Optional.of(item.build());
         return this;
      }

      public Builder offhand(ItemPredicate.Builder item) {
         this.offhand = Optional.of(item.build());
         return this;
      }

      public EntityEquipmentPredicate build() {
         return new EntityEquipmentPredicate(this.head, this.chest, this.legs, this.feet, this.body, this.mainhand, this.offhand);
      }
   }
}
