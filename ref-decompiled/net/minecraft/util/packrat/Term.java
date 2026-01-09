package net.minecraft.util.packrat;

import java.util.ArrayList;
import java.util.List;

public interface Term {
   boolean matches(ParsingState state, ParseResults results, Cut cut);

   static Term always(Symbol symbol, Object value) {
      return new AlwaysTerm(symbol, value);
   }

   @SafeVarargs
   static Term sequence(Term... terms) {
      return new SequenceTerm(terms);
   }

   @SafeVarargs
   static Term anyOf(Term... terms) {
      return new AnyOfTerm(terms);
   }

   static Term optional(Term term) {
      return new OptionalTerm(term);
   }

   static Term repeated(ParsingRuleEntry element, Symbol listName) {
      return repeated(element, listName, 0);
   }

   static Term repeated(ParsingRuleEntry element, Symbol listName, int minRepetitions) {
      return new RepeatedTerm(element, listName, minRepetitions);
   }

   static Term repeatWithPossiblyTrailingSeparator(ParsingRuleEntry element, Symbol listName, Term separator) {
      return repeatWithPossiblyTrailingSeparator(element, listName, separator, 0);
   }

   static Term repeatWithPossiblyTrailingSeparator(ParsingRuleEntry element, Symbol listName, Term separator, int minRepetitions) {
      return new RepeatWithSeparatorTerm(element, listName, separator, minRepetitions, true);
   }

   static Term repeatWithSeparator(ParsingRuleEntry element, Symbol listName, Term separator) {
      return repeatWithSeparator(element, listName, separator, 0);
   }

   static Term repeatWithSeparator(ParsingRuleEntry element, Symbol listName, Term separator, int minRepetitions) {
      return new RepeatWithSeparatorTerm(element, listName, separator, minRepetitions, false);
   }

   static Term positiveLookahead(Term term) {
      return new LookaheadTerm(term, true);
   }

   static Term negativeLookahead(Term term) {
      return new LookaheadTerm(term, false);
   }

   static Term cutting() {
      return new Term() {
         public boolean matches(ParsingState state, ParseResults results, Cut cut) {
            cut.cut();
            return true;
         }

         public String toString() {
            return "↑";
         }
      };
   }

   static Term epsilon() {
      return new Term() {
         public boolean matches(ParsingState state, ParseResults results, Cut cut) {
            return true;
         }

         public String toString() {
            return "ε";
         }
      };
   }

   static Term fail(final Object reason) {
      return new Term() {
         public boolean matches(ParsingState state, ParseResults results, Cut cut) {
            state.getErrors().add(state.getCursor(), reason);
            return false;
         }

         public String toString() {
            return "fail";
         }
      };
   }

   public static record AlwaysTerm(Symbol name, Object value) implements Term {
      public AlwaysTerm(Symbol symbol, Object object) {
         this.name = symbol;
         this.value = object;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         results.put(this.name, this.value);
         return true;
      }

      public Symbol name() {
         return this.name;
      }

      public Object value() {
         return this.value;
      }
   }

   public static record SequenceTerm(Term[] elements) implements Term {
      public SequenceTerm(Term[] terms) {
         this.elements = terms;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         int i = state.getCursor();
         Term[] var5 = this.elements;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Term term = var5[var7];
            if (!term.matches(state, results, cut)) {
               state.setCursor(i);
               return false;
            }
         }

         return true;
      }

      public Term[] elements() {
         return this.elements;
      }
   }

   public static record AnyOfTerm(Term[] elements) implements Term {
      public AnyOfTerm(Term[] terms) {
         this.elements = terms;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         Cut cut2 = state.pushCutter();

         try {
            int i = state.getCursor();
            results.duplicateFrames();
            Term[] var6 = this.elements;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Term term = var6[var8];
               if (term.matches(state, results, cut2)) {
                  results.chooseCurrentFrame();
                  boolean var10 = true;
                  return var10;
               }

               results.clearFrameValues();
               state.setCursor(i);
               if (cut2.isCut()) {
                  break;
               }
            }

            results.popFrame();
            boolean var14 = false;
            return var14;
         } finally {
            state.popCutter();
         }
      }

      public Term[] elements() {
         return this.elements;
      }
   }

   public static record OptionalTerm(Term term) implements Term {
      public OptionalTerm(Term term) {
         this.term = term;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         int i = state.getCursor();
         if (!this.term.matches(state, results, cut)) {
            state.setCursor(i);
         }

         return true;
      }

      public Term term() {
         return this.term;
      }
   }

   public static record RepeatedTerm(ParsingRuleEntry element, Symbol listName, int minRepetitions) implements Term {
      public RepeatedTerm(ParsingRuleEntry parsingRuleEntry, Symbol symbol, int i) {
         this.element = parsingRuleEntry;
         this.listName = symbol;
         this.minRepetitions = i;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         int i = state.getCursor();
         List list = new ArrayList(this.minRepetitions);

         while(true) {
            int j = state.getCursor();
            Object object = state.parse(this.element);
            if (object == null) {
               state.setCursor(j);
               if (list.size() < this.minRepetitions) {
                  state.setCursor(i);
                  return false;
               } else {
                  results.put(this.listName, list);
                  return true;
               }
            }

            list.add(object);
         }
      }

      public ParsingRuleEntry element() {
         return this.element;
      }

      public Symbol listName() {
         return this.listName;
      }

      public int minRepetitions() {
         return this.minRepetitions;
      }
   }

   public static record RepeatWithSeparatorTerm(ParsingRuleEntry element, Symbol listName, Term separator, int minRepetitions, boolean allowTrailingSeparator) implements Term {
      public RepeatWithSeparatorTerm(ParsingRuleEntry parsingRuleEntry, Symbol symbol, Term term, int i, boolean bl) {
         this.element = parsingRuleEntry;
         this.listName = symbol;
         this.separator = term;
         this.minRepetitions = i;
         this.allowTrailingSeparator = bl;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         int i = state.getCursor();
         List list = new ArrayList(this.minRepetitions);
         boolean bl = true;

         while(true) {
            int j = state.getCursor();
            if (!bl && !this.separator.matches(state, results, cut)) {
               state.setCursor(j);
               break;
            }

            int k = state.getCursor();
            Object object = state.parse(this.element);
            if (object == null) {
               if (bl) {
                  state.setCursor(k);
               } else {
                  if (!this.allowTrailingSeparator) {
                     state.setCursor(i);
                     return false;
                  }

                  state.setCursor(k);
               }
               break;
            }

            list.add(object);
            bl = false;
         }

         if (list.size() < this.minRepetitions) {
            state.setCursor(i);
            return false;
         } else {
            results.put(this.listName, list);
            return true;
         }
      }

      public ParsingRuleEntry element() {
         return this.element;
      }

      public Symbol listName() {
         return this.listName;
      }

      public Term separator() {
         return this.separator;
      }

      public int minRepetitions() {
         return this.minRepetitions;
      }

      public boolean allowTrailingSeparator() {
         return this.allowTrailingSeparator;
      }
   }

   public static record LookaheadTerm(Term term, boolean positive) implements Term {
      public LookaheadTerm(Term term, boolean bl) {
         this.term = term;
         this.positive = bl;
      }

      public boolean matches(ParsingState state, ParseResults results, Cut cut) {
         int i = state.getCursor();
         boolean bl = this.term.matches(state.getErrorSuppressingState(), results, cut);
         state.setCursor(i);
         return this.positive == bl;
      }

      public Term term() {
         return this.term;
      }

      public boolean positive() {
         return this.positive;
      }
   }
}
