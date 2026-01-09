package net.minecraft.enchantment;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

public class EnchantmentHelper {
   public static int getLevel(RegistryEntry enchantment, ItemStack stack) {
      ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
      return itemEnchantmentsComponent.getLevel(enchantment);
   }

   public static ItemEnchantmentsComponent apply(ItemStack stack, java.util.function.Consumer applier) {
      ComponentType componentType = getEnchantmentsComponentType(stack);
      ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)stack.get(componentType);
      if (itemEnchantmentsComponent == null) {
         return ItemEnchantmentsComponent.DEFAULT;
      } else {
         ItemEnchantmentsComponent.Builder builder = new ItemEnchantmentsComponent.Builder(itemEnchantmentsComponent);
         applier.accept(builder);
         ItemEnchantmentsComponent itemEnchantmentsComponent2 = builder.build();
         stack.set(componentType, itemEnchantmentsComponent2);
         return itemEnchantmentsComponent2;
      }
   }

   public static boolean canHaveEnchantments(ItemStack stack) {
      return stack.contains(getEnchantmentsComponentType(stack));
   }

   public static void set(ItemStack stack, ItemEnchantmentsComponent enchantments) {
      stack.set(getEnchantmentsComponentType(stack), enchantments);
   }

   public static ItemEnchantmentsComponent getEnchantments(ItemStack stack) {
      return (ItemEnchantmentsComponent)stack.getOrDefault(getEnchantmentsComponentType(stack), ItemEnchantmentsComponent.DEFAULT);
   }

   public static ComponentType getEnchantmentsComponentType(ItemStack stack) {
      return stack.isOf(Items.ENCHANTED_BOOK) ? DataComponentTypes.STORED_ENCHANTMENTS : DataComponentTypes.ENCHANTMENTS;
   }

   public static boolean hasEnchantments(ItemStack stack) {
      return !((ItemEnchantmentsComponent)stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)).isEmpty() || !((ItemEnchantmentsComponent)stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT)).isEmpty();
   }

   public static int getItemDamage(ServerWorld world, ItemStack stack, int baseItemDamage) {
      MutableFloat mutableFloat = new MutableFloat((float)baseItemDamage);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyItemDamage(world, level, stack, mutableFloat);
      });
      return mutableFloat.intValue();
   }

   public static int getAmmoUse(ServerWorld world, ItemStack rangedWeaponStack, ItemStack projectileStack, int baseAmmoUse) {
      MutableFloat mutableFloat = new MutableFloat((float)baseAmmoUse);
      forEachEnchantment(rangedWeaponStack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyAmmoUse(world, level, projectileStack, mutableFloat);
      });
      return mutableFloat.intValue();
   }

   public static int getBlockExperience(ServerWorld world, ItemStack stack, int baseBlockExperience) {
      MutableFloat mutableFloat = new MutableFloat((float)baseBlockExperience);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyBlockExperience(world, level, stack, mutableFloat);
      });
      return mutableFloat.intValue();
   }

   public static int getMobExperience(ServerWorld world, @Nullable Entity attacker, Entity mob, int baseMobExperience) {
      if (attacker instanceof LivingEntity livingEntity) {
         MutableFloat mutableFloat = new MutableFloat((float)baseMobExperience);
         forEachEnchantment(livingEntity, (enchantment, level, context) -> {
            ((Enchantment)enchantment.value()).modifyMobExperience(world, level, context.stack(), mob, mutableFloat);
         });
         return mutableFloat.intValue();
      } else {
         return baseMobExperience;
      }
   }

   public static ItemStack getEnchantedBookWith(EnchantmentLevelEntry entry) {
      ItemStack itemStack = new ItemStack(Items.ENCHANTED_BOOK);
      itemStack.addEnchantment(entry.enchantment(), entry.level());
      return itemStack;
   }

   public static void forEachEnchantment(ItemStack stack, Consumer consumer) {
      ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
      Iterator var3 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry entry = (Object2IntMap.Entry)var3.next();
         consumer.accept((RegistryEntry)entry.getKey(), entry.getIntValue());
      }

   }

   public static void forEachEnchantment(ItemStack stack, EquipmentSlot slot, LivingEntity entity, ContextAwareConsumer contextAwareConsumer) {
      if (!stack.isEmpty()) {
         ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)stack.get(DataComponentTypes.ENCHANTMENTS);
         if (itemEnchantmentsComponent != null && !itemEnchantmentsComponent.isEmpty()) {
            EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(stack, slot, entity);
            Iterator var6 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

            while(var6.hasNext()) {
               Object2IntMap.Entry entry = (Object2IntMap.Entry)var6.next();
               RegistryEntry registryEntry = (RegistryEntry)entry.getKey();
               if (((Enchantment)registryEntry.value()).slotMatches(slot)) {
                  contextAwareConsumer.accept(registryEntry, entry.getIntValue(), enchantmentEffectContext);
               }
            }

         }
      }
   }

   public static void forEachEnchantment(LivingEntity entity, ContextAwareConsumer contextAwareConsumer) {
      Iterator var2 = EquipmentSlot.VALUES.iterator();

      while(var2.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var2.next();
         forEachEnchantment(entity.getEquippedStack(equipmentSlot), equipmentSlot, entity, contextAwareConsumer);
      }

   }

   public static boolean isInvulnerableTo(ServerWorld world, LivingEntity user, DamageSource damageSource) {
      MutableBoolean mutableBoolean = new MutableBoolean();
      forEachEnchantment(user, (enchantment, level, context) -> {
         mutableBoolean.setValue(mutableBoolean.isTrue() || ((Enchantment)enchantment.value()).hasDamageImmunityTo(world, level, user, damageSource));
      });
      return mutableBoolean.isTrue();
   }

   public static float getProtectionAmount(ServerWorld world, LivingEntity user, DamageSource damageSource) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      forEachEnchantment(user, (enchantment, level, context) -> {
         ((Enchantment)enchantment.value()).modifyDamageProtection(world, level, context.stack(), user, damageSource, mutableFloat);
      });
      return mutableFloat.floatValue();
   }

   public static float getDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseDamage) {
      MutableFloat mutableFloat = new MutableFloat(baseDamage);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyDamage(world, level, stack, target, damageSource, mutableFloat);
      });
      return mutableFloat.floatValue();
   }

   public static float getSmashDamagePerFallenBlock(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseSmashDamagePerFallenBlock) {
      MutableFloat mutableFloat = new MutableFloat(baseSmashDamagePerFallenBlock);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifySmashDamagePerFallenBlock(world, level, stack, target, damageSource, mutableFloat);
      });
      return mutableFloat.floatValue();
   }

   public static float getArmorEffectiveness(ServerWorld world, ItemStack stack, Entity user, DamageSource damageSource, float baseArmorEffectiveness) {
      MutableFloat mutableFloat = new MutableFloat(baseArmorEffectiveness);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyArmorEffectiveness(world, level, stack, user, damageSource, mutableFloat);
      });
      return mutableFloat.floatValue();
   }

   public static float modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseKnockback) {
      MutableFloat mutableFloat = new MutableFloat(baseKnockback);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyKnockback(world, level, stack, target, damageSource, mutableFloat);
      });
      return mutableFloat.floatValue();
   }

   public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource damageSource) {
      Entity var4 = damageSource.getAttacker();
      if (var4 instanceof LivingEntity livingEntity) {
         onTargetDamaged(world, target, damageSource, livingEntity.getWeaponStack());
      } else {
         onTargetDamaged(world, target, damageSource, (ItemStack)null);
      }

   }

   public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource damageSource, @Nullable ItemStack weapon) {
      onTargetDamaged(world, target, damageSource, weapon, (java.util.function.Consumer)null);
   }

   public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource damageSource, @Nullable ItemStack weapon, @Nullable java.util.function.Consumer breakCallback) {
      if (target instanceof LivingEntity livingEntity) {
         forEachEnchantment(livingEntity, (enchantment, level, context) -> {
            ((Enchantment)enchantment.value()).onTargetDamaged(world, level, context, EnchantmentEffectTarget.VICTIM, target, damageSource);
         });
      }

      if (weapon != null) {
         Entity var6 = damageSource.getAttacker();
         if (var6 instanceof LivingEntity) {
            livingEntity = (LivingEntity)var6;
            forEachEnchantment(weapon, EquipmentSlot.MAINHAND, livingEntity, (enchantment, level, context) -> {
               ((Enchantment)enchantment.value()).onTargetDamaged(world, level, context, EnchantmentEffectTarget.ATTACKER, target, damageSource);
            });
         } else if (breakCallback != null) {
            EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(weapon, (EquipmentSlot)null, (LivingEntity)null, breakCallback);
            forEachEnchantment(weapon, (enchantment, level) -> {
               ((Enchantment)enchantment.value()).onTargetDamaged(world, level, enchantmentEffectContext, EnchantmentEffectTarget.ATTACKER, target, damageSource);
            });
         }
      }

   }

   public static void applyLocationBasedEffects(ServerWorld world, LivingEntity user) {
      forEachEnchantment(user, (enchantment, level, context) -> {
         ((Enchantment)enchantment.value()).applyLocationBasedEffects(world, level, context, user);
      });
   }

   public static void applyLocationBasedEffects(ServerWorld world, ItemStack stack, LivingEntity user, EquipmentSlot slot) {
      forEachEnchantment(stack, slot, user, (enchantment, level, context) -> {
         ((Enchantment)enchantment.value()).applyLocationBasedEffects(world, level, context, user);
      });
   }

   public static void removeLocationBasedEffects(LivingEntity user) {
      forEachEnchantment(user, (enchantment, level, context) -> {
         ((Enchantment)enchantment.value()).removeLocationBasedEffects(level, context, user);
      });
   }

   public static void removeLocationBasedEffects(ItemStack stack, LivingEntity user, EquipmentSlot slot) {
      forEachEnchantment(stack, slot, user, (enchantment, level, context) -> {
         ((Enchantment)enchantment.value()).removeLocationBasedEffects(level, context, user);
      });
   }

   public static void onTick(ServerWorld world, LivingEntity user) {
      forEachEnchantment(user, (enchantment, level, context) -> {
         ((Enchantment)enchantment.value()).onTick(world, level, context, user);
      });
   }

   public static int getEquipmentLevel(RegistryEntry enchantment, LivingEntity entity) {
      Iterable iterable = ((Enchantment)enchantment.value()).getEquipment(entity).values();
      int i = 0;
      Iterator var4 = iterable.iterator();

      while(var4.hasNext()) {
         ItemStack itemStack = (ItemStack)var4.next();
         int j = getLevel(enchantment, itemStack);
         if (j > i) {
            i = j;
         }
      }

      return i;
   }

   public static int getProjectileCount(ServerWorld world, ItemStack stack, Entity user, int baseProjectileCount) {
      MutableFloat mutableFloat = new MutableFloat((float)baseProjectileCount);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyProjectileCount(world, level, stack, user, mutableFloat);
      });
      return Math.max(0, mutableFloat.intValue());
   }

   public static float getProjectileSpread(ServerWorld world, ItemStack stack, Entity user, float baseProjectileSpread) {
      MutableFloat mutableFloat = new MutableFloat(baseProjectileSpread);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyProjectileSpread(world, level, stack, user, mutableFloat);
      });
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static int getProjectilePiercing(ServerWorld world, ItemStack weaponStack, ItemStack projectileStack) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      forEachEnchantment(weaponStack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyProjectilePiercing(world, level, projectileStack, mutableFloat);
      });
      return Math.max(0, mutableFloat.intValue());
   }

   public static void onProjectileSpawned(ServerWorld world, ItemStack weaponStack, ProjectileEntity projectile, java.util.function.Consumer onBreak) {
      Entity var6 = projectile.getOwner();
      LivingEntity var10000;
      if (var6 instanceof LivingEntity livingEntity) {
         var10000 = livingEntity;
      } else {
         var10000 = null;
      }

      LivingEntity livingEntity2 = var10000;
      EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(weaponStack, (EquipmentSlot)null, livingEntity2, onBreak);
      forEachEnchantment(weaponStack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).onProjectileSpawned(world, level, enchantmentEffectContext, projectile);
      });
   }

   public static void onHitBlock(ServerWorld world, ItemStack stack, @Nullable LivingEntity user, Entity enchantedEntity, @Nullable EquipmentSlot slot, Vec3d pos, BlockState state, java.util.function.Consumer onBreak) {
      EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(stack, slot, user, onBreak);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).onHitBlock(world, level, enchantmentEffectContext, enchantedEntity, pos, state);
      });
   }

   public static int getRepairWithExperience(ServerWorld world, ItemStack stack, int baseRepairWithExperience) {
      MutableFloat mutableFloat = new MutableFloat((float)baseRepairWithExperience);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyRepairWithExperience(world, level, stack, mutableFloat);
      });
      return Math.max(0, mutableFloat.intValue());
   }

   public static float getEquipmentDropChance(ServerWorld world, LivingEntity attacker, DamageSource damageSource, float baseEquipmentDropChance) {
      MutableFloat mutableFloat = new MutableFloat(baseEquipmentDropChance);
      Random random = attacker.getRandom();
      forEachEnchantment(attacker, (enchantment, level, context) -> {
         LootContext lootContext = Enchantment.createEnchantedDamageLootContext(world, level, attacker, damageSource);
         ((Enchantment)enchantment.value()).getEffect(EnchantmentEffectComponentTypes.EQUIPMENT_DROPS).forEach((effect) -> {
            if (effect.enchanted() == EnchantmentEffectTarget.VICTIM && effect.affected() == EnchantmentEffectTarget.VICTIM && effect.test(lootContext)) {
               mutableFloat.setValue(((EnchantmentValueEffect)effect.effect()).apply(level, random, mutableFloat.floatValue()));
            }

         });
      });
      Entity entity = damageSource.getAttacker();
      if (entity instanceof LivingEntity livingEntity) {
         forEachEnchantment(livingEntity, (enchantment, level, context) -> {
            LootContext lootContext = Enchantment.createEnchantedDamageLootContext(world, level, attacker, damageSource);
            ((Enchantment)enchantment.value()).getEffect(EnchantmentEffectComponentTypes.EQUIPMENT_DROPS).forEach((effect) -> {
               if (effect.enchanted() == EnchantmentEffectTarget.ATTACKER && effect.affected() == EnchantmentEffectTarget.VICTIM && effect.test(lootContext)) {
                  mutableFloat.setValue(((EnchantmentValueEffect)effect.effect()).apply(level, random, mutableFloat.floatValue()));
               }

            });
         });
      }

      return mutableFloat.floatValue();
   }

   public static void applyAttributeModifiers(ItemStack stack, AttributeModifierSlot slot, BiConsumer attributeModifierConsumer) {
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach((effect) -> {
            if (((Enchantment)enchantment.value()).definition().slots().contains(slot)) {
               attributeModifierConsumer.accept(effect.attribute(), effect.createAttributeModifier(level, slot));
            }

         });
      });
   }

   public static void applyAttributeModifiers(ItemStack stack, EquipmentSlot slot, BiConsumer attributeModifierConsumer) {
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach((effect) -> {
            if (((Enchantment)enchantment.value()).slotMatches(slot)) {
               attributeModifierConsumer.accept(effect.attribute(), effect.createAttributeModifier(level, slot));
            }

         });
      });
   }

   public static int getFishingLuckBonus(ServerWorld world, ItemStack stack, Entity user) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyFishingLuckBonus(world, level, stack, user, mutableFloat);
      });
      return Math.max(0, mutableFloat.intValue());
   }

   public static float getFishingTimeReduction(ServerWorld world, ItemStack stack, Entity user) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyFishingTimeReduction(world, level, stack, user, mutableFloat);
      });
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static int getTridentReturnAcceleration(ServerWorld world, ItemStack stack, Entity user) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyTridentReturnAcceleration(world, level, stack, user, mutableFloat);
      });
      return Math.max(0, mutableFloat.intValue());
   }

   public static float getCrossbowChargeTime(ItemStack stack, LivingEntity user, float baseCrossbowChargeTime) {
      MutableFloat mutableFloat = new MutableFloat(baseCrossbowChargeTime);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyCrossbowChargeTime(user.getRandom(), level, mutableFloat);
      });
      return Math.max(0.0F, mutableFloat.floatValue());
   }

   public static float getTridentSpinAttackStrength(ItemStack stack, LivingEntity user) {
      MutableFloat mutableFloat = new MutableFloat(0.0F);
      forEachEnchantment(stack, (enchantment, level) -> {
         ((Enchantment)enchantment.value()).modifyTridentSpinAttackStrength(user.getRandom(), level, mutableFloat);
      });
      return mutableFloat.floatValue();
   }

   public static boolean hasAnyEnchantmentsIn(ItemStack stack, TagKey tag) {
      ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
      Iterator var3 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

      RegistryEntry registryEntry;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         Object2IntMap.Entry entry = (Object2IntMap.Entry)var3.next();
         registryEntry = (RegistryEntry)entry.getKey();
      } while(!registryEntry.isIn(tag));

      return true;
   }

   public static boolean hasAnyEnchantmentsWith(ItemStack stack, ComponentType componentType) {
      MutableBoolean mutableBoolean = new MutableBoolean(false);
      forEachEnchantment(stack, (enchantment, level) -> {
         if (((Enchantment)enchantment.value()).effects().contains(componentType)) {
            mutableBoolean.setTrue();
         }

      });
      return mutableBoolean.booleanValue();
   }

   public static Optional getEffect(ItemStack stack, ComponentType componentType) {
      Pair pair = getHighestLevelEffect(stack, componentType);
      if (pair != null) {
         List list = (List)pair.getFirst();
         int i = (Integer)pair.getSecond();
         return Optional.of(list.get(Math.min(i, list.size()) - 1));
      } else {
         return Optional.empty();
      }
   }

   @Nullable
   public static Pair getHighestLevelEffect(ItemStack stack, ComponentType componentType) {
      MutableObject mutableObject = new MutableObject();
      forEachEnchantment(stack, (enchantment, level) -> {
         if (mutableObject.getValue() == null || (Integer)((Pair)mutableObject.getValue()).getSecond() < level) {
            Object object = ((Enchantment)enchantment.value()).effects().get(componentType);
            if (object != null) {
               mutableObject.setValue(Pair.of(object, level));
            }
         }

      });
      return (Pair)mutableObject.getValue();
   }

   public static Optional chooseEquipmentWith(ComponentType componentType, LivingEntity entity, Predicate stackPredicate) {
      List list = new ArrayList();
      Iterator var4 = EquipmentSlot.VALUES.iterator();

      while(true) {
         EquipmentSlot equipmentSlot;
         ItemStack itemStack;
         do {
            if (!var4.hasNext()) {
               return Util.getRandomOrEmpty(list, entity.getRandom());
            }

            equipmentSlot = (EquipmentSlot)var4.next();
            itemStack = entity.getEquippedStack(equipmentSlot);
         } while(!stackPredicate.test(itemStack));

         ItemEnchantmentsComponent itemEnchantmentsComponent = (ItemEnchantmentsComponent)itemStack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
         Iterator var8 = itemEnchantmentsComponent.getEnchantmentEntries().iterator();

         while(var8.hasNext()) {
            Object2IntMap.Entry entry = (Object2IntMap.Entry)var8.next();
            RegistryEntry registryEntry = (RegistryEntry)entry.getKey();
            if (((Enchantment)registryEntry.value()).effects().contains(componentType) && ((Enchantment)registryEntry.value()).slotMatches(equipmentSlot)) {
               list.add(new EnchantmentEffectContext(itemStack, equipmentSlot, entity));
            }
         }
      }
   }

   public static int calculateRequiredExperienceLevel(Random random, int slotIndex, int bookshelfCount, ItemStack stack) {
      EnchantableComponent enchantableComponent = (EnchantableComponent)stack.get(DataComponentTypes.ENCHANTABLE);
      if (enchantableComponent == null) {
         return 0;
      } else {
         if (bookshelfCount > 15) {
            bookshelfCount = 15;
         }

         int i = random.nextInt(8) + 1 + (bookshelfCount >> 1) + random.nextInt(bookshelfCount + 1);
         if (slotIndex == 0) {
            return Math.max(i / 3, 1);
         } else {
            return slotIndex == 1 ? i * 2 / 3 + 1 : Math.max(i, bookshelfCount * 2);
         }
      }
   }

   public static ItemStack enchant(Random random, ItemStack stack, int level, DynamicRegistryManager dynamicRegistryManager, Optional enchantments) {
      return enchant(random, stack, level, (Stream)enchantments.map(RegistryEntryList::stream).orElseGet(() -> {
         return dynamicRegistryManager.getOrThrow(RegistryKeys.ENCHANTMENT).streamEntries().map((reference) -> {
            return reference;
         });
      }));
   }

   public static ItemStack enchant(Random random, ItemStack stack, int level, Stream possibleEnchantments) {
      List list = generateEnchantments(random, stack, level, possibleEnchantments);
      if (stack.isOf(Items.BOOK)) {
         stack = new ItemStack(Items.ENCHANTED_BOOK);
      }

      Iterator var5 = list.iterator();

      while(var5.hasNext()) {
         EnchantmentLevelEntry enchantmentLevelEntry = (EnchantmentLevelEntry)var5.next();
         stack.addEnchantment(enchantmentLevelEntry.enchantment(), enchantmentLevelEntry.level());
      }

      return stack;
   }

   public static List generateEnchantments(Random random, ItemStack stack, int level, Stream possibleEnchantments) {
      List list = Lists.newArrayList();
      EnchantableComponent enchantableComponent = (EnchantableComponent)stack.get(DataComponentTypes.ENCHANTABLE);
      if (enchantableComponent == null) {
         return list;
      } else {
         level += 1 + random.nextInt(enchantableComponent.value() / 4 + 1) + random.nextInt(enchantableComponent.value() / 4 + 1);
         float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
         level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
         List list2 = getPossibleEntries(level, stack, possibleEnchantments);
         if (!list2.isEmpty()) {
            Optional var10000 = Weighting.getRandom(random, list2, EnchantmentLevelEntry::getWeight);
            Objects.requireNonNull(list);
            var10000.ifPresent(list::add);

            while(random.nextInt(50) <= level) {
               if (!list.isEmpty()) {
                  removeConflicts(list2, (EnchantmentLevelEntry)Util.getLast(list));
               }

               if (list2.isEmpty()) {
                  break;
               }

               var10000 = Weighting.getRandom(random, list2, EnchantmentLevelEntry::getWeight);
               Objects.requireNonNull(list);
               var10000.ifPresent(list::add);
               level /= 2;
            }
         }

         return list;
      }
   }

   public static void removeConflicts(List possibleEntries, EnchantmentLevelEntry pickedEntry) {
      possibleEntries.removeIf((entry) -> {
         return !Enchantment.canBeCombined(pickedEntry.enchantment(), entry.enchantment());
      });
   }

   public static boolean isCompatible(Collection existing, RegistryEntry candidate) {
      Iterator var2 = existing.iterator();

      RegistryEntry registryEntry;
      do {
         if (!var2.hasNext()) {
            return true;
         }

         registryEntry = (RegistryEntry)var2.next();
      } while(Enchantment.canBeCombined(registryEntry, candidate));

      return false;
   }

   public static List getPossibleEntries(int level, ItemStack stack, Stream possibleEnchantments) {
      List list = Lists.newArrayList();
      boolean bl = stack.isOf(Items.BOOK);
      possibleEnchantments.filter((enchantment) -> {
         return ((Enchantment)enchantment.value()).isPrimaryItem(stack) || bl;
      }).forEach((enchantmentx) -> {
         Enchantment enchantment = (Enchantment)enchantmentx.value();

         for(int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); --j) {
            if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
               list.add(new EnchantmentLevelEntry(enchantmentx, j));
               break;
            }
         }

      });
      return list;
   }

   public static void applyEnchantmentProvider(ItemStack stack, DynamicRegistryManager registryManager, RegistryKey providerKey, LocalDifficulty localDifficulty, Random random) {
      EnchantmentProvider enchantmentProvider = (EnchantmentProvider)registryManager.getOrThrow(RegistryKeys.ENCHANTMENT_PROVIDER).get(providerKey);
      if (enchantmentProvider != null) {
         apply(stack, (componentBuilder) -> {
            enchantmentProvider.provideEnchantments(stack, componentBuilder, random, localDifficulty);
         });
      }

   }

   @FunctionalInterface
   public interface Consumer {
      void accept(RegistryEntry enchantment, int level);
   }

   @FunctionalInterface
   public interface ContextAwareConsumer {
      void accept(RegistryEntry enchantment, int level, EnchantmentEffectContext context);
   }
}
