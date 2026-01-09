package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class PlaySoundCommand {
   private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.playsound.failed"));

   public static void register(CommandDispatcher dispatcher) {
      RequiredArgumentBuilder requiredArgumentBuilder = (RequiredArgumentBuilder)CommandManager.argument("sound", IdentifierArgumentType.identifier()).suggests(SuggestionProviders.cast(SuggestionProviders.AVAILABLE_SOUNDS)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), toList(((ServerCommandSource)context.getSource()).getPlayer()), IdentifierArgumentType.getIdentifier(context, "sound"), SoundCategory.MASTER, ((ServerCommandSource)context.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
      });
      SoundCategory[] var2 = SoundCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SoundCategory soundCategory = var2[var4];
         requiredArgumentBuilder.then(makeArgumentsForCategory(soundCategory));
      }

      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("playsound").requires(CommandManager.requirePermissionLevel(2))).then(requiredArgumentBuilder));
   }

   private static LiteralArgumentBuilder makeArgumentsForCategory(SoundCategory category) {
      return (LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(category.getName()).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), toList(((ServerCommandSource)context.getSource()).getPlayer()), IdentifierArgumentType.getIdentifier(context, "sound"), category, ((ServerCommandSource)context.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)CommandManager.argument("targets", EntityArgumentType.players()).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IdentifierArgumentType.getIdentifier(context, "sound"), category, ((ServerCommandSource)context.getSource()).getPosition(), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)CommandManager.argument("pos", Vec3ArgumentType.vec3()).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IdentifierArgumentType.getIdentifier(context, "sound"), category, Vec3ArgumentType.getVec3(context, "pos"), 1.0F, 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)CommandManager.argument("volume", FloatArgumentType.floatArg(0.0F)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IdentifierArgumentType.getIdentifier(context, "sound"), category, Vec3ArgumentType.getVec3(context, "pos"), (Float)context.getArgument("volume", Float.class), 1.0F, 0.0F);
      })).then(((RequiredArgumentBuilder)CommandManager.argument("pitch", FloatArgumentType.floatArg(0.0F, 2.0F)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IdentifierArgumentType.getIdentifier(context, "sound"), category, Vec3ArgumentType.getVec3(context, "pos"), (Float)context.getArgument("volume", Float.class), (Float)context.getArgument("pitch", Float.class), 0.0F);
      })).then(CommandManager.argument("minVolume", FloatArgumentType.floatArg(0.0F, 1.0F)).executes((context) -> {
         return execute((ServerCommandSource)context.getSource(), EntityArgumentType.getPlayers(context, "targets"), IdentifierArgumentType.getIdentifier(context, "sound"), category, Vec3ArgumentType.getVec3(context, "pos"), (Float)context.getArgument("volume", Float.class), (Float)context.getArgument("pitch", Float.class), (Float)context.getArgument("minVolume", Float.class));
      }))))));
   }

   private static Collection toList(@Nullable ServerPlayerEntity player) {
      return player != null ? List.of(player) : List.of();
   }

   private static int execute(ServerCommandSource source, Collection targets, Identifier sound, SoundCategory category, Vec3d pos, float volume, float pitch, float minVolume) throws CommandSyntaxException {
      RegistryEntry registryEntry = RegistryEntry.of(SoundEvent.of(sound));
      double d = (double)MathHelper.square(((SoundEvent)registryEntry.value()).getDistanceToTravel(volume));
      ServerWorld serverWorld = source.getWorld();
      long l = serverWorld.getRandom().nextLong();
      List list = new ArrayList();
      Iterator var15 = targets.iterator();

      while(true) {
         ServerPlayerEntity serverPlayerEntity;
         Vec3d vec3d;
         float i;
         while(true) {
            do {
               if (!var15.hasNext()) {
                  int k = list.size();
                  if (k == 0) {
                     throw FAILED_EXCEPTION.create();
                  }

                  if (k == 1) {
                     source.sendFeedback(() -> {
                        return Text.translatable("commands.playsound.success.single", Text.of(sound), ((ServerPlayerEntity)list.getFirst()).getDisplayName());
                     }, true);
                  } else {
                     source.sendFeedback(() -> {
                        return Text.translatable("commands.playsound.success.multiple", Text.of(sound), k);
                     }, true);
                  }

                  return k;
               }

               serverPlayerEntity = (ServerPlayerEntity)var15.next();
            } while(serverPlayerEntity.getWorld() != serverWorld);

            double e = pos.x - serverPlayerEntity.getX();
            double f = pos.y - serverPlayerEntity.getY();
            double g = pos.z - serverPlayerEntity.getZ();
            double h = e * e + f * f + g * g;
            vec3d = pos;
            i = volume;
            if (!(h > d)) {
               break;
            }

            if (!(minVolume <= 0.0F)) {
               double j = Math.sqrt(h);
               vec3d = new Vec3d(serverPlayerEntity.getX() + e / j * 2.0, serverPlayerEntity.getY() + f / j * 2.0, serverPlayerEntity.getZ() + g / j * 2.0);
               i = minVolume;
               break;
            }
         }

         serverPlayerEntity.networkHandler.sendPacket(new PlaySoundS2CPacket(registryEntry, category, vec3d.getX(), vec3d.getY(), vec3d.getZ(), i, pitch, l));
         list.add(serverPlayerEntity);
      }
   }
}
