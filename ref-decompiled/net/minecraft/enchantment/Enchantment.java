package net.minecraft.enchantment;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.effect.TargetedEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;

public record Enchantment(Text description, Definition definition, RegistryEntryList exclusiveSet, ComponentMap effects) {
   public static final int MAX_LEVEL = 255;
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(TextCodecs.CODEC.fieldOf("description").forGetter(Enchantment::description), Enchantment.Definition.CODEC.forGetter(Enchantment::definition), RegistryCodecs.entryList(RegistryKeys.ENCHANTMENT).optionalFieldOf("exclusive_set", RegistryEntryList.of()).forGetter(Enchantment::exclusiveSet), EnchantmentEffectComponentTypes.COMPONENT_MAP_CODEC.optionalFieldOf("effects", ComponentMap.EMPTY).forGetter(Enchantment::effects)).apply(instance, Enchantment::new);
   });
   public static final Codec ENTRY_CODEC;
   public static final PacketCodec ENTRY_PACKET_CODEC;

   public Enchantment(Text text, Definition definition, RegistryEntryList registryEntryList, ComponentMap componentMap) {
      this.description = text;
      this.definition = definition;
      this.exclusiveSet = registryEntryList;
      this.effects = componentMap;
   }

   public static Cost constantCost(int base) {
      return new Cost(base, 0);
   }

   public static Cost leveledCost(int base, int perLevel) {
      return new Cost(base, perLevel);
   }

   public static Definition definition(RegistryEntryList supportedItems, RegistryEntryList primaryItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, AttributeModifierSlot... slots) {
      return new Definition(supportedItems, Optional.of(primaryItems), weight, maxLevel, minCost, maxCost, anvilCost, List.of(slots));
   }

   public static Definition definition(RegistryEntryList supportedItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, AttributeModifierSlot... slots) {
      return new Definition(supportedItems, Optional.empty(), weight, maxLevel, minCost, maxCost, anvilCost, List.of(slots));
   }

   public Map getEquipment(LivingEntity entity) {
      Map map = Maps.newEnumMap(EquipmentSlot.class);
      Iterator var3 = EquipmentSlot.VALUES.iterator();

      while(var3.hasNext()) {
         EquipmentSlot equipmentSlot = (EquipmentSlot)var3.next();
         if (this.slotMatches(equipmentSlot)) {
            ItemStack itemStack = entity.getEquippedStack(equipmentSlot);
            if (!itemStack.isEmpty()) {
               map.put(equipmentSlot, itemStack);
            }
         }
      }

      return map;
   }

   public RegistryEntryList getApplicableItems() {
      return this.definition.supportedItems();
   }

   public boolean slotMatches(EquipmentSlot slot) {
      return this.definition.slots().stream().anyMatch((slotx) -> {
         return slotx.matches(slot);
      });
   }

   public boolean isPrimaryItem(ItemStack stack) {
      return this.isSupportedItem(stack) && (this.definition.primaryItems.isEmpty() || stack.isIn((RegistryEntryList)this.definition.primaryItems.get()));
   }

   public boolean isSupportedItem(ItemStack stack) {
      return stack.isIn(this.definition.supportedItems);
   }

   public int getWeight() {
      return this.definition.weight();
   }

   public int getAnvilCost() {
      return this.definition.anvilCost();
   }

   public int getMinLevel() {
      return 1;
   }

   public int getMaxLevel() {
      return this.definition.maxLevel();
   }

   public int getMinPower(int level) {
      return this.definition.minCost().forLevel(level);
   }

   public int getMaxPower(int level) {
      return this.definition.maxCost().forLevel(level);
   }

   public String toString() {
      return "Enchantment " + this.description.getString();
   }

   public static boolean canBeCombined(RegistryEntry first, RegistryEntry second) {
      return !first.equals(second) && !((Enchantment)first.value()).exclusiveSet.contains(second) && !((Enchantment)second.value()).exclusiveSet.contains(first);
   }

   public static Text getName(RegistryEntry enchantment, int level) {
      MutableText mutableText = ((Enchantment)enchantment.value()).description.copy();
      if (enchantment.isIn(EnchantmentTags.CURSE)) {
         Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.RED));
      } else {
         Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.GRAY));
      }

      if (level != 1 || ((Enchantment)enchantment.value()).getMaxLevel() != 1) {
         mutableText.append(ScreenTexts.SPACE).append((Text)Text.translatable("enchantment.level." + level));
      }

      return mutableText;
   }

   public boolean isAcceptableItem(ItemStack stack) {
      return this.definition.supportedItems().contains(stack.getRegistryEntry());
   }

   public List getEffect(ComponentType type) {
      return (List)this.effects.getOrDefault(type, List.of());
   }

   public boolean hasDamageImmunityTo(ServerWorld world, int level, Entity user, DamageSource damageSource) {
      LootContext lootContext = createEnchantedDamageLootContext(world, level, user, damageSource);
      Iterator var6 = this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_IMMUNITY).iterator();

      EnchantmentEffectEntry enchantmentEffectEntry;
      do {
         if (!var6.hasNext()) {
            return false;
         }

         enchantmentEffectEntry = (EnchantmentEffectEntry)var6.next();
      } while(!enchantmentEffectEntry.test(lootContext));

      return true;
   }

   public void modifyDamageProtection(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damageProtection) {
      LootContext lootContext = createEnchantedDamageLootContext(world, level, user, damageSource);
      Iterator var8 = this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_PROTECTION).iterator();

      while(var8.hasNext()) {
         EnchantmentEffectEntry enchantmentEffectEntry = (EnchantmentEffectEntry)var8.next();
         if (enchantmentEffectEntry.test(lootContext)) {
            damageProtection.setValue(((EnchantmentValueEffect)enchantmentEffectEntry.effect()).apply(level, user.getRandom(), damageProtection.floatValue()));
         }
      }

   }

   public void modifyItemDamage(ServerWorld world, int level, ItemStack stack, MutableFloat itemDamage) {
      this.modifyValue(EnchantmentEffectComponentTypes.ITEM_DAMAGE, world, level, stack, itemDamage);
   }

   public void modifyAmmoUse(ServerWorld world, int level, ItemStack projectileStack, MutableFloat ammoUse) {
      this.modifyValue(EnchantmentEffectComponentTypes.AMMO_USE, world, level, projectileStack, ammoUse);
   }

   public void modifyProjectilePiercing(ServerWorld world, int level, ItemStack stack, MutableFloat projectilePiercing) {
      this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_PIERCING, world, level, stack, projectilePiercing);
   }

   public void modifyBlockExperience(ServerWorld world, int level, ItemStack stack, MutableFloat blockExperience) {
      this.modifyValue(EnchantmentEffectComponentTypes.BLOCK_EXPERIENCE, world, level, stack, blockExperience);
   }

   public void modifyMobExperience(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat mobExperience) {
      this.modifyValue(EnchantmentEffectComponentTypes.MOB_EXPERIENCE, world, level, stack, user, mobExperience);
   }

   public void modifyRepairWithExperience(ServerWorld world, int level, ItemStack stack, MutableFloat repairWithExperience) {
      this.modifyValue(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, world, level, stack, repairWithExperience);
   }

   public void modifyTridentReturnAcceleration(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat tridentReturnAcceleration) {
      this.modifyValue(EnchantmentEffectComponentTypes.TRIDENT_RETURN_ACCELERATION, world, level, stack, user, tridentReturnAcceleration);
   }

   public void modifyTridentSpinAttackStrength(Random random, int level, MutableFloat tridentSpinAttackStrength) {
      this.modifyValue(EnchantmentEffectComponentTypes.TRIDENT_SPIN_ATTACK_STRENGTH, random, level, tridentSpinAttackStrength);
   }

   public void modifyFishingTimeReduction(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat fishingTimeReduction) {
      this.modifyValue(EnchantmentEffectComponentTypes.FISHING_TIME_REDUCTION, world, level, stack, user, fishingTimeReduction);
   }

   public void modifyFishingLuckBonus(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat fishingLuckBonus) {
      this.modifyValue(EnchantmentEffectComponentTypes.FISHING_LUCK_BONUS, world, level, stack, user, fishingLuckBonus);
   }

   public void modifyDamage(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damage) {
      this.modifyValue(EnchantmentEffectComponentTypes.DAMAGE, world, level, stack, user, damageSource, damage);
   }

   public void modifySmashDamagePerFallenBlock(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat smashDamagePerFallenBlock) {
      this.modifyValue(EnchantmentEffectComponentTypes.SMASH_DAMAGE_PER_FALLEN_BLOCK, world, level, stack, user, damageSource, smashDamagePerFallenBlock);
   }

   public void modifyKnockback(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat knockback) {
      this.modifyValue(EnchantmentEffectComponentTypes.KNOCKBACK, world, level, stack, user, damageSource, knockback);
   }

   public void modifyArmorEffectiveness(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat armorEffectiveness) {
      this.modifyValue(EnchantmentEffectComponentTypes.ARMOR_EFFECTIVENESS, world, level, stack, user, damageSource, armorEffectiveness);
   }

   public void onTargetDamaged(ServerWorld world, int level, EnchantmentEffectContext context, EnchantmentEffectTarget target, Entity user, DamageSource damageSource) {
      Iterator var7 = this.getEffect(EnchantmentEffectComponentTypes.POST_ATTACK).iterator();

      while(var7.hasNext()) {
         TargetedEnchantmentEffect targetedEnchantmentEffect = (TargetedEnchantmentEffect)var7.next();
         if (target == targetedEnchantmentEffect.enchanted()) {
            applyTargetedEffect(targetedEnchantmentEffect, world, level, context, user, damageSource);
         }
      }

   }

   public static void applyTargetedEffect(TargetedEnchantmentEffect effect, ServerWorld world, int level, EnchantmentEffectContext context, Entity user, DamageSource damageSource) {
      if (effect.test(createEnchantedDamageLootContext(world, level, user, damageSource))) {
         Entity var10000;
         switch (effect.affected()) {
            case ATTACKER:
               var10000 = damageSource.getAttacker();
               break;
            case DAMAGING_ENTITY:
               var10000 = damageSource.getSource();
               break;
            case VICTIM:
               var10000 = user;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         Entity entity = var10000;
         if (entity != null) {
            ((EnchantmentEntityEffect)effect.effect()).apply(world, level, context, entity, entity.getPos());
         }
      }

   }

   public void modifyProjectileCount(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat projectileCount) {
      this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_COUNT, world, level, stack, user, projectileCount);
   }

   public void modifyProjectileSpread(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat projectileSpread) {
      this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_SPREAD, world, level, stack, user, projectileSpread);
   }

   public void modifyCrossbowChargeTime(Random random, int level, MutableFloat crossbowChargeTime) {
      this.modifyValue(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, random, level, crossbowChargeTime);
   }

   public void modifyValue(ComponentType type, Random random, int level, MutableFloat value) {
      EnchantmentValueEffect enchantmentValueEffect = (EnchantmentValueEffect)this.effects.get(type);
      if (enchantmentValueEffect != null) {
         value.setValue(enchantmentValueEffect.apply(level, random, value.floatValue()));
      }

   }

   public void onTick(ServerWorld world, int level, EnchantmentEffectContext context, Entity user) {
      applyEffects(this.getEffect(EnchantmentEffectComponentTypes.TICK), createEnchantedEntityLootContext(world, level, user, user.getPos()), (effect) -> {
         effect.apply(world, level, context, user, user.getPos());
      });
   }

   public void onProjectileSpawned(ServerWorld world, int level, EnchantmentEffectContext context, Entity user) {
      applyEffects(this.getEffect(EnchantmentEffectComponentTypes.PROJECTILE_SPAWNED), createEnchantedEntityLootContext(world, level, user, user.getPos()), (effect) -> {
         effect.apply(world, level, context, user, user.getPos());
      });
   }

   public void onHitBlock(ServerWorld world, int level, EnchantmentEffectContext context, Entity enchantedEntity, Vec3d pos, BlockState state) {
      applyEffects(this.getEffect(EnchantmentEffectComponentTypes.HIT_BLOCK), createHitBlockLootContext(world, level, enchantedEntity, pos, state), (effect) -> {
         effect.apply(world, level, context, enchantedEntity, pos);
      });
   }

   public final void modifyValue(ComponentType type, ServerWorld world, int level, ItemStack stack, MutableFloat value) {
      applyEffects(this.getEffect(type), createEnchantedItemLootContext(world, level, stack), (effect) -> {
         value.setValue(effect.apply(level, world.getRandom(), value.getValue()));
      });
   }

   public final void modifyValue(ComponentType type, ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat value) {
      applyEffects(this.getEffect(type), createEnchantedEntityLootContext(world, level, user, user.getPos()), (effect) -> {
         value.setValue(effect.apply(level, user.getRandom(), value.floatValue()));
      });
   }

   public final void modifyValue(ComponentType type, ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat value) {
      applyEffects(this.getEffect(type), createEnchantedDamageLootContext(world, level, user, damageSource), (effect) -> {
         value.setValue(effect.apply(level, user.getRandom(), value.floatValue()));
      });
   }

   public static LootContext createEnchantedDamageLootContext(ServerWorld world, int level, Entity entity, DamageSource damageSource) {
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ENCHANTMENT_LEVEL, level).add(LootContextParameters.ORIGIN, entity.getPos()).add(LootContextParameters.DAMAGE_SOURCE, damageSource).addOptional(LootContextParameters.ATTACKING_ENTITY, damageSource.getAttacker()).addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, damageSource.getSource()).build(LootContextTypes.ENCHANTED_DAMAGE);
      return (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
   }

   public static LootContext createEnchantedItemLootContext(ServerWorld world, int level, ItemStack stack) {
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).add(LootContextParameters.TOOL, stack).add(LootContextParameters.ENCHANTMENT_LEVEL, level).build(LootContextTypes.ENCHANTED_ITEM);
      return (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
   }

   public static LootContext createEnchantedLocationLootContext(ServerWorld world, int level, Entity entity, boolean enchantmentActive) {
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ENCHANTMENT_LEVEL, level).add(LootContextParameters.ORIGIN, entity.getPos()).add(LootContextParameters.ENCHANTMENT_ACTIVE, enchantmentActive).build(LootContextTypes.ENCHANTED_LOCATION);
      return (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
   }

   public static LootContext createEnchantedEntityLootContext(ServerWorld world, int level, Entity entity, Vec3d pos) {
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ENCHANTMENT_LEVEL, level).add(LootContextParameters.ORIGIN, pos).build(LootContextTypes.ENCHANTED_ENTITY);
      return (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
   }

   public static LootContext createHitBlockLootContext(ServerWorld world, int level, Entity entity, Vec3d pos, BlockState state) {
      LootWorldContext lootWorldContext = (new LootWorldContext.Builder(world)).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ENCHANTMENT_LEVEL, level).add(LootContextParameters.ORIGIN, pos).add(LootContextParameters.BLOCK_STATE, state).build(LootContextTypes.HIT_BLOCK);
      return (new LootContext.Builder(lootWorldContext)).build(Optional.empty());
   }

   public static void applyEffects(List entries, LootContext lootContext, Consumer effectConsumer) {
      Iterator var3 = entries.iterator();

      while(var3.hasNext()) {
         EnchantmentEffectEntry enchantmentEffectEntry = (EnchantmentEffectEntry)var3.next();
         if (enchantmentEffectEntry.test(lootContext)) {
            effectConsumer.accept(enchantmentEffectEntry.effect());
         }
      }

   }

   public void applyLocationBasedEffects(ServerWorld world, int level, EnchantmentEffectContext context, LivingEntity user) {
      EquipmentSlot equipmentSlot = context.slot();
      if (equipmentSlot != null) {
         Map map = user.getLocationBasedEnchantmentEffects(equipmentSlot);
         if (!this.slotMatches(equipmentSlot)) {
            Set set = (Set)map.remove(this);
            if (set != null) {
               set.forEach((effect) -> {
                  effect.remove(context, user, user.getPos(), level);
               });
            }

         } else {
            Set set = (Set)map.get(this);
            Iterator var8 = this.getEffect(EnchantmentEffectComponentTypes.LOCATION_CHANGED).iterator();

            while(var8.hasNext()) {
               EnchantmentEffectEntry enchantmentEffectEntry = (EnchantmentEffectEntry)var8.next();
               EnchantmentLocationBasedEffect enchantmentLocationBasedEffect = (EnchantmentLocationBasedEffect)enchantmentEffectEntry.effect();
               boolean bl = set != null && ((Set)set).contains(enchantmentLocationBasedEffect);
               if (enchantmentEffectEntry.test(createEnchantedLocationLootContext(world, level, user, bl))) {
                  if (!bl) {
                     if (set == null) {
                        set = new ObjectArraySet();
                        map.put(this, set);
                     }

                     ((Set)set).add(enchantmentLocationBasedEffect);
                  }

                  enchantmentLocationBasedEffect.apply(world, level, context, user, user.getPos(), !bl);
               } else if (set != null && ((Set)set).remove(enchantmentLocationBasedEffect)) {
                  enchantmentLocationBasedEffect.remove(context, user, user.getPos(), level);
               }
            }

            if (set != null && ((Set)set).isEmpty()) {
               map.remove(this);
            }

         }
      }
   }

   public void removeLocationBasedEffects(int level, EnchantmentEffectContext context, LivingEntity user) {
      EquipmentSlot equipmentSlot = context.slot();
      if (equipmentSlot != null) {
         Set set = (Set)user.getLocationBasedEnchantmentEffects(equipmentSlot).remove(this);
         if (set != null) {
            Iterator var6 = set.iterator();

            while(var6.hasNext()) {
               EnchantmentLocationBasedEffect enchantmentLocationBasedEffect = (EnchantmentLocationBasedEffect)var6.next();
               enchantmentLocationBasedEffect.remove(context, user, user.getPos(), level);
            }

         }
      }
   }

   public static Builder builder(Definition definition) {
      return new Builder(definition);
   }

   public Text description() {
      return this.description;
   }

   public Definition definition() {
      return this.definition;
   }

   public RegistryEntryList exclusiveSet() {
      return this.exclusiveSet;
   }

   public ComponentMap effects() {
      return this.effects;
   }

   static {
      ENTRY_CODEC = RegistryFixedCodec.of(RegistryKeys.ENCHANTMENT);
      ENTRY_PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ENCHANTMENT);
   }

   public static record Definition(RegistryEntryList supportedItems, Optional primaryItems, int weight, int maxLevel, Cost minCost, Cost maxCost, int anvilCost, List slots) {
      final RegistryEntryList supportedItems;
      final Optional primaryItems;
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("supported_items").forGetter(Definition::supportedItems), RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("primary_items").forGetter(Definition::primaryItems), Codecs.rangedInt(1, 1024).fieldOf("weight").forGetter(Definition::weight), Codecs.rangedInt(1, 255).fieldOf("max_level").forGetter(Definition::maxLevel), Enchantment.Cost.CODEC.fieldOf("min_cost").forGetter(Definition::minCost), Enchantment.Cost.CODEC.fieldOf("max_cost").forGetter(Definition::maxCost), Codecs.NON_NEGATIVE_INT.fieldOf("anvil_cost").forGetter(Definition::anvilCost), AttributeModifierSlot.CODEC.listOf().fieldOf("slots").forGetter(Definition::slots)).apply(instance, Definition::new);
      });

      public Definition(RegistryEntryList registryEntryList, Optional optional, int i, int j, Cost cost, Cost cost2, int k, List list) {
         this.supportedItems = registryEntryList;
         this.primaryItems = optional;
         this.weight = i;
         this.maxLevel = j;
         this.minCost = cost;
         this.maxCost = cost2;
         this.anvilCost = k;
         this.slots = list;
      }

      public RegistryEntryList supportedItems() {
         return this.supportedItems;
      }

      public Optional primaryItems() {
         return this.primaryItems;
      }

      public int weight() {
         return this.weight;
      }

      public int maxLevel() {
         return this.maxLevel;
      }

      public Cost minCost() {
         return this.minCost;
      }

      public Cost maxCost() {
         return this.maxCost;
      }

      public int anvilCost() {
         return this.anvilCost;
      }

      public List slots() {
         return this.slots;
      }
   }

   public static record Cost(int base, int perLevelAboveFirst) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Codec.INT.fieldOf("base").forGetter(Cost::base), Codec.INT.fieldOf("per_level_above_first").forGetter(Cost::perLevelAboveFirst)).apply(instance, Cost::new);
      });

      public Cost(int i, int j) {
         this.base = i;
         this.perLevelAboveFirst = j;
      }

      public int forLevel(int level) {
         return this.base + this.perLevelAboveFirst * (level - 1);
      }

      public int base() {
         return this.base;
      }

      public int perLevelAboveFirst() {
         return this.perLevelAboveFirst;
      }
   }

   public static class Builder {
      private final Definition definition;
      private RegistryEntryList exclusiveSet = RegistryEntryList.of();
      private final Map effectLists = new HashMap();
      private final ComponentMap.Builder effectMap = ComponentMap.builder();

      public Builder(Definition properties) {
         this.definition = properties;
      }

      public Builder exclusiveSet(RegistryEntryList exclusiveSet) {
         this.exclusiveSet = exclusiveSet;
         return this;
      }

      public Builder addEffect(ComponentType effectType, Object effect, LootCondition.Builder requirements) {
         this.getEffectsList(effectType).add(new EnchantmentEffectEntry(effect, Optional.of(requirements.build())));
         return this;
      }

      public Builder addEffect(ComponentType effectType, Object effect) {
         this.getEffectsList(effectType).add(new EnchantmentEffectEntry(effect, Optional.empty()));
         return this;
      }

      public Builder addEffect(ComponentType type, EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, Object effect, LootCondition.Builder requirements) {
         this.getEffectsList(type).add(new TargetedEnchantmentEffect(enchanted, affected, effect, Optional.of(requirements.build())));
         return this;
      }

      public Builder addEffect(ComponentType type, EnchantmentEffectTarget enchanted, EnchantmentEffectTarget affected, Object effect) {
         this.getEffectsList(type).add(new TargetedEnchantmentEffect(enchanted, affected, effect, Optional.empty()));
         return this;
      }

      public Builder addEffect(ComponentType type, AttributeEnchantmentEffect effect) {
         this.getEffectsList(type).add(effect);
         return this;
      }

      public Builder addNonListEffect(ComponentType type, Object effect) {
         this.effectMap.add(type, effect);
         return this;
      }

      public Builder addEffect(ComponentType type) {
         this.effectMap.add(type, Unit.INSTANCE);
         return this;
      }

      private List getEffectsList(ComponentType type) {
         return (List)this.effectLists.computeIfAbsent(type, (typex) -> {
            ArrayList arrayList = new ArrayList();
            this.effectMap.add(type, arrayList);
            return arrayList;
         });
      }

      public Enchantment build(Identifier id) {
         return new Enchantment(Text.translatable(Util.createTranslationKey("enchantment", id)), this.definition, this.exclusiveSet, this.effectMap.build());
      }
   }
}
