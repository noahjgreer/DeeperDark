package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RegistryKeyArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType INVALID_FEATURE_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("commands.place.feature.invalid", id);
   });
   private static final DynamicCommandExceptionType INVALID_STRUCTURE_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("commands.place.structure.invalid", id);
   });
   private static final DynamicCommandExceptionType INVALID_JIGSAW_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("commands.place.jigsaw.invalid", id);
   });
   private static final DynamicCommandExceptionType RECIPE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("recipe.notFound", id);
   });
   private static final DynamicCommandExceptionType ADVANCEMENT_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType((id) -> {
      return Text.stringifiedTranslatable("advancement.advancementNotFound", id);
   });
   final RegistryKey registryRef;

   public RegistryKeyArgumentType(RegistryKey registryRef) {
      this.registryRef = registryRef;
   }

   public static RegistryKeyArgumentType registryKey(RegistryKey registryRef) {
      return new RegistryKeyArgumentType(registryRef);
   }

   public static RegistryKey getKey(CommandContext context, String name, RegistryKey registryRef, DynamicCommandExceptionType invalidException) throws CommandSyntaxException {
      RegistryKey registryKey = (RegistryKey)context.getArgument(name, RegistryKey.class);
      Optional optional = registryKey.tryCast(registryRef);
      return (RegistryKey)optional.orElseThrow(() -> {
         return invalidException.create(registryKey.getValue());
      });
   }

   private static Registry getRegistry(CommandContext context, RegistryKey registryRef) {
      return ((ServerCommandSource)context.getSource()).getServer().getRegistryManager().getOrThrow(registryRef);
   }

   private static RegistryEntry.Reference getRegistryEntry(CommandContext context, String name, RegistryKey registryRef, DynamicCommandExceptionType invalidException) throws CommandSyntaxException {
      RegistryKey registryKey = getKey(context, name, registryRef, invalidException);
      return (RegistryEntry.Reference)getRegistry(context, registryRef).getOptional(registryKey).orElseThrow(() -> {
         return invalidException.create(registryKey.getValue());
      });
   }

   public static RegistryEntry.Reference getConfiguredFeatureEntry(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.CONFIGURED_FEATURE, INVALID_FEATURE_EXCEPTION);
   }

   public static RegistryEntry.Reference getStructureEntry(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.STRUCTURE, INVALID_STRUCTURE_EXCEPTION);
   }

   public static RegistryEntry.Reference getStructurePoolEntry(CommandContext context, String name) throws CommandSyntaxException {
      return getRegistryEntry(context, name, RegistryKeys.TEMPLATE_POOL, INVALID_JIGSAW_EXCEPTION);
   }

   public static RecipeEntry getRecipeEntry(CommandContext context, String name) throws CommandSyntaxException {
      ServerRecipeManager serverRecipeManager = ((ServerCommandSource)context.getSource()).getServer().getRecipeManager();
      RegistryKey registryKey = getKey(context, name, RegistryKeys.RECIPE, RECIPE_NOT_FOUND_EXCEPTION);
      return (RecipeEntry)serverRecipeManager.get(registryKey).orElseThrow(() -> {
         return RECIPE_NOT_FOUND_EXCEPTION.create(registryKey.getValue());
      });
   }

   public static AdvancementEntry getAdvancementEntry(CommandContext context, String name) throws CommandSyntaxException {
      RegistryKey registryKey = getKey(context, name, RegistryKeys.ADVANCEMENT, ADVANCEMENT_NOT_FOUND_EXCEPTION);
      AdvancementEntry advancementEntry = ((ServerCommandSource)context.getSource()).getServer().getAdvancementLoader().get(registryKey.getValue());
      if (advancementEntry == null) {
         throw ADVANCEMENT_NOT_FOUND_EXCEPTION.create(registryKey.getValue());
      } else {
         return advancementEntry;
      }
   }

   public RegistryKey parse(StringReader stringReader) throws CommandSyntaxException {
      Identifier identifier = Identifier.fromCommandInput(stringReader);
      return RegistryKey.of(this.registryRef, identifier);
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

      public Properties getArgumentTypeProperties(RegistryKeyArgumentType registryKeyArgumentType) {
         return new Properties(registryKeyArgumentType.registryRef);
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

         public RegistryKeyArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new RegistryKeyArgumentType(this.registryRef);
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
