package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.WrittenBookContentComponent;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.collection.CollectionPredicate;
import net.minecraft.predicate.component.ComponentSubPredicate;
import net.minecraft.text.RawFilteredPair;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public record WrittenBookContentPredicate(Optional pages, Optional author, Optional title, NumberRange.IntRange generation, Optional resolved) implements ComponentSubPredicate {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(CollectionPredicate.createCodec(WrittenBookContentPredicate.RawTextPredicate.CODEC).optionalFieldOf("pages").forGetter(WrittenBookContentPredicate::pages), Codec.STRING.optionalFieldOf("author").forGetter(WrittenBookContentPredicate::author), Codec.STRING.optionalFieldOf("title").forGetter(WrittenBookContentPredicate::title), NumberRange.IntRange.CODEC.optionalFieldOf("generation", NumberRange.IntRange.ANY).forGetter(WrittenBookContentPredicate::generation), Codec.BOOL.optionalFieldOf("resolved").forGetter(WrittenBookContentPredicate::resolved)).apply(instance, WrittenBookContentPredicate::new);
   });

   public WrittenBookContentPredicate(Optional optional, Optional optional2, Optional optional3, NumberRange.IntRange intRange, Optional optional4) {
      this.pages = optional;
      this.author = optional2;
      this.title = optional3;
      this.generation = intRange;
      this.resolved = optional4;
   }

   public ComponentType getComponentType() {
      return DataComponentTypes.WRITTEN_BOOK_CONTENT;
   }

   public boolean test(WrittenBookContentComponent writtenBookContentComponent) {
      if (this.author.isPresent() && !((String)this.author.get()).equals(writtenBookContentComponent.author())) {
         return false;
      } else if (this.title.isPresent() && !((String)this.title.get()).equals(writtenBookContentComponent.title().raw())) {
         return false;
      } else if (!this.generation.test(writtenBookContentComponent.generation())) {
         return false;
      } else if (this.resolved.isPresent() && (Boolean)this.resolved.get() != writtenBookContentComponent.resolved()) {
         return false;
      } else {
         return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test((Iterable)writtenBookContentComponent.pages());
      }
   }

   public Optional pages() {
      return this.pages;
   }

   public Optional author() {
      return this.author;
   }

   public Optional title() {
      return this.title;
   }

   public NumberRange.IntRange generation() {
      return this.generation;
   }

   public Optional resolved() {
      return this.resolved;
   }

   public static record RawTextPredicate(Text contents) implements Predicate {
      public static final Codec CODEC;

      public RawTextPredicate(Text text) {
         this.contents = text;
      }

      public boolean test(RawFilteredPair rawFilteredPair) {
         return ((Text)rawFilteredPair.raw()).equals(this.contents);
      }

      public Text contents() {
         return this.contents;
      }

      // $FF: synthetic method
      public boolean test(final Object text) {
         return this.test((RawFilteredPair)text);
      }

      static {
         CODEC = TextCodecs.CODEC.xmap(RawTextPredicate::new, RawTextPredicate::contents);
      }
   }
}
