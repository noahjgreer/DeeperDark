package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RegistryEntryReferenceArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType NOT_SUMMONABLE_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("entity.not_summonable", id);
   });
   public static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((element, type) -> {
      return Text.stringifiedTranslatable("argument.resource.not_found", element, type);
   });
   public static final Dynamic3CommandExceptionType INVALID_TYPE_EXCEPTION = new Dynamic3CommandExceptionType((element, type, expectedType) -> {
      return Text.stringifiedTranslatable("argument.resource.invalid_type", element, type, expectedType);
   });
   final RegistryKey registryRef;
   private final RegistryWrapper registryWrapper;

   public RegistryEntryReferenceArgumentType(CommandRegistryAccess registryAccess, RegistryKey registryRef) {
      this.registryRef = registryRef;
      this.registryWrapper = registryAccess.getOrThrow(registryRef);
   }

   public static RegistryEntryReferenceArgumentType registryEntry(CommandRegistryAccess registryAccess, RegistryKey registryRef) {
      return new RegistryEntryReferenceArgumentType(registryAccess, registryRef);
   }

   public static RegistryEntry.Reference getRegistryEntry(CommandContext context, String name, RegistryKey registryRef) throws CommandSyntaxException {
      RegistryEntry.Reference reference = (RegistryEntry.Reference)context.getArgument(name, RegistryEntry.Reference.class);
      RegistryKey registryKey = reference.registryKey();
      if (registryKey.isOf(registryRef)) {
         return reference;
      } else {
         throw INVALID_TYPE_EXCEPTION.create(registryKey.getValue(), registryKey.getRegistry(), registryRef.getValue());
      }
   }

   public static RegistryEntry.Reference getEntityAttribute(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.ATTRIBUTE);
   }

   public static RegistryEntry.Reference getConfiguredFeature(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.CONFIGURED_FEATURE);
   }

   public static RegistryEntry.Reference getStructure(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.STRUCTURE);
   }

   public static RegistryEntry.Reference getEntityType(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
   }

   public static RegistryEntry.Reference getSummonableEntityType(CommandContext context, String name) throws CommandSyntaxException {
      RegistryEntry.Reference reference = getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
      if (!((EntityType)reference.value()).isSummonable()) {
         throw NOT_SUMMONABLE_EXCEPTION.create(reference.registryKey().getValue().toString());
      } else {
         return reference;
      }
   }

   public static RegistryEntry.Reference getStatusEffect(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.STATUS_EFFECT);
   }

   public static RegistryEntry.Reference getEnchantment(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.ENCHANTMENT);
   }

   public RegistryEntry.Reference parse(StringReader stringReader) throws CommandSyntaxException {
      Identifier identifier = Identifier.fromCommandInput(stringReader);
      RegistryKey registryKey = RegistryKey.of(this.registryRef, identifier);
      return (RegistryEntry.Reference)this.registryWrapper.getOptional(registryKey).orElseThrow(() -> {
         return NOT_FOUND_EXCEPTION.createWithContext(stringReader, identifier, this.registryRef.getValue());
      });
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

      public Properties getArgumentTypeProperties(RegistryEntryReferenceArgumentType registryEntryReferenceArgumentType) {
         return new Properties(registryEntryReferenceArgumentType.registryRef);
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

         public RegistryEntryReferenceArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new RegistryEntryReferenceArgumentType(commandRegistryAccess, this.registryRef);
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
