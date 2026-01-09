package net.minecraft.item.equipment;

import java.util.Map;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record ArmorMaterial(int durability, Map defense, int enchantmentValue, RegistryEntry equipSound, float toughness, float knockbackResistance, TagKey repairIngredient, RegistryKey assetId) {
   public ArmorMaterial(int i, Map map, int j, RegistryEntry registryEntry, float f, float g, TagKey tagKey, RegistryKey registryKey) {
      this.durability = i;
      this.defense = map;
      this.enchantmentValue = j;
      this.equipSound = registryEntry;
      this.toughness = f;
      this.knockbackResistance = g;
      this.repairIngredient = tagKey;
      this.assetId = registryKey;
   }

   public AttributeModifiersComponent createAttributeModifiers(EquipmentType equipmentType) {
      int i = (Integer)this.defense.getOrDefault(equipmentType, 0);
      AttributeModifiersComponent.Builder builder = AttributeModifiersComponent.builder();
      AttributeModifierSlot attributeModifierSlot = AttributeModifierSlot.forEquipmentSlot(equipmentType.getEquipmentSlot());
      Identifier identifier = Identifier.ofVanilla("armor." + equipmentType.getName());
      builder.add(EntityAttributes.ARMOR, new EntityAttributeModifier(identifier, (double)i, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
      builder.add(EntityAttributes.ARMOR_TOUGHNESS, new EntityAttributeModifier(identifier, (double)this.toughness, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
      if (this.knockbackResistance > 0.0F) {
         builder.add(EntityAttributes.KNOCKBACK_RESISTANCE, new EntityAttributeModifier(identifier, (double)this.knockbackResistance, EntityAttributeModifier.Operation.ADD_VALUE), attributeModifierSlot);
      }

      return builder.build();
   }

   public int durability() {
      return this.durability;
   }

   public Map defense() {
      return this.defense;
   }

   public int enchantmentValue() {
      return this.enchantmentValue;
   }

   public RegistryEntry equipSound() {
      return this.equipSound;
   }

   public float toughness() {
      return this.toughness;
   }

   public float knockbackResistance() {
      return this.knockbackResistance;
   }

   public TagKey repairIngredient() {
      return this.repairIngredient;
   }

   public RegistryKey assetId() {
      return this.assetId;
   }
}
