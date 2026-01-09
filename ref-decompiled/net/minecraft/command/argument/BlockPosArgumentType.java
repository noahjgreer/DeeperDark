package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPosArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType UNLOADED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.unloaded"));
   public static final SimpleCommandExceptionType OUT_OF_WORLD_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.outofworld"));
   public static final SimpleCommandExceptionType OUT_OF_BOUNDS_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("argument.pos.outofbounds"));

   public static BlockPosArgumentType blockPos() {
      return new BlockPosArgumentType();
   }

   public static BlockPos getLoadedBlockPos(CommandContext context, String name) throws CommandSyntaxException {
      ServerWorld serverWorld = ((ServerCommandSource)context.getSource()).getWorld();
      return getLoadedBlockPos(context, serverWorld, name);
   }

   public static BlockPos getLoadedBlockPos(CommandContext context, ServerWorld world, String name) throws CommandSyntaxException {
      BlockPos blockPos = getBlockPos(context, name);
      if (!world.isChunkLoaded(blockPos)) {
         throw UNLOADED_EXCEPTION.create();
      } else if (!world.isInBuildLimit(blockPos)) {
         throw OUT_OF_WORLD_EXCEPTION.create();
      } else {
         return blockPos;
      }
   }

   public static BlockPos getBlockPos(CommandContext context, String name) {
      return ((PosArgument)context.getArgument(name, PosArgument.class)).toAbsoluteBlockPos((ServerCommandSource)context.getSource());
   }

   public static BlockPos getValidBlockPos(CommandContext context, String name) throws CommandSyntaxException {
      BlockPos blockPos = getBlockPos(context, name);
      if (!World.isValid(blockPos)) {
         throw OUT_OF_BOUNDS_EXCEPTION.create();
      } else {
         return blockPos;
      }
   }

   public PosArgument parse(StringReader stringReader) throws CommandSyntaxException {
      return (PosArgument)(stringReader.canRead() && stringReader.peek() == '^' ? LookingPosArgument.parse(stringReader) : DefaultPosArgument.parse(stringReader));
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      if (!(context.getSource() instanceof CommandSource)) {
         return Suggestions.empty();
      } else {
         String string = builder.getRemaining();
         Object collection;
         if (!string.isEmpty() && string.charAt(0) == '^') {
            collection = Collections.singleton(CommandSource.RelativePosition.ZERO_LOCAL);
         } else {
            collection = ((CommandSource)context.getSource()).getBlockPositionSuggestions();
         }

         return CommandSource.suggestPositions(string, (Collection)collection, builder, CommandManager.getCommandValidator(this::parse));
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
