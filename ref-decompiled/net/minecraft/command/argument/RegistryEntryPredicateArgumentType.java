package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RegistryEntryPredicateArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   private static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((tag, type) -> {
      return Text.stringifiedTranslatable("argument.resource_tag.not_found", tag, type);
   });
   private static final Dynamic3CommandExceptionType WRONG_TYPE_EXCEPTION = new Dynamic3CommandExceptionType((tag, type, expectedType) -> {
      return Text.stringifiedTranslatable("argument.resource_tag.invalid_type", tag, type, expectedType);
   });
   private final RegistryWrapper registryWrapper;
   final RegistryKey registryRef;

   public RegistryEntryPredicateArgumentType(CommandRegistryAccess registryAccess, RegistryKey registryRef) {
      this.registryRef = registryRef;
      this.registryWrapper = registryAccess.getOrThrow(registryRef);
   }

   public static RegistryEntryPredicateArgumentType registryEntryPredicate(CommandRegistryAccess registryRef, RegistryKey registryAccess) {
      return new RegistryEntryPredicateArgumentType(registryRef, registryAccess);
   }

   public static EntryPredicate getRegistryEntryPredicate(CommandContext context, String name, RegistryKey registryRef) throws CommandSyntaxException {
      EntryPredicate entryPredicate = (EntryPredicate)context.getArgument(name, EntryPredicate.class);
      Optional optional = entryPredicate.tryCast(registryRef);
      return (EntryPredicate)optional.orElseThrow(() -> {
         return (CommandSyntaxException)entryPredicate.getEntry().map((entry) -> {
            RegistryKey registryKey2 = entry.registryKey();
            return RegistryEntryReferenceArgumentType.INVALID_TYPE_EXCEPTION.create(registryKey2.getValue(), registryKey2.getRegistry(), registryRef.getValue());
         }, (entryList) -> {
            TagKey tagKey = entryList.getTag();
            return WRONG_TYPE_EXCEPTION.create(tagKey.id(), tagKey.registryRef(), registryRef.getValue());
         });
      });
   }

   public EntryPredicate parse(StringReader stringReader) throws CommandSyntaxException {
      if (stringReader.canRead() && stringReader.peek() == '#') {
         int i = stringReader.getCursor();

         try {
            stringReader.skip();
            Identifier identifier = Identifier.fromCommandInput(stringReader);
            TagKey tagKey = TagKey.of(this.registryRef, identifier);
            RegistryEntryList.Named named = (RegistryEntryList.Named)this.registryWrapper.getOptional(tagKey).orElseThrow(() -> {
               return NOT_FOUND_EXCEPTION.createWithContext(stringReader, identifier, this.registryRef.getValue());
            });
            return new TagBased(named);
         } catch (CommandSyntaxException var6) {
            stringReader.setCursor(i);
            throw var6;
         }
      } else {
         Identifier identifier2 = Identifier.fromCommandInput(stringReader);
         RegistryKey registryKey = RegistryKey.of(this.registryRef, identifier2);
         RegistryEntry.Reference reference = (RegistryEntry.Reference)this.registryWrapper.getOptional(registryKey).orElseThrow(() -> {
            return RegistryEntryReferenceArgumentType.NOT_FOUND_EXCEPTION.createWithContext(stringReader, identifier2, this.registryRef.getValue());
         });
         return new EntryBased(reference);
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.listSuggestions(context, builder, this.registryRef, CommandSource.SuggestedIdType.ALL);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   public interface EntryPredicate extends Predicate {
      Either getEntry();

      Optional tryCast(RegistryKey registryRef);

      String asString();
   }

   private static record TagBased(RegistryEntryList.Named tag) implements EntryPredicate {
      TagBased(RegistryEntryList.Named named) {
         this.tag = named;
      }

      public Either getEntry() {
         return Either.right(this.tag);
      }

      public Optional tryCast(RegistryKey registryRef) {
         return this.tag.getTag().isOf(registryRef) ? Optional.of(this) : Optional.empty();
      }

      public boolean test(RegistryEntry registryEntry) {
         return this.tag.contains(registryEntry);
      }

      public String asString() {
         return "#" + String.valueOf(this.tag.getTag().id());
      }

      public RegistryEntryList.Named tag() {
         return this.tag;
      }

      // $FF: synthetic method
      public boolean test(final Object entry) {
         return this.test((RegistryEntry)entry);
      }
   }

   private static record EntryBased(RegistryEntry.Reference value) implements EntryPredicate {
      EntryBased(RegistryEntry.Reference reference) {
         this.value = reference;
      }

      public Either getEntry() {
         return Either.left(this.value);
      }

      public Optional tryCast(RegistryKey registryRef) {
         return this.value.registryKey().isOf(registryRef) ? Optional.of(this) : Optional.empty();
      }

      public boolean test(RegistryEntry registryEntry) {
         return registryEntry.equals(this.value);
      }

      public String asString() {
         return this.value.registryKey().getValue().toString();
      }

      public RegistryEntry.Reference value() {
         return this.value;
      }

      // $FF: synthetic method
      public boolean test(final Object entry) {
         return this.test((RegistryEntry)entry);
      }
   }

   public static class Serializer implements ArgumentSerializer {
      public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
         packetByteBuf.writeRegistryKey(properties.registryRef);
      }

      public Properties fromPacket(PacketByteBuf packetByteBuf) {
         return new Properties(packetByteBuf.readRegistryRefKey());
      }

      public void writeJson(Properties properties, JsonObject jsonObject) {
         jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
      }

      public Properties getArgumentTypeProperties(RegistryEntryPredicateArgumentType registryEntryPredicateArgumentType) {
         return new Properties(registryEntryPredicateArgumentType.registryRef);
      }

      // $FF: synthetic method
      public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
         return this.fromPacket(buf);
      }

      public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
         final RegistryKey registryRef;

         Properties(final RegistryKey registryRef) {
            this.registryRef = registryRef;
         }

         public RegistryEntryPredicateArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new RegistryEntryPredicateArgumentType(commandRegistryAccess, this.registryRef);
         }

         public ArgumentSerializer getSerializer() {
            return Serializer.this;
         }

         // $FF: synthetic method
         public ArgumentType createType(final CommandRegistryAccess commandRegistryAccess) {
            return this.createType(commandRegistryAccess);
         }
      }
   }
}
