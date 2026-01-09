package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class ScoreHolderArgumentType implements ArgumentType {
   public static final SuggestionProvider SUGGESTION_PROVIDER = (context, builder) -> {
      StringReader stringReader = new StringReader(builder.getInput());
      stringReader.setCursor(builder.getStart());
      EntitySelectorReader entitySelectorReader = new EntitySelectorReader(stringReader, EntitySelectorReader.shouldAllowAtSelectors((ServerCommandSource)context.getSource()));

      try {
         entitySelectorReader.read();
      } catch (CommandSyntaxException var5) {
      }

      return entitySelectorReader.listSuggestions(builder, (builderx) -> {
         CommandSource.suggestMatching((Iterable)((ServerCommandSource)context.getSource()).getPlayerNames(), builderx);
      });
   };
   private static final Collection EXAMPLES = Arrays.asList("Player", "0123", "*", "@e");
   private static final SimpleCommandExceptionType EMPTY_SCORE_HOLDER_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.scoreHolder.empty"));
   final boolean multiple;

   public ScoreHolderArgumentType(boolean multiple) {
      this.multiple = multiple;
   }

   public static ScoreHolder getScoreHolder(CommandContext context, String name) throws CommandSyntaxException {
      return (ScoreHolder)getScoreHolders(context, name).iterator().next();
   }

   public static Collection getScoreHolders(CommandContext context, String name) throws CommandSyntaxException {
      return getScoreHolders(context, name, Collections::emptyList);
   }

   public static Collection getScoreboardScoreHolders(CommandContext context, String name) throws CommandSyntaxException {
      ServerScoreboard var10002 = ((ServerCommandSource)context.getSource()).getServer().getScoreboard();
      Objects.requireNonNull(var10002);
      return getScoreHolders(context, name, var10002::getKnownScoreHolders);
   }

   public static Collection getScoreHolders(CommandContext context, String name, Supplier players) throws CommandSyntaxException {
      Collection collection = ((ScoreHolders)context.getArgument(name, ScoreHolders.class)).getNames((ServerCommandSource)context.getSource(), players);
      if (collection.isEmpty()) {
         throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
      } else {
         return collection;
      }
   }

   public static ScoreHolderArgumentType scoreHolder() {
      return new ScoreHolderArgumentType(false);
   }

   public static ScoreHolderArgumentType scoreHolders() {
      return new ScoreHolderArgumentType(true);
   }

   public ScoreHolders parse(StringReader stringReader) throws CommandSyntaxException {
      return this.parse(stringReader, true);
   }

   public ScoreHolders parse(StringReader stringReader, Object object) throws CommandSyntaxException {
      return this.parse(stringReader, EntitySelectorReader.shouldAllowAtSelectors(object));
   }

   private ScoreHolders parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '@') {
         EntitySelectorReader entitySelectorReader = new EntitySelectorReader(reader, allowAtSelectors);
         EntitySelector entitySelector = entitySelectorReader.read();
         if (!this.multiple && entitySelector.getLimit() > 1) {
            throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.createWithContext(reader);
         } else {
            return new SelectorScoreHolders(entitySelector);
         }
      } else {
         int i = reader.getCursor();

         while(reader.canRead() && reader.peek() != ' ') {
            reader.skip();
         }

         String string = reader.getString().substring(i, reader.getCursor());
         if (string.equals("*")) {
            return (source, players) -> {
               Collection collection = (Collection)players.get();
               if (collection.isEmpty()) {
                  throw EMPTY_SCORE_HOLDER_EXCEPTION.create();
               } else {
                  return collection;
               }
            };
         } else {
            List list = List.of(ScoreHolder.fromName(string));
            if (string.startsWith("#")) {
               return (source, players) -> {
                  return list;
               };
            } else {
               try {
                  UUID uUID = UUID.fromString(string);
                  return (source, holders) -> {
                     MinecraftServer minecraftServer = source.getServer();
                     ScoreHolder scoreHolder = null;
                     List list2 = null;
                     Iterator var7 = minecraftServer.getWorlds().iterator();

                     while(var7.hasNext()) {
                        ServerWorld serverWorld = (ServerWorld)var7.next();
                        Entity entity = serverWorld.getEntity(uUID);
                        if (entity != null) {
                           if (scoreHolder == null) {
                              scoreHolder = entity;
                           } else {
                              if (list2 == null) {
                                 list2 = new ArrayList();
                                 list2.add(scoreHolder);
                              }

                              list2.add(entity);
                           }
                        }
                     }

                     if (list2 != null) {
                        return list2;
                     } else if (scoreHolder != null) {
                        return List.of(scoreHolder);
                     } else {
                        return list;
                     }
                  };
               } catch (IllegalArgumentException var7) {
                  return (source, holders) -> {
                     MinecraftServer minecraftServer = source.getServer();
                     ServerPlayerEntity serverPlayerEntity = minecraftServer.getPlayerManager().getPlayer(string);
                     return serverPlayerEntity != null ? List.of(serverPlayerEntity) : list;
                  };
               }
            }
         }
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

   @FunctionalInterface
   public interface ScoreHolders {
      Collection getNames(ServerCommandSource source, Supplier holders) throws CommandSyntaxException;
   }

   public static class SelectorScoreHolders implements ScoreHolders {
      private final EntitySelector selector;

      public SelectorScoreHolders(EntitySelector selector) {
         this.selector = selector;
      }

      public Collection getNames(ServerCommandSource serverCommandSource, Supplier supplier) throws CommandSyntaxException {
         List list = this.selector.getEntities(serverCommandSource);
         if (list.isEmpty()) {
            throw EntityArgumentType.ENTITY_NOT_FOUND_EXCEPTION.create();
         } else {
            return List.copyOf(list);
         }
      }
   }

   public static class Serializer implements ArgumentSerializer {
      private static final byte MULTIPLE_FLAG = 1;

      public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
         int i = 0;
         if (properties.multiple) {
            i |= 1;
         }

         packetByteBuf.writeByte(i);
      }

      public Properties fromPacket(PacketByteBuf packetByteBuf) {
         byte b = packetByteBuf.readByte();
         boolean bl = (b & 1) != 0;
         return new Properties(bl);
      }

      public void writeJson(Properties properties, JsonObject jsonObject) {
         jsonObject.addProperty("amount", properties.multiple ? "multiple" : "single");
      }

      public Properties getArgumentTypeProperties(ScoreHolderArgumentType scoreHolderArgumentType) {
         return new Properties(scoreHolderArgumentType.multiple);
      }

      // $FF: synthetic method
      public ArgumentSerializer.ArgumentTypeProperties fromPacket(final PacketByteBuf buf) {
         return this.fromPacket(buf);
      }

      public final class Properties implements ArgumentSerializer.ArgumentTypeProperties {
         final boolean multiple;

         Properties(final boolean multiple) {
            this.multiple = multiple;
         }

         public ScoreHolderArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
            return new ScoreHolderArgumentType(this.multiple);
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
