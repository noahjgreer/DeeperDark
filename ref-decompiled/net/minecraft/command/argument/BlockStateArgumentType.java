package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class BlockStateArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");
   private final RegistryWrapper registryWrapper;

   public BlockStateArgumentType(CommandRegistryAccess commandRegistryAccess) {
      this.registryWrapper = commandRegistryAccess.getOrThrow(RegistryKeys.BLOCK);
   }

   public static BlockStateArgumentType blockState(CommandRegistryAccess commandRegistryAccess) {
      return new BlockStateArgumentType(commandRegistryAccess);
   }

   public BlockStateArgument parse(StringReader stringReader) throws CommandSyntaxException {
      BlockArgumentParser.BlockResult blockResult = BlockArgumentParser.block(this.registryWrapper, stringReader, true);
      return new BlockStateArgument(blockResult.blockState(), blockResult.properties().keySet(), blockResult.nbt());
   }

   public static BlockStateArgument getBlockState(CommandContext context, String name) {
      return (BlockStateArgument)context.getArgument(name, BlockStateArgument.class);
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return BlockArgumentParser.getSuggestions(this.registryWrapper, builder, false, true);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
