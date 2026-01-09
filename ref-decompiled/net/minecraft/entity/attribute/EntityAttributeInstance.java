package net.minecraft.entity.attribute;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class EntityAttributeInstance {
   private final RegistryEntry type;
   private final Map operationToModifiers = Maps.newEnumMap(EntityAttributeModifier.Operation.class);
   private final Map idToModifiers = new Object2ObjectArrayMap();
   private final Map persistentModifiers = new Object2ObjectArrayMap();
   private double baseValue;
   private boolean dirty = true;
   private double value;
   private final Consumer updateCallback;

   public EntityAttributeInstance(RegistryEntry type, Consumer updateCallback) {
      this.type = type;
      this.updateCallback = updateCallback;
      this.baseValue = ((EntityAttribute)type.value()).getDefaultValue();
   }

   public RegistryEntry getAttribute() {
      return this.type;
   }

   public double getBaseValue() {
      return this.baseValue;
   }

   public void setBaseValue(double baseValue) {
      if (baseValue != this.baseValue) {
         this.baseValue = baseValue;
         this.onUpdate();
      }
   }

   @VisibleForTesting
   Map getModifiers(EntityAttributeModifier.Operation operation) {
      return (Map)this.operationToModifiers.computeIfAbsent(operation, (operationx) -> {
         return new Object2ObjectOpenHashMap();
      });
   }

   public Set getModifiers() {
      return ImmutableSet.copyOf(this.idToModifiers.values());
   }

   public Set getPersistentModifiers() {
      return ImmutableSet.copyOf(this.persistentModifiers.values());
   }

   @Nullable
   public EntityAttributeModifier getModifier(Identifier id) {
      return (EntityAttributeModifier)this.idToModifiers.get(id);
   }

   public boolean hasModifier(Identifier id) {
      return this.idToModifiers.get(id) != null;
   }

   private void addModifier(EntityAttributeModifier modifier) {
      EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.putIfAbsent(modifier.id(), modifier);
      if (entityAttributeModifier != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         this.getModifiers(modifier.operation()).put(modifier.id(), modifier);
         this.onUpdate();
      }
   }

   public void updateModifier(EntityAttributeModifier modifier) {
      EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.put(modifier.id(), modifier);
      if (modifier != entityAttributeModifier) {
         this.getModifiers(modifier.operation()).put(modifier.id(), modifier);
         this.onUpdate();
      }
   }

   public void addTemporaryModifier(EntityAttributeModifier modifier) {
      this.addModifier(modifier);
   }

   public void overwritePersistentModifier(EntityAttributeModifier modifier) {
      this.removeModifier(modifier.id());
      this.addModifier(modifier);
      this.persistentModifiers.put(modifier.id(), modifier);
   }

   public void addPersistentModifier(EntityAttributeModifier modifier) {
      this.addModifier(modifier);
      this.persistentModifiers.put(modifier.id(), modifier);
   }

   public void addPersistentModifiers(Collection modifiers) {
      Iterator var2 = modifiers.iterator();

      while(var2.hasNext()) {
         EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)var2.next();
         this.addPersistentModifier(entityAttributeModifier);
      }

   }

   protected void onUpdate() {
      this.dirty = true;
      this.updateCallback.accept(this);
   }

   public void removeModifier(EntityAttributeModifier modifier) {
      this.removeModifier(modifier.id());
   }

   public boolean removeModifier(Identifier id) {
      EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)this.idToModifiers.remove(id);
      if (entityAttributeModifier == null) {
         return false;
      } else {
         this.getModifiers(entityAttributeModifier.operation()).remove(id);
         this.persistentModifiers.remove(id);
         this.onUpdate();
         return true;
      }
   }

   public void clearModifiers() {
      Iterator var1 = this.getModifiers().iterator();

      while(var1.hasNext()) {
         EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)var1.next();
         this.removeModifier(entityAttributeModifier);
      }

   }

   public double getValue() {
      if (this.dirty) {
         this.value = this.computeValue();
         this.dirty = false;
      }

      return this.value;
   }

   private double computeValue() {
      double d = this.getBaseValue();

      EntityAttributeModifier entityAttributeModifier;
      for(Iterator var3 = this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_VALUE).iterator(); var3.hasNext(); d += entityAttributeModifier.value()) {
         entityAttributeModifier = (EntityAttributeModifier)var3.next();
      }

      double e = d;

      Iterator var5;
      EntityAttributeModifier entityAttributeModifier2;
      for(var5 = this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE).iterator(); var5.hasNext(); e += d * entityAttributeModifier2.value()) {
         entityAttributeModifier2 = (EntityAttributeModifier)var5.next();
      }

      for(var5 = this.getModifiersByOperation(EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL).iterator(); var5.hasNext(); e *= 1.0 + entityAttributeModifier2.value()) {
         entityAttributeModifier2 = (EntityAttributeModifier)var5.next();
      }

      return ((EntityAttribute)this.type.value()).clamp(e);
   }

   private Collection getModifiersByOperation(EntityAttributeModifier.Operation operation) {
      return ((Map)this.operationToModifiers.getOrDefault(operation, Map.of())).values();
   }

   public void setFrom(EntityAttributeInstance other) {
      this.baseValue = other.baseValue;
      this.idToModifiers.clear();
      this.idToModifiers.putAll(other.idToModifiers);
      this.persistentModifiers.clear();
      this.persistentModifiers.putAll(other.persistentModifiers);
      this.operationToModifiers.clear();
      other.operationToModifiers.forEach((operation, modifiers) -> {
         this.getModifiers(operation).putAll(modifiers);
      });
      this.onUpdate();
   }

   public Packed pack() {
      return new Packed(this.type, this.baseValue, List.copyOf(this.persistentModifiers.values()));
   }

   public void unpack(Packed packed) {
      this.baseValue = packed.baseValue;
      Iterator var2 = packed.modifiers.iterator();

      while(var2.hasNext()) {
         EntityAttributeModifier entityAttributeModifier = (EntityAttributeModifier)var2.next();
         this.idToModifiers.put(entityAttributeModifier.id(), entityAttributeModifier);
         this.getModifiers(entityAttributeModifier.operation()).put(entityAttributeModifier.id(), entityAttributeModifier);
         this.persistentModifiers.put(entityAttributeModifier.id(), entityAttributeModifier);
      }

      this.onUpdate();
   }

   public static record Packed(RegistryEntry attribute, double baseValue, List modifiers) {
      final double baseValue;
      final List modifiers;
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(Registries.ATTRIBUTE.getEntryCodec().fieldOf("id").forGetter(Packed::attribute), Codec.DOUBLE.fieldOf("base").orElse(0.0).forGetter(Packed::baseValue), EntityAttributeModifier.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(Packed::modifiers)).apply(instance, Packed::new);
      });
      public static final Codec LIST_CODEC;

      public Packed(RegistryEntry registryEntry, double d, List list) {
         this.attribute = registryEntry;
         this.baseValue = d;
         this.modifiers = list;
      }

      public RegistryEntry attribute() {
         return this.attribute;
      }

      public double baseValue() {
         return this.baseValue;
      }

      public List modifiers() {
         return this.modifiers;
      }

      static {
         LIST_CODEC = CODEC.listOf();
      }
   }
}
