package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public record EntityTypePredicate(RegistryEntryList types) {
   public static final Codec CODEC;

   public EntityTypePredicate(RegistryEntryList registryEntryList) {
      this.types = registryEntryList;
   }

   public static EntityTypePredicate create(RegistryEntryLookup entityTypeRegistry, EntityType type) {
      return new EntityTypePredicate(RegistryEntryList.of(type.getRegistryEntry()));
   }

   public static EntityTypePredicate create(RegistryEntryLookup entityTypeRegistry, TagKey tag) {
      return new EntityTypePredicate(entityTypeRegistry.getOrThrow(tag));
   }

   public boolean matches(EntityType type) {
      return type.isIn(this.types);
   }

   public RegistryEntryList types() {
      return this.types;
   }

   static {
      CODEC = RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).xmap(EntityTypePredicate::new, EntityTypePredicate::types);
   }
}
