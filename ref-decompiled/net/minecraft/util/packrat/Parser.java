package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface Parser {
   Object parse(StringReader reader) throws CommandSyntaxException;

   CompletableFuture listSuggestions(SuggestionsBuilder builder);

   default Parser map(final Function mapper) {
      return new Parser() {
         public Object parse(StringReader reader) throws CommandSyntaxException {
            return mapper.apply(Parser.this.parse(reader));
         }

         public CompletableFuture listSuggestions(SuggestionsBuilder builder) {
            return Parser.this.listSuggestions(builder);
         }
      };
   }

   default Parser withDecoding(final DynamicOps ops, final Parser encodedParser, final Codec codec, final DynamicCommandExceptionType invalidDataError) {
      return new Parser() {
         public Object parse(StringReader reader) throws CommandSyntaxException {
            int i = reader.getCursor();
            Object object = encodedParser.parse(reader);
            DataResult dataResult = codec.parse(ops, object);
            return dataResult.getOrThrow((error) -> {
               reader.setCursor(i);
               return invalidDataError.createWithContext(reader, error);
            });
         }

         public CompletableFuture listSuggestions(SuggestionsBuilder builder) {
            return Parser.this.listSuggestions(builder);
         }
      };
   }
}
