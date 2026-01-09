package net.minecraft.util.packrat;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;

public record PackratParser(ParsingRules rules, ParsingRuleEntry top) implements Parser {
   public PackratParser(ParsingRules parsingRules, ParsingRuleEntry parsingRuleEntry) {
      parsingRules.ensureBound();
      this.rules = parsingRules;
      this.top = parsingRuleEntry;
   }

   public Optional startParsing(ParsingState state) {
      return state.startParsing(this.top);
   }

   public Object parse(StringReader reader) throws CommandSyntaxException {
      ParseErrorList.Impl impl = new ParseErrorList.Impl();
      ReaderBackedParsingState readerBackedParsingState = new ReaderBackedParsingState(impl, reader);
      Optional optional = this.startParsing(readerBackedParsingState);
      if (optional.isPresent()) {
         return optional.get();
      } else {
         List list = impl.getErrors();
         List list2 = list.stream().mapMulti((error, callback) -> {
            Object object = error.reason();
            if (object instanceof CursorExceptionType cursorExceptionType) {
               callback.accept(cursorExceptionType.create(reader.getString(), error.cursor()));
            } else {
               object = error.reason();
               if (object instanceof Exception exception) {
                  callback.accept(exception);
               }
            }

         }).toList();
         Iterator var7 = list2.iterator();

         Exception exception;
         do {
            if (!var7.hasNext()) {
               if (list2.size() == 1) {
                  Object var11 = list2.get(0);
                  if (var11 instanceof RuntimeException) {
                     RuntimeException runtimeException = (RuntimeException)var11;
                     throw runtimeException;
                  }
               }

               Stream var10002 = list.stream().map(ParseError::toString);
               throw new IllegalStateException("Failed to parse: " + (String)var10002.collect(Collectors.joining(", ")));
            }

            exception = (Exception)var7.next();
         } while(!(exception instanceof CommandSyntaxException));

         CommandSyntaxException commandSyntaxException = (CommandSyntaxException)exception;
         throw commandSyntaxException;
      }
   }

   public CompletableFuture listSuggestions(SuggestionsBuilder builder) {
      StringReader stringReader = new StringReader(builder.getInput());
      stringReader.setCursor(builder.getStart());
      ParseErrorList.Impl impl = new ParseErrorList.Impl();
      ReaderBackedParsingState readerBackedParsingState = new ReaderBackedParsingState(impl, stringReader);
      this.startParsing(readerBackedParsingState);
      List list = impl.getErrors();
      if (list.isEmpty()) {
         return builder.buildFuture();
      } else {
         SuggestionsBuilder suggestionsBuilder = builder.createOffset(impl.getCursor());
         Iterator var7 = list.iterator();

         while(var7.hasNext()) {
            ParseError parseError = (ParseError)var7.next();
            Suggestable var10 = parseError.suggestions();
            if (var10 instanceof IdentifierSuggestable) {
               IdentifierSuggestable identifierSuggestable = (IdentifierSuggestable)var10;
               CommandSource.suggestIdentifiers(identifierSuggestable.possibleIds(), suggestionsBuilder);
            } else {
               CommandSource.suggestMatching(parseError.suggestions().possibleValues(readerBackedParsingState), suggestionsBuilder);
            }
         }

         return suggestionsBuilder.buildFuture();
      }
   }

   public ParsingRules rules() {
      return this.rules;
   }

   public ParsingRuleEntry top() {
      return this.top;
   }
}
