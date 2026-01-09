package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;

public record BundleContentsPredicate(Optional items) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CollectionPredicate.createCodec(ItemPredicate.CODEC).optionalFieldOf("items").forGetter(BundleContentsPredicate::items)).apply(instance, BundleContentsPredicate::new);
   });

   public BundleContentsPredicate(Optional optional) {
      this.items = optional;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.BUNDLE_CONTENTS;
   }

   public boolean test(BundleContentsComponent bundleContentsComponent) {
      return !this.items.isPresent() || ((CollectionPredicate)this.items.get()).test(bundleContentsComponent.iterate());
   }

   public Optional items() {
      return this.items;
   }
}
