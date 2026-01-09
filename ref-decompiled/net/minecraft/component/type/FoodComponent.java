package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public record FoodComponent(int nutrition, float saturation, boolean canAlwaysEat) implements Consumable {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_INT.fieldOf("nutrition").forGetter(FoodComponent::nutrition), Codec.FLOAT.fieldOf("saturation").forGetter(FoodComponent::saturation), Codec.BOOL.optionalFieldOf("can_always_eat", false).forGetter(FoodComponent::canAlwaysEat)).apply(instance, FoodComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public FoodComponent(int nutrition, float saturation, boolean canAlwaysEat) {
      this.nutrition = nutrition;
      this.saturation = saturation;
      this.canAlwaysEat = canAlwaysEat;
   }

   public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable) {
      Random random = user.getRandom();
      world.playSound((Entity)null, user.getX(), user.getY(), user.getZ(), (SoundEvent)((SoundEvent)consumable.sound().value()), SoundCategory.NEUTRAL, 1.0F, random.nextTriangular(1.0F, 0.4F));
      if (user instanceof PlayerEntity playerEntity) {
         playerEntity.getHungerManager().eat(this);
         world.playSound((Entity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), (SoundEvent)SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, MathHelper.nextBetween(random, 0.9F, 1.0F));
      }

   }

   public int nutrition() {
      return this.nutrition;
   }

   public float saturation() {
      return this.saturation;
   }

   public boolean canAlwaysEat() {
      return this.canAlwaysEat;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, FoodComponent::nutrition, PacketCodecs.FLOAT, FoodComponent::saturation, PacketCodecs.BOOLEAN, FoodComponent::canAlwaysEat, FoodComponent::new);
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean canAlwaysEat;

      public Builder nutrition(int nutrition) {
         this.nutrition = nutrition;
         return this;
      }

      public Builder saturationModifier(float saturationModifier) {
         this.saturationModifier = saturationModifier;
         return this;
      }

      public Builder alwaysEdible() {
         this.canAlwaysEat = true;
         return this;
      }

      public FoodComponent build() {
         float f = HungerConstants.calculateSaturation(this.nutrition, this.saturationModifier);
         return new FoodComponent(this.nutrition, f, this.canAlwaysEat);
      }
   }
}
