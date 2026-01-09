package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public record BlocksAttacksComponent(float blockDelaySeconds, float disableCooldownScale, List damageReductions, ItemDamage itemDamage, Optional bypassedBy, Optional blockSound, Optional disableSound) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("block_delay_seconds", 0.0F).forGetter(BlocksAttacksComponent::blockDelaySeconds), Codecs.NON_NEGATIVE_FLOAT.optionalFieldOf("disable_cooldown_scale", 1.0F).forGetter(BlocksAttacksComponent::disableCooldownScale), BlocksAttacksComponent.DamageReduction.CODEC.listOf().optionalFieldOf("damage_reductions", List.of(new DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F))).forGetter(BlocksAttacksComponent::damageReductions), BlocksAttacksComponent.ItemDamage.CODEC.optionalFieldOf("item_damage", BlocksAttacksComponent.ItemDamage.DEFAULT).forGetter(BlocksAttacksComponent::itemDamage), TagKey.codec(RegistryKeys.DAMAGE_TYPE).optionalFieldOf("bypassed_by").forGetter(BlocksAttacksComponent::bypassedBy), SoundEvent.ENTRY_CODEC.optionalFieldOf("block_sound").forGetter(BlocksAttacksComponent::blockSound), SoundEvent.ENTRY_CODEC.optionalFieldOf("disabled_sound").forGetter(BlocksAttacksComponent::disableSound)).apply(instance, BlocksAttacksComponent::new);
   });
   public static final PacketCodec PACKET_CODEC;

   public BlocksAttacksComponent(float f, float g, List list, ItemDamage itemDamage, Optional optional, Optional optional2, Optional optional3) {
      this.blockDelaySeconds = f;
      this.disableCooldownScale = g;
      this.damageReductions = list;
      this.itemDamage = itemDamage;
      this.bypassedBy = optional;
      this.blockSound = optional2;
      this.disableSound = optional3;
   }

   public void playBlockSound(ServerWorld world, LivingEntity from) {
      this.blockSound.ifPresent((sound) -> {
         world.playSound((Entity)null, from.getX(), from.getY(), from.getZ(), sound, from.getSoundCategory(), 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
      });
   }

   public void applyShieldCooldown(ServerWorld world, LivingEntity affectedEntity, float cooldownSeconds, ItemStack stack) {
      int i = this.convertCooldownToTicks(cooldownSeconds);
      if (i > 0) {
         if (affectedEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)affectedEntity;
            playerEntity.getItemCooldownManager().set(stack, i);
         }

         affectedEntity.clearActiveItem();
         this.disableSound.ifPresent((sound) -> {
            world.playSound((Entity)null, affectedEntity.getX(), affectedEntity.getY(), affectedEntity.getZ(), sound, affectedEntity.getSoundCategory(), 0.8F, 0.8F + world.random.nextFloat() * 0.4F);
         });
      }

   }

   public void onShieldHit(World world, ItemStack stack, LivingEntity entity, Hand hand, float itemDamage) {
      if (entity instanceof PlayerEntity playerEntity) {
         if (!world.isClient) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
         }

         int i = this.itemDamage.calculate(itemDamage);
         if (i > 0) {
            stack.damage(i, entity, LivingEntity.getSlotForHand(hand));
         }

      }
   }

   private int convertCooldownToTicks(float cooldownSeconds) {
      float f = cooldownSeconds * this.disableCooldownScale;
      return f > 0.0F ? Math.round(f * 20.0F) : 0;
   }

   public int getBlockDelayTicks() {
      return Math.round(this.blockDelaySeconds * 20.0F);
   }

   public float getDamageReductionAmount(DamageSource source, float damage, double angle) {
      float f = 0.0F;

      DamageReduction damageReduction;
      for(Iterator var6 = this.damageReductions.iterator(); var6.hasNext(); f += damageReduction.getReductionAmount(source, damage, angle)) {
         damageReduction = (DamageReduction)var6.next();
      }

      return MathHelper.clamp(f, 0.0F, damage);
   }

   public float blockDelaySeconds() {
      return this.blockDelaySeconds;
   }

   public float disableCooldownScale() {
      return this.disableCooldownScale;
   }

   public List damageReductions() {
      return this.damageReductions;
   }

   public ItemDamage itemDamage() {
      return this.itemDamage;
   }

   public Optional bypassedBy() {
      return this.bypassedBy;
   }

   public Optional blockSound() {
      return this.blockSound;
   }

   public Optional disableSound() {
      return this.disableSound;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, BlocksAttacksComponent::blockDelaySeconds, PacketCodecs.FLOAT, BlocksAttacksComponent::disableCooldownScale, BlocksAttacksComponent.DamageReduction.PACKET_CODEC.collect(PacketCodecs.toList()), BlocksAttacksComponent::damageReductions, BlocksAttacksComponent.ItemDamage.PACKET_CODEC, BlocksAttacksComponent::itemDamage, TagKey.packetCodec(RegistryKeys.DAMAGE_TYPE).collect(PacketCodecs::optional), BlocksAttacksComponent::bypassedBy, SoundEvent.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), BlocksAttacksComponent::blockSound, SoundEvent.ENTRY_PACKET_CODEC.collect(PacketCodecs::optional), BlocksAttacksComponent::disableSound, BlocksAttacksComponent::new);
   }

   public static record ItemDamage(float threshold, float base, float factor) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.NON_NEGATIVE_FLOAT.fieldOf("threshold").forGetter(ItemDamage::threshold), Codec.FLOAT.fieldOf("base").forGetter(ItemDamage::base), Codec.FLOAT.fieldOf("factor").forGetter(ItemDamage::factor)).apply(instance, ItemDamage::new);
      });
      public static final PacketCodec PACKET_CODEC;
      public static final ItemDamage DEFAULT;

      public ItemDamage(float f, float g, float h) {
         this.threshold = f;
         this.base = g;
         this.factor = h;
      }

      public int calculate(float itemDamage) {
         return itemDamage < this.threshold ? 0 : MathHelper.floor(this.base + this.factor * itemDamage);
      }

      public float threshold() {
         return this.threshold;
      }

      public float base() {
         return this.base;
      }

      public float factor() {
         return this.factor;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, ItemDamage::threshold, PacketCodecs.FLOAT, ItemDamage::base, PacketCodecs.FLOAT, ItemDamage::factor, ItemDamage::new);
         DEFAULT = new ItemDamage(1.0F, 0.0F, 1.0F);
      }
   }

   public static record DamageReduction(float horizontalBlockingAngle, Optional type, float base, float factor) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codecs.POSITIVE_FLOAT.optionalFieldOf("horizontal_blocking_angle", 90.0F).forGetter(DamageReduction::horizontalBlockingAngle), RegistryCodecs.entryList(RegistryKeys.DAMAGE_TYPE).optionalFieldOf("type").forGetter(DamageReduction::type), Codec.FLOAT.fieldOf("base").forGetter(DamageReduction::base), Codec.FLOAT.fieldOf("factor").forGetter(DamageReduction::factor)).apply(instance, DamageReduction::new);
      });
      public static final PacketCodec PACKET_CODEC;

      public DamageReduction(float f, Optional optional, float g, float h) {
         this.horizontalBlockingAngle = f;
         this.type = optional;
         this.base = g;
         this.factor = h;
      }

      public float getReductionAmount(DamageSource source, float damage, double angle) {
         if (angle > (double)(0.017453292F * this.horizontalBlockingAngle)) {
            return 0.0F;
         } else {
            return this.type.isPresent() && !((RegistryEntryList)this.type.get()).contains(source.getTypeRegistryEntry()) ? 0.0F : MathHelper.clamp(this.base + this.factor * damage, 0.0F, damage);
         }
      }

      public float horizontalBlockingAngle() {
         return this.horizontalBlockingAngle;
      }

      public Optional type() {
         return this.type;
      }

      public float base() {
         return this.base;
      }

      public float factor() {
         return this.factor;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, DamageReduction::horizontalBlockingAngle, PacketCodecs.registryEntryList(RegistryKeys.DAMAGE_TYPE).collect(PacketCodecs::optional), DamageReduction::type, PacketCodecs.FLOAT, DamageReduction::base, PacketCodecs.FLOAT, DamageReduction::factor, DamageReduction::new);
      }
   }
}
