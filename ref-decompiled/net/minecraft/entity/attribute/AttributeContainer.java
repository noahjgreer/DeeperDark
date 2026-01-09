package net.minecraft.entity.attribute;

import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class AttributeContainer {
   private final Map custom = new Object2ObjectOpenHashMap();
   private final Set tracked = new ObjectOpenHashSet();
   private final Set pendingUpdate = new ObjectOpenHashSet();
   private final DefaultAttributeContainer defaultAttributes;

   public AttributeContainer(DefaultAttributeContainer defaultAttributes) {
      this.defaultAttributes = defaultAttributes;
   }

   private void updateTrackedStatus(EntityAttributeInstance instance) {
      this.pendingUpdate.add(instance);
      if (((EntityAttribute)instance.getAttribute().value()).isTracked()) {
         this.tracked.add(instance);
      }

   }

   public Set getTracked() {
      return this.tracked;
   }

   public Set getPendingUpdate() {
      return this.pendingUpdate;
   }

   public Collection getAttributesToSend() {
      return (Collection)this.custom.values().stream().filter((attribute) -> {
         return ((EntityAttribute)attribute.getAttribute().value()).isTracked();
      }).collect(Collectors.toList());
   }

   @Nullable
   public EntityAttributeInstance getCustomInstance(RegistryEntry attribute) {
      return (EntityAttributeInstance)this.custom.computeIfAbsent(attribute, (attributex) -> {
         return this.defaultAttributes.createOverride(this::updateTrackedStatus, attributex);
      });
   }

   public boolean hasAttribute(RegistryEntry attribute) {
      return this.custom.get(attribute) != null || this.defaultAttributes.has(attribute);
   }

   public boolean hasModifierForAttribute(RegistryEntry attribute, Identifier id) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
      return entityAttributeInstance != null ? entityAttributeInstance.getModifier(id) != null : this.defaultAttributes.hasModifier(attribute, id);
   }

   public double getValue(RegistryEntry attribute) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
      return entityAttributeInstance != null ? entityAttributeInstance.getValue() : this.defaultAttributes.getValue(attribute);
   }

   public double getBaseValue(RegistryEntry attribute) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
      return entityAttributeInstance != null ? entityAttributeInstance.getBaseValue() : this.defaultAttributes.getBaseValue(attribute);
   }

   public double getModifierValue(RegistryEntry attribute, Identifier id) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
      return entityAttributeInstance != null ? entityAttributeInstance.getModifier(id).value() : this.defaultAttributes.getModifierValue(attribute, id);
   }

   public void addTemporaryModifiers(Multimap modifiersMap) {
      modifiersMap.forEach((attribute, modifier) -> {
         EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attribute);
         if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(modifier.id());
            entityAttributeInstance.addTemporaryModifier(modifier);
         }

      });
   }

   public void removeModifiers(Multimap modifiersMap) {
      modifiersMap.asMap().forEach((attribute, modifiers) -> {
         EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
         if (entityAttributeInstance != null) {
            modifiers.forEach((modifier) -> {
               entityAttributeInstance.removeModifier(modifier.id());
            });
         }

      });
   }

   public void setFrom(AttributeContainer other) {
      other.custom.values().forEach((attributeInstance) -> {
         EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attributeInstance.getAttribute());
         if (entityAttributeInstance != null) {
            entityAttributeInstance.setFrom(attributeInstance);
         }

      });
   }

   public void setBaseFrom(AttributeContainer other) {
      other.custom.values().forEach((attributeInstance) -> {
         EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attributeInstance.getAttribute());
         if (entityAttributeInstance != null) {
            entityAttributeInstance.setBaseValue(attributeInstance.getBaseValue());
         }

      });
   }

   public void addPersistentModifiersFrom(AttributeContainer other) {
      other.custom.values().forEach((attributeInstance) -> {
         EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(attributeInstance.getAttribute());
         if (entityAttributeInstance != null) {
            entityAttributeInstance.addPersistentModifiers(attributeInstance.getPersistentModifiers());
         }

      });
   }

   public boolean resetToBaseValue(RegistryEntry attribute) {
      if (!this.defaultAttributes.has(attribute)) {
         return false;
      } else {
         EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.custom.get(attribute);
         if (entityAttributeInstance != null) {
            entityAttributeInstance.setBaseValue(this.defaultAttributes.getBaseValue(attribute));
         }

         return true;
      }
   }

   public List pack() {
      List list = new ArrayList(this.custom.values().size());
      Iterator var2 = this.custom.values().iterator();

      while(var2.hasNext()) {
         EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)var2.next();
         list.add(entityAttributeInstance.pack());
      }

      return list;
   }

   public void unpack(List packedList) {
      Iterator var2 = packedList.iterator();

      while(var2.hasNext()) {
         EntityAttributeInstance.Packed packed = (EntityAttributeInstance.Packed)var2.next();
         EntityAttributeInstance entityAttributeInstance = this.getCustomInstance(packed.attribute());
         if (entityAttributeInstance != null) {
            entityAttributeInstance.unpack(packed);
         }
      }

   }
}
