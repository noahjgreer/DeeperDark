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

public class ItemStackArgumentType implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
   private final ItemStringReader reader;

   public ItemStackArgumentType(CommandRegistryAccess commandRegistryAccess) {
      this.reader = new ItemStringReader(commandRegistryAccess);
   }

   public static ItemStackArgumentType itemStack(CommandRegistryAccess commandRegistryAccess) {
      return new ItemStackArgumentType(commandRegistryAccess);
   }

   public ItemStackArgument parse(StringReader stringReader) throws CommandSyntaxException {
      ItemStringReader.ItemResult itemResult = this.reader.consume(stringReader);
      return new ItemStackArgument(itemResult.item(), itemResult.components());
   }

   public static ItemStackArgument getItemStackArgument(CommandContext context, String name) {
      return (ItemStackArgument)context.getArgument(name, ItemStackArgument.class);
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return this.reader.getSuggestions(builder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }
}
