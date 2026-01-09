package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class DimensionArgumentType implements ArgumentType {
   private static final Collection EXAMPLES;
   private static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION;

   public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
      return Identifier.fromCommandInput(stringReader);
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return context.getSource() instanceof CommandSource ? CommandSource.suggestIdentifiers(((CommandSource)context.getSource()).getWorldKeys().stream().map(RegistryKey::getValue), builder) : Suggestions.empty();
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static DimensionArgumentType dimension() {
      return new DimensionArgumentType();
   }

   public static ServerWorld getDimensionArgument(CommandContext context, String name) throws CommandSyntaxException {
      Identifier identifier = (Identifier)context.getArgument(name, Identifier.class);
      RegistryKey registryKey = RegistryKey.of(RegistryKeys.WORLD, identifier);
      ServerWorld serverWorld = ((ServerCommandSource)context.getSource()).getServer().getWorld(registryKey);
      if (serverWorld == null) {
         throw INVALID_DIMENSION_EXCEPTION.create(identifier);
      } else {
         return serverWorld;
      }
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   static {
      EXAMPLES = (Collection)Stream.of(World.OVERWORLD, World.NETHER).map((key) -> {
         return key.getValue().toString();
      }).collect(Collectors.toList());
      INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType((id) -> {
         return Text.stringifiedTranslatable("argument.dimension.invalid", id);
      });
   }
}
