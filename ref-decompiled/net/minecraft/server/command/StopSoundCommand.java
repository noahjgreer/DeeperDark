package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class StopSoundCommand {
   public static void register(CommandDispatcher dispatcher) {
      RequiredArgumentBuilder requiredArgumentBuilder = (RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), (SoundCategory)null, (Identifier)null);
      })).then(CommandManager.literal("*").then(CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), (SoundCategory)null, IdentifierArgumentType.getIdentifier(context, "sound"));
      })));
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory soundCategory = var2[var4];
         requiredArgumentBuilder.then(((LiteralArgumentBuilder)CommandManager.literal(soundCategory.getName()).executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), soundCategory, (Identifier)null);
         })).then(CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS)).executes((context) -> {
            return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), soundCategory, IdentifierArgumentType.getIdentifier(context, "sound"));
         })));
      }

      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("stopsound").requires(CommandManager.requirePermissionLevel(2))).then(requiredArgumentBuilder));
   }

   private static int execute(ServerCommandSource source, Collection targets, @Nullable SoundCategory category, @Nullable Identifier sound) {
      StopSoundS2CPacket stopSoundS2CPacket = new StopSoundS2CPacket(sound, category);
      Iterator var5 = targets.iterator();

      while(var5.hasNext()) {
         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)var5.next();
         serverPlayerEntity.networkHandler.sendPacket(stopSoundS2CPacket);
      }

      if (category != null) {
         if (sound != null) {
            source.sendFeedback(() -> {
               return Text.translatable("commands.stopsound.success.source.sound", Text.of(sound), category.getName());
            }, true);
         } else {
            source.sendFeedback(() -> {
               return Text.translatable("commands.stopsound.success.source.any", category.getName());
            }, true);
         }
      } else if (sound != null) {
         source.sendFeedback(() -> {
            return Text.translatable("commands.stopsound.success.sourceless.sound", Text.of(sound));
         }, true);
      } else {
         source.sendFeedback(() -> {
            return Text.translatable("commands.stopsound.success.sourceless.any");
         }, true);
      }

      return targets.size();
   }
}
