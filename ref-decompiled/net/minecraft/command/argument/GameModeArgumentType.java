package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

public class GameModeArgumentType implements ArgumentType {
   private static final Collection EXAMPLES;
   private static final GameMode[] VALUES;
   private static final DynamicCommandExceptionType INVALID_GAME_MODE_EXCEPTION;

   public GameMode parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readUnquotedString();
      GameMode gameMode = GameMode.byId(string, (GameMode)null);
      if (gameMode == null) {
         throw INVALID_GAME_MODE_EXCEPTION.createWithContext(stringReader, string);
      } else {
         return gameMode;
      }
   }

   public CompletableFuture listSuggestions(CommandContext context, SuggestionsBuilder builder) {
      return context.getSource() instanceof CommandSource ? CommandSource.suggestMatching(Arrays.stream(VALUES).map(GameMode::getId), builder) : Suggestions.empty();
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static GameModeArgumentType gameMode() {
      return new GameModeArgumentType();
   }

   public static GameMode getGameMode(CommandContext context, String name) throws CommandSyntaxException {
      return (GameMode)context.getArgument(name, GameMode.class);
   }

   // $FF: synthetic method
   public Object parse(final StringReader reader) throws CommandSyntaxException {
      return this.parse(reader);
   }

   static {
      EXAMPLES = (Collection)Stream.of(GameMode.SURVIVAL, GameMode.CREATIVE).map(GameMode::getId).collect(Collectors.toList());
      VALUES = GameMode.values();
      INVALID_GAME_MODE_EXCEPTION = new DynamicCommandExceptionType((gameMode) -> {
         return Text.stringifiedTranslatable("argument.gamemode.invalid", gameMode);
      });
   }
}
