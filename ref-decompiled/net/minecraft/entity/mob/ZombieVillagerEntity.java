package net.minecraft.entity.mob;

import com.google.common.annotations.VisibleForTesting;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerGossips;
import net.minecraft.village.VillagerType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ZombieVillagerEntity extends ZombieEntity implements VillagerDataContainer {
   private static final TrackedData CONVERTING;
   private static final TrackedData VILLAGER_DATA;
   private static final int BASE_CONVERSION_DELAY = 3600;
   private static final int field_30520 = 6000;
   private static final int field_30521 = 14;
   private static final int field_30522 = 4;
   private static final int DEFAULT_CONVERSION_TIME = -1;
   private static final int DEFAULT_EXPERIENCE = 0;
   private int conversionTimer;
   @Nullable
   private UUID converter;
   @Nullable
   private VillagerGossips gossip;
   @Nullable
   private TradeOfferList offerData;
   private int experience = 0;

   public ZombieVillagerEntity(EntityType entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker(DataTracker.Builder builder) {
      super.initDataTracker(builder);
      builder.add(CONVERTING, false);
      builder.add(VILLAGER_DATA, VillagerEntity.createVillagerData());
   }

   protected void writeCustomData(WriteView view) {
      super.writeCustomData(view);
      view.put("VillagerData", VillagerData.CODEC, this.getVillagerData());
      view.putNullable("Offers", TradeOfferList.CODEC, this.offerData);
      view.putNullable("Gossips", VillagerGossips.CODEC, this.gossip);
      view.putInt("ConversionTime", this.isConverting() ? this.conversionTimer : -1);
      view.putNullable("ConversionPlayer", Uuids.INT_STREAM_CODEC, this.converter);
      view.putInt("Xp", this.experience);
   }

   protected void readCustomData(ReadView view) {
      super.readCustomData(view);
      this.dataTracker.set(VILLAGER_DATA, (VillagerData)view.read("VillagerData", VillagerData.CODEC).orElseGet(VillagerEntity::createVillagerData));
      this.offerData = (TradeOfferList)view.read("Offers", TradeOfferList.CODEC).orElse((Object)null);
      this.gossip = (VillagerGossips)view.read("Gossips", VillagerGossips.CODEC).orElse((Object)null);
      int i = view.getInt("ConversionTime", -1);
      if (i != -1) {
         UUID uUID = (UUID)view.read("ConversionPlayer", Uuids.INT_STREAM_CODEC).orElse((Object)null);
         this.setConverting(uUID, i);
      } else {
         this.getDataTracker().set(CONVERTING, false);
         this.conversionTimer = -1;
      }

      this.experience = view.getInt("Xp", 0);
   }

   public void tick() {
      if (!this.getWorld().isClient && this.isAlive() && this.isConverting()) {
         int i = this.getConversionRate();
         this.conversionTimer -= i;
         if (this.conversionTimer <= 0) {
            this.finishConversion((ServerWorld)this.getWorld());
         }
      }

      super.tick();
   }

   public ActionResult interactMob(PlayerEntity player, Hand hand) {
      ItemStack itemStack = player.getStackInHand(hand);
      if (itemStack.isOf(Items.GOLDEN_APPLE)) {
         if (this.hasStatusEffect(StatusEffects.WEAKNESS)) {
            itemStack.decrementUnlessCreative(1, player);
            if (!this.getWorld().isClient) {
               this.setConverting(player.getUuid(), this.random.nextInt(2401) + 3600);
            }

            return ActionResult.SUCCESS_SERVER;
         } else {
            return ActionResult.CONSUME;
         }
      } else {
         return super.interactMob(player, hand);
      }
   }

   protected boolean canConvertInWater() {
      return false;
   }

   public boolean canImmediatelyDespawn(double distanceSquared) {
      return !this.isConverting() && this.experience == 0;
   }

   public boolean isConverting() {
      return (Boolean)this.getDataTracker().get(CONVERTING);
   }

   private void setConverting(@Nullable UUID uuid, int delay) {
      this.converter = uuid;
      this.conversionTimer = delay;
      this.getDataTracker().set(CONVERTING, true);
      this.removeStatusEffect(StatusEffects.WEAKNESS);
      this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, delay, Math.min(this.getWorld().getDifficulty().getId() - 1, 0)));
      this.getWorld().sendEntityStatus(this, (byte)16);
   }

   public void handleStatus(byte status) {
      if (status == 16) {
         if (!this.isSilent()) {
            this.getWorld().playSoundClient(this.getX(), this.getEyeY(), this.getZ(), SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
         }

      } else {
         super.handleStatus(status);
      }
   }

   private void finishConversion(ServerWorld world) {
      this.convertTo(EntityType.VILLAGER, EntityConversionContext.create(this, false, false), (villager) -> {
         Iterator var3 = this.dropForeignEquipment(world, (stack) -> {
            return !EnchantmentHelper.hasAnyEnchantmentsWith(stack, EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE);
         }).iterator();

         while(var3.hasNext()) {
            EquipmentSlot equipmentSlot = (EquipmentSlot)var3.next();
            StackReference stackReference = villager.getStackReference(equipmentSlot.getEntitySlotId() + 300);
            stackReference.set(this.getEquippedStack(equipmentSlot));
         }

         villager.setVillagerData(this.getVillagerData());
         if (this.gossip != null) {
            villager.readGossipData(this.gossip);
         }

         if (this.offerData != null) {
            villager.setOffers(this.offerData.copy());
         }

         villager.setExperience(this.experience);
         villager.initialize(world, world.getLocalDifficulty(villager.getBlockPos()), SpawnReason.CONVERSION, (EntityData)null);
         villager.reinitializeBrain(world);
         if (this.converter != null) {
            PlayerEntity playerEntity = world.getPlayerByUuid(this.converter);
            if (playerEntity instanceof ServerPlayerEntity) {
               Criteria.CURED_ZOMBIE_VILLAGER.trigger((ServerPlayerEntity)playerEntity, this, villager);
               world.handleInteraction(EntityInteraction.ZOMBIE_VILLAGER_CURED, playerEntity, villager);
            }
         }

         villager.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 0));
         if (!this.isSilent()) {
            world.syncWorldEvent((Entity)null, 1027, this.getBlockPos(), 0);
         }

      });
   }

   @VisibleForTesting
   public void setConversionTimer(int conversionTimer) {
      this.conversionTimer = conversionTimer;
   }

   private int getConversionRate() {
      int i = 1;
      if (this.random.nextFloat() < 0.01F) {
         int j = 0;
         BlockPos.Mutable mutable = new BlockPos.Mutable();

         for(int k = (int)this.getX() - 4; k < (int)this.getX() + 4 && j < 14; ++k) {
            for(int l = (int)this.getY() - 4; l < (int)this.getY() + 4 && j < 14; ++l) {
               for(int m = (int)this.getZ() - 4; m < (int)this.getZ() + 4 && j < 14; ++m) {
                  BlockState blockState = this.getWorld().getBlockState(mutable.set(k, l, m));
                  if (blockState.isOf(Blocks.IRON_BARS) || blockState.getBlock() instanceof BedBlock) {
                     if (this.random.nextFloat() < 0.3F) {
                        ++i;
                     }

                     ++j;
                  }
               }
            }
         }
      }

      return i;
   }

   public float getSoundPitch() {
      return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource source) {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
   }

   protected ItemStack getSkull() {
      return ItemStack.EMPTY;
   }

   public void setOfferData(TradeOfferList offerData) {
      this.offerData = offerData;
   }

   public void setGossip(VillagerGossips gossip) {
      this.gossip = gossip;
   }

   @Nullable
   public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
      VillagerData villagerData = this.getVillagerData().withType(world.getRegistryManager(), VillagerType.forBiome(world.getBiome(this.getBlockPos())));
      Optional optional = Registries.VILLAGER_PROFESSION.getRandom(this.random);
      if (optional.isPresent()) {
         villagerData = villagerData.withProfession((RegistryEntry)optional.get());
      }

      this.setVillagerData(villagerData);
      return super.initialize(world, difficulty, spawnReason, entityData);
   }

   public void setVillagerData(VillagerData villagerData) {
      VillagerData villagerData2 = this.getVillagerData();
      if (!villagerData2.profession().equals(villagerData.profession())) {
         this.offerData = null;
      }

      this.dataTracker.set(VILLAGER_DATA, villagerData);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.dataTracker.get(VILLAGER_DATA);
   }

   public int getExperience() {
      return this.experience;
   }

   public void setExperience(int experience) {
      this.experience = experience;
   }

   @Nullable
   public Object get(ComponentType type) {
      return type == DataComponentTypes.VILLAGER_VARIANT ? castComponentValue(type, this.getVillagerData().type()) : super.get(type);
   }

   protected void copyComponentsFrom(ComponentsAccess from) {
      this.copyComponentFrom(from, DataComponentTypes.VILLAGER_VARIANT);
      super.copyComponentsFrom(from);
   }

   protected boolean setApplicableComponent(ComponentType type, Object value) {
      if (type == DataComponentTypes.VILLAGER_VARIANT) {
         RegistryEntry registryEntry = (RegistryEntry)castComponentValue(DataComponentTypes.VILLAGER_VARIANT, value);
         this.setVillagerData(this.getVillagerData().withType(registryEntry));
         return true;
      } else {
         return super.setApplicableComponent(type, value);
      }
   }

   static {
      CONVERTING = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
      VILLAGER_DATA = DataTracker.registerData(ZombieVillagerEntity.class, TrackedDataHandlerRegistry.VILLAGER_DATA);
   }
}
