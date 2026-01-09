package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class RegistryPredicateArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
   final RegistryKey registryRef;

   public RegistryPredicateArgumentType(RegistryKey registryRef) {
      this.registryRef = registryRef;
   }

   public static RegistryPredicateArgumentType registryPredicate(RegistryKey registryRef) {
      return new RegistryPredicateArgumentType(registryRef);
   }

   public static RegistryPredicate getPredicate(CommandContext context, String name, RegistryKey registryRef, DynamicCommandExceptionType invalidException) throws CommandSyntaxException {
      RegistryPredicate registryPredicate = (RegistryPredicate)context.getArgument(name, RegistryPredicate.class);
      Optional optional = registryPredicate.tryCast(registryRef);
      return (RegistryPredicate)optional.orElseThrow(() -> {
         return invalidException.create(registryPredicate);
      });
   }

   public RegistryPredicate parse(StringReader stringReader) throws CommandSyntaxException {
      if (stringReader.canRead() && stringReader.peek() == '#') {
         int i = stringReader.getCursor();

         try {
            stringReader.skip();
            Identifier identifier = Identifier.fromCommandInput(stringReader);
            return new TagBased(TagKey.of(this.registryRef, identifier));
         } catch (CommandSyntaxException var4) {
            stringReader.setCursor(i);
            throw var4;
         }
      } else {
         Identifier identifier2 = Identifier.fromCommandInput(stringReader);
         return new RegistryKeyBased(RegistryKey.of(this.registryRef, identifier2));
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

   public interface RegistryPredicate extends Predicate {
      Either getKey();

      Optional tryCast(RegistryKey registryRef);

      String asString();
   }

   static record TagBased(TagKey key) implements RegistryPredicate {
      TagBased(TagKey tagKey) {
         this.key = tagKey;
      }

      public Either getKey() {
         return Either.right(this.key);
      }

      public Optional tryCast(RegistryKey registryRef) {
         return this.key.tryCast(registryRef).map(TagBased::new);
      }

      public boolean test(RegistryEntry registryEntry) {
         return registryEntry.isIn(this.key);
      }

      public String asString() {
         return "#" + String.valueOf(this.key.id());
      }

      public TagKey key() {
         return this.key;
      }

      // $FF: synthetic method
      public boolean test(final Object entry) {
         return this.test((RegistryEntry)entry);
      }
   }

   private static record RegistryKeyBased(RegistryKey key) implements RegistryPredicate {
      RegistryKeyBased(RegistryKey registryKey) {
         this.key = registryKey;
      }

      public Either getKey() {
         return Either.left(this.key);
      }

      public Optional tryCast(RegistryKey registryRef) {
         return this.key.tryCast(registryRef).map(RegistryKeyBased::new);
      }

      public boolean test(RegistryEntry registryEntry) {
         return registryEntry.matchesKey(this.key);
      }

      public String asString() {
         return this.key.getValue().toString();
      }

      public RegistryKey key() {
         return this.key;
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

      public Properties getArgumentTypeProperties(RegistryPredicateArgumentType registryPredicateArgumentType) {
         return new Properties(registryPredicateArgumentType.registryRef);
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

         public RegistryPredicateArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new RegistryPredicateArgumentType(this.registryRef);
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
