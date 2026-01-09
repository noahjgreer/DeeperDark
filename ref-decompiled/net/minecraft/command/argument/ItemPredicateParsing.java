package net.minecraft.command.argument;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtParsingRule;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.packrat.AnyIdParsingRule;
import net.minecraft.util.packrat.IdentifiableParsingRule;
import net.minecraft.util.packrat.Literals;
import net.minecraft.util.packrat.PackratParser;
import net.minecraft.util.packrat.ParseResults;
import net.minecraft.util.packrat.ParsingRuleEntry;
import net.minecraft.util.packrat.ParsingRules;
import net.minecraft.util.packrat.Symbol;
import net.minecraft.util.packrat.Term;

public class ItemPredicateParsing {
   public static PackratParser createParser(Callbacks callbacks) {
      Symbol symbol = Symbol.of("top");
      Symbol symbol2 = Symbol.of("type");
      Symbol symbol3 = Symbol.of("any_type");
      Symbol symbol4 = Symbol.of("element_type");
      Symbol symbol5 = Symbol.of("tag_type");
      Symbol symbol6 = Symbol.of("conditions");
      Symbol symbol7 = Symbol.of("alternatives");
      Symbol symbol8 = Symbol.of("term");
      Symbol symbol9 = Symbol.of("negation");
      Symbol symbol10 = Symbol.of("test");
      Symbol symbol11 = Symbol.of("component_type");
      Symbol symbol12 = Symbol.of("predicate_type");
      Symbol symbol13 = Symbol.of("id");
      Symbol symbol14 = Symbol.of("tag");
      ParsingRules parsingRules = new ParsingRules();
      ParsingRuleEntry parsingRuleEntry = parsingRules.set(symbol13, AnyIdParsingRule.INSTANCE);
      ParsingRuleEntry parsingRuleEntry2 = parsingRules.set(symbol, Term.anyOf(Term.sequence(parsingRules.term(symbol2), Literals.character('['), Term.cutting(), Term.optional(parsingRules.term(symbol6)), Literals.character(']')), parsingRules.term(symbol2)), (results) -> {
         ImmutableList.Builder builder = ImmutableList.builder();
         Optional var10000 = (Optional)results.getOrThrow(symbol2);
         Objects.requireNonNull(builder);
         var10000.ifPresent(builder::add);
         List list = (List)results.get(symbol6);
         if (list != null) {
            builder.addAll(list);
         }

         return builder.build();
      });
      parsingRules.set(symbol2, Term.anyOf(parsingRules.term(symbol4), Term.sequence(Literals.character('#'), Term.cutting(), parsingRules.term(symbol5)), parsingRules.term(symbol3)), (results) -> {
         return Optional.ofNullable(results.getAny(symbol4, symbol5));
      });
      parsingRules.set(symbol3, Literals.character('*'), (results) -> {
         return Unit.INSTANCE;
      });
      parsingRules.set(symbol4, new ItemParsingRule(parsingRuleEntry, callbacks));
      parsingRules.set(symbol5, new TagParsingRule(parsingRuleEntry, callbacks));
      parsingRules.set(symbol6, Term.sequence(parsingRules.term(symbol7), Term.optional(Term.sequence(Literals.character(','), parsingRules.term(symbol6)))), (results) -> {
         Object object = callbacks.anyOf((List)results.getOrThrow(symbol7));
         return (List)Optional.ofNullable((List)results.get(symbol6)).map((predicates) -> {
            return Util.withPrepended(object, predicates);
         }).orElse(List.of(object));
      });
      parsingRules.set(symbol7, Term.sequence(parsingRules.term(symbol8), Term.optional(Term.sequence(Literals.character('|'), parsingRules.term(symbol7)))), (results) -> {
         Object object = results.getOrThrow(symbol8);
         return (List)Optional.ofNullable((List)results.get(symbol7)).map((predicates) -> {
            return Util.withPrepended(object, predicates);
         }).orElse(List.of(object));
      });
      parsingRules.set(symbol8, Term.anyOf(parsingRules.term(symbol10), Term.sequence(Literals.character('!'), parsingRules.term(symbol9))), (results) -> {
         return results.getAnyOrThrow(symbol10, symbol9);
      });
      parsingRules.set(symbol9, parsingRules.term(symbol10), (results) -> {
         return callbacks.negate(results.getOrThrow(symbol10));
      });
      parsingRules.set(symbol10, Term.anyOf(Term.sequence(parsingRules.term(symbol11), Literals.character('='), Term.cutting(), parsingRules.term(symbol14)), Term.sequence(parsingRules.term(symbol12), Literals.character('~'), Term.cutting(), parsingRules.term(symbol14)), parsingRules.term(symbol11)), (state) -> {
         ParseResults parseResults = state.getResults();
         Object object = parseResults.get(symbol12);

         try {
            if (object != null) {
               Dynamic dynamic = (Dynamic)parseResults.getOrThrow(symbol14);
               return callbacks.subPredicatePredicate((ImmutableStringReader)state.getReader(), object, dynamic);
            } else {
               Object object2 = parseResults.getOrThrow(symbol11);
               Dynamic dynamic2 = (Dynamic)parseResults.get(symbol14);
               return dynamic2 != null ? callbacks.componentMatchPredicate((ImmutableStringReader)state.getReader(), object2, dynamic2) : callbacks.componentPresencePredicate((ImmutableStringReader)state.getReader(), object2);
            }
         } catch (CommandSyntaxException var9) {
            state.getErrors().add(state.getCursor(), var9);
            return null;
         }
      });
      parsingRules.set(symbol11, new ComponentParsingRule(parsingRuleEntry, callbacks));
      parsingRules.set(symbol12, new SubPredicateParsingRule(parsingRuleEntry, callbacks));
      parsingRules.set(symbol14, new NbtParsingRule(NbtOps.INSTANCE));
      return new PackratParser(parsingRules, parsingRuleEntry2);
   }

