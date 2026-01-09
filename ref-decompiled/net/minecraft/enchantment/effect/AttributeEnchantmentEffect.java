package net.minecraft.enchantment.effect;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Vec3d;

public record AttributeEnchantmentEffect(Identifier id, RegistryEntry attribute, EnchantmentLevelBasedValue amount, EntityAttributeModifier.Operation operation) implements EnchantmentLocationBasedEffect {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("id").forGetter(AttributeEnchantmentEffect::id), EntityAttribute.CODEC.fieldOf("attribute").forGetter(AttributeEnchantmentEffect::attribute), EnchantmentLevelBasedValue.CODEC.fieldOf("amount").forGetter(AttributeEnchantmentEffect::amount), EntityAttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AttributeEnchantmentEffect::operation)).apply(instance, AttributeEnchantmentEffect::new);
   });

   public AttributeEnchantmentEffect(Identifier identifier, RegistryEntry registryEntry, EnchantmentLevelBasedValue enchantmentLevelBasedValue, EntityAttributeModifier.Operation operation) {
      this.id = identifier;
      this.attribute = registryEntry;
      this.amount = enchantmentLevelBasedValue;
      this.operation = operation;
   }

   private Identifier getModifierId(StringIdentifiable suffix) {
      return this.id.withSuffixedPath("/" + suffix.asString());
   }

   public EntityAttributeModifier createAttributeModifier(int value, StringIdentifiable suffix) {
      return new EntityAttributeModifier(this.getModifierId(suffix), (double)this.amount().getValue(value), this.operation());
   }

   public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos, boolean newlyApplied) {
      if (newlyApplied && user instanceof LivingEntity livingEntity) {
         livingEntity.getAttributes().addTemporaryModifiers(this.getModifiers(level, context.slot()));
      }

   }

   public void remove(EnchantmentEffectContext context, Entity user, Vec3d pos, int level) {
      if (user instanceof LivingEntity livingEntity) {
         livingEntity.getAttributes().removeModifiers(this.getModifiers(level, context.slot()));
      }

   }

   private HashMultimap getModifiers(int level, EquipmentSlot slot) {
      HashMultimap hashMultimap = HashMultimap.create();
      hashMultimap.put(this.attribute, this.createAttributeModifier(level, slot));
      return hashMultimap;
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Identifier id() {
      return this.id;
   }

   public RegistryEntry attribute() {
      return this.attribute;
   }

   public EnchantmentLevelBasedValue amount() {
      return this.amount;
   }

   public EntityAttributeModifier.Operation operation() {
      return this.operation;
   }
}
