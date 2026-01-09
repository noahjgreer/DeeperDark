package net.minecraft.util.packrat;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class ParsingRules {
   private final Map rules = new IdentityHashMap();

   public ParsingRuleEntry set(Symbol symbol, ParsingRule rule) {
      RuleEntryImpl ruleEntryImpl = (RuleEntryImpl)this.rules.computeIfAbsent(symbol, RuleEntryImpl::new);
      if (ruleEntryImpl.rule != null) {
         throw new IllegalArgumentException("Trying to override rule: " + String.valueOf(symbol));
      } else {
         ruleEntryImpl.rule = rule;
         return ruleEntryImpl;
      }
   }

   public ParsingRuleEntry set(Symbol symbol, Term term, ParsingRule.RuleAction action) {
      return this.set(symbol, ParsingRule.of(term, action));
   }

   public ParsingRuleEntry set(Symbol symbol, Term term, ParsingRule.StatelessAction action) {
      return this.set(symbol, ParsingRule.of(term, action));
   }

   public void ensureBound() {
      List list = this.rules.entrySet().stream().filter((entry) -> {
         return entry.getValue() == null;
      }).map(Map.Entry::getKey).toList();
      if (!list.isEmpty()) {
         throw new IllegalStateException("Unbound names: " + String.valueOf(list));
      }
   }

   public ParsingRuleEntry get(Symbol symbol) {
      return (ParsingRuleEntry)Objects.requireNonNull((RuleEntryImpl)this.rules.get(symbol), () -> {
         return "No rule called " + String.valueOf(symbol);
      });
   }

   public ParsingRuleEntry getOrCreate(Symbol symbol) {
      return this.getOrCreateInternal(symbol);
   }

   private RuleEntryImpl getOrCreateInternal(Symbol symbol) {
      return (RuleEntryImpl)this.rules.computeIfAbsent(symbol, RuleEntryImpl::new);
   }

   public Term term(Symbol symbol) {
      return new RuleTerm(this.getOrCreateInternal(symbol), symbol);
   }

   public Term term(Symbol symbol, Symbol nameToStore) {
      return new RuleTerm(this.getOrCreateInternal(symbol), nameToStore);
   }

   static class RuleEntryImpl implements ParsingRuleEntry, Supplier {
      private final Symbol symbol;
      @Nullable
      ParsingRule rule;

      private RuleEntryImpl(Symbol symbol) {
         this.symbol = symbol;
      }

      public Symbol getSymbol() {
         return this.symbol;
      }

      public ParsingRule getRule() {
         return (ParsingRule)Objects.requireNonNull(this.rule, this);
      }

      public String get() {
         return "Unbound rule " + String.valueOf(this.symbol);
      }

      // $FF: synthetic method
      public Object get() {
         return this.get();
      }
   }

   private static record RuleTerm(RuleEntryImpl ruleToParse, Symbol nameToStore) implements Term {
      RuleTerm(RuleEntryImpl ruleEntryImpl, Symbol symbol) {
         this.ruleToParse = ruleEntryImpl;
         this.nameToStore = symbol;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         Object object = state.parse(this.ruleToParse);
         if (object == null) {
            return false;
         } else {
            results.put(this.nameToStore, object);
            return true;
         }
      }

      public RuleEntryImpl ruleToParse() {
         return this.ruleToParse;
      }

      public Symbol nameToStore() {
         return this.nameToStore;
      }
   }
}
