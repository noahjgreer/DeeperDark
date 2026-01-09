package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DefaultAttributeContainer {
   private final Map instances;

   DefaultAttributeContainer(Map instances) {
      this.instances = instances;
   }

   private EntityAttributeInstance require(RegistryEntry attribute) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
      if (entityAttributeInstance == null) {
         throw new IllegalArgumentException("Can't find attribute " + attribute.getIdAsString());
      } else {
         return entityAttributeInstance;
      }
   }

   public double getValue(RegistryEntry attribute) {
      return this.require(attribute).getValue();
   }

   public double getBaseValue(RegistryEntry attribute) {
      return this.require(attribute).getBaseValue();
   }

   public double getModifierValue(RegistryEntry attribute, Identifier id) {
      EntityAttributeModifier entityAttributeModifier = this.require(attribute).getModifier(id);
      if (entityAttributeModifier == null) {
         String var10002 = String.valueOf(id);
         throw new IllegalArgumentException("Can't find modifier " + var10002 + " on attribute " + attribute.getIdAsString());
      } else {
         return entityAttributeModifier.value();
      }
   }

   @Nullable
   public EntityAttributeInstance createOverride(Consumer updateCallback, RegistryEntry attribute) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
      if (entityAttributeInstance == null) {
         return null;
      } else {
         EntityAttributeInstance entityAttributeInstance2 = new EntityAttributeInstance(attribute, updateCallback);
         entityAttributeInstance2.setFrom(entityAttributeInstance);
         return entityAttributeInstance2;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public boolean has(RegistryEntry attribute) {
      return this.instances.containsKey(attribute);
   }

   public boolean hasModifier(RegistryEntry attribute, Identifier id) {
      EntityAttributeInstance entityAttributeInstance = (EntityAttributeInstance)this.instances.get(attribute);
      return entityAttributeInstance != null && entityAttributeInstance.getModifier(id) != null;
   }

   public static class Builder {
      private final ImmutableMap.Builder instances = ImmutableMap.builder();
      private boolean unmodifiable;

      private EntityAttributeInstance checkedAdd(RegistryEntry attribute) {
         EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, (attributex) -> {
            if (this.unmodifiable) {
               throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + attribute.getIdAsString());
            }
         });
         this.instances.put(attribute, entityAttributeInstance);
         return entityAttributeInstance;
      }

      public Builder add(RegistryEntry attribute) {
         this.checkedAdd(attribute);
         return this;
      }

      public Builder add(RegistryEntry attribute, double baseValue) {
         EntityAttributeInstance entityAttributeInstance = this.checkedAdd(attribute);
         entityAttributeInstance.setBaseValue(baseValue);
         return this;
      }

      public DefaultAttributeContainer build() {
         this.unmodifiable = true;
         return new DefaultAttributeContainer(this.instances.buildKeepingLast());
      }
   }
}
