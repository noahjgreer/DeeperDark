package net.minecraft.registry.tag;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

public class TagEntry {
   private static final Codec ENTRY_CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.TAG_ENTRY_ID.fieldOf("id").forGetter(TagEntry::getIdForCodec), Codec.BOOL.optionalFieldOf("required", true).forGetter((entry) -> {
         return entry.required;
      })).apply(instance, TagEntry::new);
   });
   public static final Codec CODEC;
   private final Identifier id;
   private final boolean tag;
   private final boolean required;

   private TagEntry(Identifier id, boolean tag, boolean required) {
      this.id = id;
      this.tag = tag;
      this.required = required;
   }

   private TagEntry(Codecs.TagEntryId id, boolean required) {
      this.id = id.id();
      this.tag = id.tag();
      this.required = required;
   }

   private Codecs.TagEntryId getIdForCodec() {
      return new Codecs.TagEntryId(this.id, this.tag);
   }

   public static TagEntry create(Identifier id) {
      return new TagEntry(id, false, true);
   }

   public static TagEntry createOptional(Identifier id) {
      return new TagEntry(id, false, false);
   }

   public static TagEntry createTag(Identifier id) {
      return new TagEntry(id, true, true);
   }

   public static TagEntry createOptionalTag(Identifier id) {
      return new TagEntry(id, true, false);
   }

   public boolean resolve(ValueGetter valueGetter, Consumer idConsumer) {
      if (this.tag) {
         Collection collection = valueGetter.tag(this.id);
         if (collection == null) {
            return !this.required;
         }

         collection.forEach(idConsumer);
      } else {
         Object object = valueGetter.direct(this.id, this.required);
         if (object == null) {
            return !this.required;
         }

         idConsumer.accept(object);
      }

      return true;
   }

   public void forEachRequiredTagId(Consumer idConsumer) {
      if (this.tag && this.required) {
         idConsumer.accept(this.id);
      }

   }

   public void forEachOptionalTagId(Consumer idConsumer) {
      if (this.tag && !this.required) {
         idConsumer.accept(this.id);
      }

   }

   public boolean canAdd(Predicate directEntryPredicate, Predicate tagEntryPredicate) {
      return !this.required || (this.tag ? tagEntryPredicate : directEntryPredicate).test(this.id);
   }

   public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      if (this.tag) {
         stringBuilder.append('#');
      }

      stringBuilder.append(this.id);
      if (!this.required) {
         stringBuilder.append('?');
      }

      return stringBuilder.toString();
   }

   static {
      CODEC = Codec.either(Codecs.TAG_ENTRY_ID, ENTRY_CODEC).xmap((either) -> {
         return (TagEntry)either.map((id) -> {
            return new TagEntry(id, true);
         }, (entry) -> {
            return entry;
         });
      }, (entry) -> {
         return entry.required ? Either.left(entry.getIdForCodec()) : Either.right(entry);
      });
   }

   public interface ValueGetter {
      @Nullable
      Object direct(Identifier id, boolean required);

      @Nullable
      Collection tag(Identifier id);
   }
}
