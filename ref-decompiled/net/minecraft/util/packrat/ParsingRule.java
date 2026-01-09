package net.minecraft.util.packrat;

import org.jetbrains.annotations.Nullable;

public interface ParsingRule {
   @Nullable
   Object parse(ParsingState state);

   static ParsingRule of(Term term, RuleAction action) {
      return new SimpleRule(action, term);
   }

   static ParsingRule of(Term term, StatelessAction action) {
      return new SimpleRule(action, term);
   }

   public static record SimpleRule(RuleAction action, Term child) implements ParsingRule {
      public SimpleRule(RuleAction ruleAction, Term term) {
         this.action = ruleAction;
         this.child = term;
      }

      @Nullable
      public Object parse(ParsingState state) {
         ParseResults parseResults = state.getResults();
         parseResults.pushFrame();

         Object var3;
         try {
            if (this.child.matches(state, parseResults, Cut.NOOP)) {
               var3 = this.action.run(state);
               return var3;
            }

            var3 = null;
         } finally {
            parseResults.popFrame();
         }

         return var3;
      }

      public RuleAction action() {
         return this.action;
      }

      public Term child() {
         return this.child;
      }
   }

   @FunctionalInterface
   public interface RuleAction {
      @Nullable
      Object run(ParsingState parsingState);
   }

   @FunctionalInterface
   public interface StatelessAction extends RuleAction {
      Object run(ParseResults parseResults);

      default Object run(ParsingState parsingState) {
         return this.run(parsingState.getResults());
      }
   }
}
