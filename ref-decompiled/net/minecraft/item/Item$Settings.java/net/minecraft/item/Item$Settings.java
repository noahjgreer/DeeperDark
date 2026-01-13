/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.item.v1.FabricItem$Settings
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.item;

import java.util.Objects;
import java.util.Optional;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.Block;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttackRangeComponent;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.KineticWeaponComponent;
import net.minecraft.component.type.PiercingWeaponComponent;
import net.minecraft.component.type.ProvidesTrimMaterialComponent;
import net.minecraft.component.type.RepairableComponent;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.component.type.UseCooldownComponent;
import net.minecraft.component.type.UseEffectsComponent;
import net.minecraft.component.type.UseRemainderComponent;
import net.minecraft.component.type.WeaponComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.entry.LazyRegistryEntryReference;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public static class Item.Settings
implements FabricItem.Settings {
    private static final RegistryKeyedValue<Item, String> BLOCK_PREFIXED_TRANSLATION_KEY = key -> Util.createTranslationKey("block", key.getValue());
    private static final RegistryKeyedValue<Item, String> ITEM_PREFIXED_TRANSLATION_KEY = key -> Util.createTranslationKey("item", key.getValue());
    private final ComponentMap.Builder components = ComponentMap.builder().addAll(DataComponentTypes.DEFAULT_ITEM_COMPONENTS);
    @Nullable Item recipeRemainder;
    FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
    private @Nullable RegistryKey<Item> registryKey;
    private RegistryKeyedValue<Item, String> translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
    private final RegistryKeyedValue<Item, Identifier> modelId = RegistryKey::getValue;

    public Item.Settings food(FoodComponent foodComponent) {
        return this.food(foodComponent, ConsumableComponents.FOOD);
    }

    public Item.Settings food(FoodComponent foodComponent, ConsumableComponent consumableComponent) {
        return this.component(DataComponentTypes.FOOD, foodComponent).component(DataComponentTypes.CONSUMABLE, consumableComponent);
    }

    public Item.Settings useRemainder(Item convertInto) {
        return this.component(DataComponentTypes.USE_REMAINDER, new UseRemainderComponent(new ItemStack(convertInto)));
    }

    public Item.Settings useCooldown(float seconds) {
        return this.component(DataComponentTypes.USE_COOLDOWN, new UseCooldownComponent(seconds));
    }

    public Item.Settings maxCount(int maxCount) {
        return this.component(DataComponentTypes.MAX_STACK_SIZE, maxCount);
    }

    public Item.Settings maxDamage(int maxDamage) {
        this.component(DataComponentTypes.MAX_DAMAGE, maxDamage);
        this.component(DataComponentTypes.MAX_STACK_SIZE, 1);
        this.component(DataComponentTypes.DAMAGE, 0);
        return this;
    }

    public Item.Settings recipeRemainder(Item recipeRemainder) {
        this.recipeRemainder = recipeRemainder;
        return this;
    }

    public Item.Settings rarity(Rarity rarity) {
        return this.component(DataComponentTypes.RARITY, rarity);
    }

    public Item.Settings fireproof() {
        return this.component(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(DamageTypeTags.IS_FIRE));
    }

    public Item.Settings jukeboxPlayable(RegistryKey<JukeboxSong> songKey) {
        return this.component(DataComponentTypes.JUKEBOX_PLAYABLE, new JukeboxPlayableComponent(new LazyRegistryEntryReference<JukeboxSong>(songKey)));
    }

    public Item.Settings enchantable(int enchantability) {
        return this.component(DataComponentTypes.ENCHANTABLE, new EnchantableComponent(enchantability));
    }

    public Item.Settings repairable(Item repairIngredient) {
        return this.component(DataComponentTypes.REPAIRABLE, new RepairableComponent(RegistryEntryList.of(repairIngredient.getRegistryEntry())));
    }

    public Item.Settings repairable(TagKey<Item> repairIngredientsTag) {
        RegistryEntryLookup<Item> registryEntryLookup = Registries.createEntryLookup(Registries.ITEM);
        return this.component(DataComponentTypes.REPAIRABLE, new RepairableComponent(registryEntryLookup.getOrThrow(repairIngredientsTag)));
    }

    public Item.Settings equippable(EquipmentSlot slot) {
        return this.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot).build());
    }

    public Item.Settings equippableUnswappable(EquipmentSlot slot) {
        return this.component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot).swappable(false).build());
    }

    public Item.Settings tool(ToolMaterial material, TagKey<Block> effectiveBlocks, float attackDamage, float attackSpeed, float disableBlockingForSeconds) {
        return material.applyToolSettings(this, effectiveBlocks, attackDamage, attackSpeed, disableBlockingForSeconds);
    }

    public Item.Settings pickaxe(ToolMaterial material, float attackDamage, float attackSpeed) {
        return this.tool(material, BlockTags.PICKAXE_MINEABLE, attackDamage, attackSpeed, 0.0f);
    }

    public Item.Settings axe(ToolMaterial material, float attackDamage, float attackSpeed) {
        return this.tool(material, BlockTags.AXE_MINEABLE, attackDamage, attackSpeed, 5.0f);
    }

    public Item.Settings hoe(ToolMaterial material, float attackDamage, float attackSpeed) {
        return this.tool(material, BlockTags.HOE_MINEABLE, attackDamage, attackSpeed, 0.0f);
    }

    public Item.Settings shovel(ToolMaterial material, float attackDamage, float attackSpeed) {
        return this.tool(material, BlockTags.SHOVEL_MINEABLE, attackDamage, attackSpeed, 0.0f);
    }

    public Item.Settings sword(ToolMaterial material, float attackDamage, float attackSpeed) {
        return material.applySwordSettings(this, attackDamage, attackSpeed);
    }

    public Item.Settings spear(ToolMaterial material, float swingAnimationSeconds, float chargeDamageMultiplier, float chargeDelaySeconds, float maxDurationForDismountSeconds, float minSpeedForDismount, float maxDurationForChargeKnockbackInSeconds, float minSpeedForChargeKnockback, float maxDurationForChargeDamageInSeconds, float minRelativeSpeedForChargeDamage) {
        return this.maxDamage(material.durability()).repairable(material.repairItems()).enchantable(material.enchantmentValue()).component(DataComponentTypes.DAMAGE_TYPE, new LazyRegistryEntryReference<DamageType>(DamageTypes.SPEAR)).component(DataComponentTypes.KINETIC_WEAPON, new KineticWeaponComponent(10, (int)(chargeDelaySeconds * 20.0f), KineticWeaponComponent.Condition.ofMinSpeed((int)(maxDurationForDismountSeconds * 20.0f), minSpeedForDismount), KineticWeaponComponent.Condition.ofMinSpeed((int)(maxDurationForChargeKnockbackInSeconds * 20.0f), minSpeedForChargeKnockback), KineticWeaponComponent.Condition.ofMinRelativeSpeed((int)(maxDurationForChargeDamageInSeconds * 20.0f), minRelativeSpeedForChargeDamage), 0.38f, chargeDamageMultiplier, Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_USE : SoundEvents.ITEM_SPEAR_USE), Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_HIT : SoundEvents.ITEM_SPEAR_HIT))).component(DataComponentTypes.PIERCING_WEAPON, new PiercingWeaponComponent(true, false, Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_ATTACK : SoundEvents.ITEM_SPEAR_ATTACK), Optional.of(material == ToolMaterial.WOOD ? SoundEvents.ITEM_SPEAR_WOOD_HIT : SoundEvents.ITEM_SPEAR_HIT))).component(DataComponentTypes.ATTACK_RANGE, new AttackRangeComponent(2.0f, 4.5f, 2.0f, 6.5f, 0.125f, 0.5f)).component(DataComponentTypes.MINIMUM_ATTACK_CHARGE, Float.valueOf(1.0f)).component(DataComponentTypes.SWING_ANIMATION, new SwingAnimationComponent(SwingAnimationType.STAB, (int)(swingAnimationSeconds * 20.0f))).attributeModifiers(AttributeModifiersComponent.builder().add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(BASE_ATTACK_DAMAGE_MODIFIER_ID, 0.0f + material.attackDamageBonus(), EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(BASE_ATTACK_SPEED_MODIFIER_ID, (double)(1.0f / swingAnimationSeconds) - 4.0, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND).build()).component(DataComponentTypes.USE_EFFECTS, new UseEffectsComponent(true, false, 1.0f)).component(DataComponentTypes.WEAPON, new WeaponComponent(1));
    }

    public Item.Settings spawnEgg(EntityType<?> entityType) {
        return this.component(DataComponentTypes.ENTITY_DATA, TypedEntityData.create(entityType, new NbtCompound()));
    }

    public Item.Settings armor(ArmorMaterial material, EquipmentType type) {
        return this.maxDamage(type.getMaxDamage(material.durability())).attributeModifiers(material.createAttributeModifiers(type)).enchantable(material.enchantmentValue()).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(type.getEquipmentSlot()).equipSound(material.equipSound()).model(material.assetId()).build()).repairable(material.repairIngredient());
    }

    public Item.Settings wolfArmor(ArmorMaterial material) {
        return this.maxDamage(EquipmentType.BODY.getMaxDamage(material.durability())).attributeModifiers(material.createAttributeModifiers(EquipmentType.BODY)).repairable(material.repairIngredient()).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.BODY).equipSound(material.equipSound()).model(material.assetId()).allowedEntities(RegistryEntryList.of(EntityType.WOLF.getRegistryEntry())).canBeSheared(true).shearingSound(Registries.SOUND_EVENT.getEntry(SoundEvents.ITEM_ARMOR_UNEQUIP_WOLF)).build()).component(DataComponentTypes.BREAK_SOUND, SoundEvents.ITEM_WOLF_ARMOR_BREAK).maxCount(1);
    }

    public Item.Settings horseArmor(ArmorMaterial material) {
        RegistryEntryLookup<EntityType<?>> registryEntryLookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);
        return this.attributeModifiers(material.createAttributeModifiers(EquipmentType.BODY)).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.BODY).equipSound(SoundEvents.ENTITY_HORSE_ARMOR).model(material.assetId()).allowedEntities(registryEntryLookup.getOrThrow(EntityTypeTags.CAN_WEAR_HORSE_ARMOR)).damageOnHurt(false).canBeSheared(true).shearingSound(SoundEvents.ITEM_HORSE_ARMOR_UNEQUIP).build()).maxCount(1);
    }

    public Item.Settings nautilusArmor(ArmorMaterial material) {
        RegistryEntryLookup<EntityType<?>> registryEntryLookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);
        return this.attributeModifiers(material.createAttributeModifiers(EquipmentType.BODY)).component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.BODY).equipSound(SoundEvents.ITEM_ARMOR_EQUIP_NAUTILUS).model(material.assetId()).allowedEntities(registryEntryLookup.getOrThrow(EntityTypeTags.CAN_WEAR_NAUTILUS_ARMOR)).damageOnHurt(false).equipOnInteract(true).canBeSheared(true).shearingSound(SoundEvents.ITEM_ARMOR_UNEQUIP_NAUTILUS).build()).maxCount(1);
    }

    public Item.Settings trimMaterial(RegistryKey<ArmorTrimMaterial> trimMaterial) {
        return this.component(DataComponentTypes.PROVIDES_TRIM_MATERIAL, new ProvidesTrimMaterialComponent(trimMaterial));
    }

    public Item.Settings requires(FeatureFlag ... features) {
        this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
        return this;
    }

    public Item.Settings registryKey(RegistryKey<Item> registryKey) {
        this.registryKey = registryKey;
        return this;
    }

    public Item.Settings translationKey(String translationKey) {
        this.translationKey = RegistryKeyedValue.fixed(translationKey);
        return this;
    }

    public Item.Settings useBlockPrefixedTranslationKey() {
        this.translationKey = BLOCK_PREFIXED_TRANSLATION_KEY;
        return this;
    }

    public Item.Settings useItemPrefixedTranslationKey() {
        this.translationKey = ITEM_PREFIXED_TRANSLATION_KEY;
        return this;
    }

    protected String getTranslationKey() {
        return this.translationKey.get(Objects.requireNonNull(this.registryKey, "Item id not set"));
    }

    public Identifier getModelId() {
        return this.modelId.get(Objects.requireNonNull(this.registryKey, "Item id not set"));
    }

    public <T> Item.Settings component(ComponentType<T> type, T value) {
        this.components.add(type, value);
        return this;
    }

    public Item.Settings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent) {
        return this.component(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributeModifiersComponent);
    }

    ComponentMap getValidatedComponents(Text name, Identifier modelId) {
        ComponentMap componentMap = this.components.add(DataComponentTypes.ITEM_NAME, name).add(DataComponentTypes.ITEM_MODEL, modelId).build();
        if (componentMap.contains(DataComponentTypes.DAMAGE) && componentMap.getOrDefault(DataComponentTypes.MAX_STACK_SIZE, 1) > 1) {
            throw new IllegalStateException("Item cannot have both durability and be stackable");
        }
        return componentMap;
    }
}
