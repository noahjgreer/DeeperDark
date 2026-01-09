package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;

public record ContainerPredicate(Optional items) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CollectionPredicate.createCodec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(ContainerPredicate::items)).apply(instance, ContainerPredicate::new);
   });

   public ContainerPredicate(Optional optional) {
      this.items = optional;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.CONTAINER;
   }

   public boolean test(ContainerComponent containerComponent) {
      return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(containerComponent.iterateNonEmpty());
   }

   public Optional items() {
      return this.items;
   }
}
