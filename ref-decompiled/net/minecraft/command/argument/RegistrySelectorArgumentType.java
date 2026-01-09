package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FilenameUtils;

public class RegistrySelectorArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = List.of("minecraft:*", "*:asset", "*");
   public static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((selector, registryRef) -> {
      return Text.stringifiedTranslatable("argument.resource_selector.not_found", selector, registryRef);
   });
   final RegistryKey registryRef;
   private final RegistryWrapper registry;

   RegistrySelectorArgumentType(CommandRegistryAccess registries, RegistryKey registryRef) {
      this.registryRef = registryRef;
      this.registry = registries.getOrThrow(registryRef);
   }

   public Collection parse(StringReader stringReader) throws CommandSyntaxException {
      String string = addNamespace(read(stringReader));
      List list = this.registry.streamEntries().filter((entry) -> {
         return matches(string, entry.registryKey().getValue());
      }).toList();
      if (list.isEmpty()) {
         throw NOT_FOUND_EXCEPTION.createWithContext(stringReader, string, this.registryRef.getValue());
      } else {
         return list;
      }
   }

   public static Collection select(StringReader reader, RegistryWrapper registry) {
      String string = addNamespace(read(reader));
      return registry.streamEntries().filter((entry) -> {
         return matches(string, entry.registryKey().getValue());
      }).toList();
   }

   private static String read(StringReader reader) {
      int i = reader.getCursor();

      while(reader.canRead() && isSelectorChar(reader.peek())) {
         reader.skip();
      }

      return reader.getString().substring(i, reader.getCursor());
   }

   private static boolean isSelectorChar(char c) {
      return Identifier.isCharValid(c) || c == '*' || c == '?';
   }

   private static String addNamespace(String path) {
      return !path.contains(":") ? "minecraft:" + path : path;
   }

   private static boolean matches(String selector, Identifier id) {
      return FilenameUtils.wildcardMatch(id.toString(), selector);
   }

   public static RegistrySelectorArgumentType selector(CommandRegistryAccess registries, RegistryKey registryRef) {
      return new RegistrySelectorArgumentType(registries, registryRef);
   }

   public static Collection getEntries(CommandContext context, String name) {
      return (Collection)context.getArgument(name, Collection.class);
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return CommandSource.listSuggestions(context, builder, this.registryRef, CommandSource.SuggestedIdType.ELEMENTS);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
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

      public Properties getArgumentTypeProperties(RegistrySelectorArgumentType registrySelectorArgumentType) {
         return new Properties(registrySelectorArgumentType.registryRef);
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

         public RegistrySelectorArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new RegistrySelectorArgumentType(commandRegistryAccess, this.registryRef);
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
