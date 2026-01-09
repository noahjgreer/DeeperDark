package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WritableBookContentComponent;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.text.RawFilteredPair;

public record WritableBookContentPredicate(Optional pages) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CollectionPredicate.createCodec(WritableBookContentPredicate.RawStringPredicate.CODEC).optionalFieldOf("pages").forGetter(WritableBookContentPredicate::pages)).apply(instance, WritableBookContentPredicate::new);
   });

   public WritableBookContentPredicate(Optional optional) {
      this.pages = optional;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.WRITABLE_BOOK_CONTENT;
   }

   public boolean test(WritableBookContentComponent writableBookContentComponent) {
      return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test((Iterable)writableBookContentComponent.pages());
   }

   public Optional pages() {
      return this.pages;
   }

   public static record RawStringPredicate(String contents) implements Predicate {
      public static final Codec CODEC;

      public RawStringPredicate(String string) {
         this.contents = string;
      }

      public boolean test(RawFilteredPair rawFilteredPair) {
         return ((String)rawFilteredPair.raw()).equals(this.contents);
      }

      public String contents() {
         return this.contents;
      }

      // $FF: synthetic method
      public boolean test(final Object string) {
         return this.test((RawFilteredPair)string);
      }

      static {
         CODEC = Codec.STRING.xmap(RawStringPredicate::new, RawStringPredicate::contents);
      }
   }
}
