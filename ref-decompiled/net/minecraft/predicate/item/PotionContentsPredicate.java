package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.predicate.component.ComponentPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;

public record PotionContentsPredicate(RegistryEntryList potions) implements ComponentSubPredicate {
   public static final Codec CODEC;

   public PotionContentsPredicate(RegistryEntryList registryEntryList) {
      this.potions = registryEntryList;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.POTION_CONTENTS;
   }

   public boolean test(PotionContentsComponent potionContentsComponent) {
      Optional optional = potionContentsComponent.potion();
      return !optional.isEmpty() && this.potions.contains((RegistryEntry)optional.get());
   }

   public static ComponentPredicate potionContents(RegistryEntryList potions) {
      return new PotionContentsPredicate(potions);
   }

   public RegistryEntryList potions() {
      return this.potions;
   }

   static {
      CODEC = RegistryCodecs.entryList(RegistryKeys.POTION).xmap(PotionContentsPredicate::new, PotionContentsPredicate::potions);
   }
}
