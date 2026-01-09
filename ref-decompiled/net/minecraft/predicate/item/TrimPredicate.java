package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;

public record TrimPredicate(Optional material, Optional pattern) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.TRIM_MATERIAL).optionalFieldOf("material").forGetter(TrimPredicate::material), RegistryCodecs.entryList(RegistryKeys.TRIM_PATTERN).optionalFieldOf("pattern").forGetter(TrimPredicate::pattern)).apply(instance, TrimPredicate::new);
   });

   public TrimPredicate(Optional optional, Optional optional2) {
      this.material = optional;
      this.pattern = optional2;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.TRIM;
   }

   public boolean test(ArmorTrim armorTrim) {
      if (this.material.isPresent() && !((RegistryEntryList)this.material.get()).contains(armorTrim.material())) {
         return false;
      } else {
         return !this.pattern.isPresent() || ((RegistryEntryList)this.pattern.get()).contains(armorTrim.pattern());
      }
   }

   public Optional material() {
      return this.material;
   }

   public Optional pattern() {
      return this.pattern;
   }
}
