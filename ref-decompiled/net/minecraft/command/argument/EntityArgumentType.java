package net.minecraft.command.argument;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class EntityArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("Player", "0123", "@e", "@e[type=foo]", "dd12be42-52a9-4a91-a8a1-11c01849e498");
   public static final SimpleCommandExceptionType TOO_MANY_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.toomany"));
   public static final SimpleCommandExceptionType TOO_MANY_PLAYERS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.player.toomany"));
   public static final SimpleCommandExceptionType PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.player.entities"));
   public static final SimpleCommandExceptionType ENTITY_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.notfound.entity"));
   public static final SimpleCommandExceptionType PLAYER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.notfound.player"));
   public static final SimpleCommandExceptionType NOT_ALLOWED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.entity.selector.not_allowed"));
   final boolean singleTarget;
   final boolean playersOnly;

   protected EntityArgumentType(boolean singleTarget, boolean playersOnly) {
      this.singleTarget = singleTarget;
      this.playersOnly = playersOnly;
   }

   public static EntityArgumentType entity() {
      return new EntityArgumentType(true, false);
   }

   public static Entity getEntity(CommandContext context, String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getEntity((ServerCommandSource)context.getSource());
   }

   public static EntityArgumentType entities() {
      return new EntityArgumentType(false, false);
   }

   public static Collection getEntities(CommandContext context, String name) throws CommandSyntaxException {
      Collection collection = getOptionalEntities(context, name);
      if (collection.isEmpty()) {
         throw ENTITY_NOT_FOUND_EXCEPTION.create();
      } else {
         return collection;
      }
   }

   public static Collection getOptionalEntities(CommandContext context, String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getEntities((ServerCommandSource)context.getSource());
   }

   public static Collection getOptionalPlayers(CommandContext context, String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayers((ServerCommandSource)context.getSource());
   }

   public static EntityArgumentType player() {
      return new EntityArgumentType(true, true);
   }

   public static ServerPlayerEntity getPlayer(CommandContext context, String name) throws CommandSyntaxException {
      return ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayer((ServerCommandSource)context.getSource());
   }

   public static EntityArgumentType players() {
      return new EntityArgumentType(false, true);
   }

   public static Collection getPlayers(CommandContext context, String name) throws CommandSyntaxException {
      List list = ((EntitySelector)context.getArgument(name, EntitySelector.class)).getPlayers((ServerCommandSource)context.getSource());
      if (list.isEmpty()) {
         throw PLAYER_NOT_FOUND_EXCEPTION.create();
      } else {
         return list;
      }
   }

   public EntitySelector parse(StringReader stringReader) throws CommandSyntaxException {
      return this.parse(stringReader, true);
   }

   public EntitySelector parse(StringReader stringReader, Object object) throws CommandSyntaxException {
      return this.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
   }

   private EntitySelector parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
      int i = false;
      EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, allowAtSelectors);
      EntitySelector entitySelector = entitySelectorReader.read();
      if (entitySelector.getLimit() > 1 && this.singleTarget) {
         if (this.playersOnly) {
            reader.setCursor(0);
            throw TOO_MANY_PLAYERS_EXCEPTION.createWithContext(reader);
         } else {
            reader.setCursor(0);
            throw TOO_MANY_ENTITIES_EXCEPTION.createWithContext(reader);
         }
      } else if (entitySelector.includesNonPlayers() && this.playersOnly && !entitySelector.isSenderOnly()) {
         reader.setCursor(0);
         throw PLAYER_SELECTOR_HAS_ENTITIES_EXCEPTION.createWithContext(reader);
      } else {
         return entitySelector;
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      Object var4 = context.getSource();
      if (var4 instanceof CommandSource commandSource) {
         StringReader stringReader = new StringReader(builder.getInput());
         stringReader.setCursor(builder.getStart());
         EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, EntitySelectorReader.shouldAllowAtSelectors(commandSource));

         try {
            entitySelectorReader.read();
         } catch (CommandSyntaxException var7) {
         }

         return entitySelectorReader.listSuggestions(builder, (builderx) -> {
            Collection collection = commandSource.getPlayerNames();
            Iterable iterable = this.playersOnly ? collection : Iterables.concat(collection, commandSource.getEntitySuggestions());
            CommandSource.suggestMatching((Iterable)iterable, builderx);
         });
      } else {
         return Suggestions.empty();
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader, final Object source) throws CommandSyntaxException {
      return this.parse(reader, source);
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   public static class Serializer implements ArgumentSerializer {
      private static final byte SINGLE_FLAG = 1;
      private static final byte PLAYERS_ONLY_FLAG = 2;

      public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
         int i = 0;
         if (properties.single) {
            i |= 1;
         }

         if (properties.playersOnly) {
            i |= 2;
         }

         packetByteBuf.writeByte(i);
      }

      public Properties fromPacket(PacketByteBuf packetByteBuf) {
         byte b = packetByteBuf.readByte();
         return new Properties((b & 1) != 0, (b & 2) != 0);
      }

      public void writeJson(Properties properties, JsonObject jsonObject) {
         jsonObject.addProperty("amount", properties.single ? "single" : "multiple");
         jsonObject.addProperty("type", properties.playersOnly ? "players" : "entities");
      }

      public Properties getArgumentTypeProperties(EntityArgumentType entityArgumentType) {
         return new Properties(entityArgumentType.singleTarget, entityArgumentType.playersOnly);
      }

      // $FF: synthetic method
      public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
         return this.fromPacket(buf);
      }

      public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
         final boolean single;
         final boolean playersOnly;

         Properties(final boolean single, final boolean playersOnly) {
            this.single = single;
            this.playersOnly = playersOnly;
         }

         public EntityArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new EntityArgumentType(this.single, this.playersOnly);
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