   static class ItemParsingRule extends IdentifiableParsingRule {
      ItemParsingRule(ParsingRuleEntry idParsingRule, Callbacks callbacks) {
         super(idParsingRule, callbacks);
      }

      protected Object parse(ImmutableStringReader reader, Identifier id) throws Exception {
         return ((Callbacks)this.callbacks).itemMatchPredicate(reader, id);
      }

      public Stream possibleIds() {
         return ((Callbacks)this.callbacks).streamItemIds();
      }
   }

   public interface Callbacks {
      Object itemMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream streamItemIds();

      Object tagMatchPredicate(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream streamTags();

      Object componentCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream streamComponentIds();

      Object componentMatchPredicate(ImmutableStringReader reader, Object check, Dynamic dynamic) throws CommandSyntaxException;

      Object componentPresencePredicate(ImmutableStringReader reader, Object check);

      Object subPredicateCheck(ImmutableStringReader reader, Identifier id) throws CommandSyntaxException;

      Stream streamSubPredicateIds();

      Object subPredicatePredicate(ImmutableStringReader reader, Object check, Dynamic dynamic) throws CommandSyntaxException;

      Object negate(Object predicate);

      Object anyOf(List predicates);
   }

   static class TagParsingRule extends IdentifiableParsingRule {
      TagParsingRule(ParsingRuleEntry idParsingRule, Callbacks callbacks) {
         super(idParsingRule, callbacks);
      }

      protected Object parse(ImmutableStringReader reader, Identifier id) throws Exception {
         return ((Callbacks)this.callbacks).tagMatchPredicate(reader, id);
      }

      public Stream possibleIds() {
         return ((Callbacks)this.callbacks).streamTags();
      }
   }

   static class ComponentParsingRule extends IdentifiableParsingRule {
      ComponentParsingRule(ParsingRuleEntry idParsingRule, Callbacks callbacks) {
         super(idParsingRule, callbacks);
      }

      protected Object parse(ImmutableStringReader reader, Identifier id) throws Exception {
         return ((Callbacks)this.callbacks).componentCheck(reader, id);
      }

      public Stream possibleIds() {
         return ((Callbacks)this.callbacks).streamComponentIds();
      }
   }

   static class SubPredicateParsingRule extends IdentifiableParsingRule {
      SubPredicateParsingRule(ParsingRuleEntry idParsingRule, Callbacks callbacks) {
         super(idParsingRule, callbacks);
      }

      protected Object parse(ImmutableStringReader reader, Identifier id) throws Exception {
         return ((Callbacks)this.callbacks).subPredicateCheck(reader, id);
      }

      public Stream possibleIds() {
         return ((Callbacks)this.callbacks).streamSubPredicateIds();
      }
   }
}
