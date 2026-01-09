package net.minecraft.util.packrat;

import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public abstract class ParsingStateImpl implements ParsingState {
   private MemoizedData[] memoStack = new MemoizedData[256];
   private final ParseErrorList errors;
   private final ParseResults results = new ParseResults();
   private Cutter[] cutters = new Cutter[16];
   private int topCutterIndex;
   private final ErrorSuppressing errorSuppressingState = new ErrorSuppressing();

   protected ParsingStateImpl(ParseErrorList errors) {
      this.errors = errors;
   }

   public ParseResults getResults() {
      return this.results;
   }

   public ParseErrorList getErrors() {
      return this.errors;
   }

   @Nullable
   public Object parse(ParsingRuleEntry rule) {
      int i = this.getCursor();
      MemoizedData memoizedData = this.pushMemoizedData(i);
      int j = memoizedData.get(rule.getSymbol());
      if (j != -1) {
         MemoizedValue memoizedValue = memoizedData.get(j);
         if (memoizedValue != null) {
            if (memoizedValue == ParsingStateImpl.MemoizedValue.EMPTY) {
               return null;
            }

            this.setCursor(memoizedValue.markAfterParse);
            return memoizedValue.value;
         }
      } else {
         j = memoizedData.push(rule.getSymbol());
      }

      Object object = rule.getRule().parse(this);
      MemoizedValue memoizedValue2;
      if (object == null) {
         memoizedValue2 = ParsingStateImpl.MemoizedValue.empty();
      } else {
         int k = this.getCursor();
         memoizedValue2 = new MemoizedValue(object, k);
      }

      memoizedData.put(j, memoizedValue2);
      return object;
   }

   private MemoizedData pushMemoizedData(int cursor) {
      int i = this.memoStack.length;
      if (cursor >= i) {
         int j = Util.nextCapacity(i, cursor + 1);
         MemoizedData[] memoizedDatas = new MemoizedData[j];
         System.arraycopy(this.memoStack, 0, memoizedDatas, 0, i);
         this.memoStack = memoizedDatas;
      }

      MemoizedData memoizedData = this.memoStack[cursor];
      if (memoizedData == null) {
         memoizedData = new MemoizedData();
         this.memoStack[cursor] = memoizedData;
      }

      return memoizedData;
   }

   public Cut pushCutter() {
      int i = this.cutters.length;
      int j;
      if (this.topCutterIndex >= i) {
         j = Util.nextCapacity(i, this.topCutterIndex + 1);
         Cutter[] cutters = new Cutter[j];
         System.arraycopy(this.cutters, 0, cutters, 0, i);
         this.cutters = cutters;
      }

      j = this.topCutterIndex++;
      Cutter cutter = this.cutters[j];
      if (cutter == null) {
         cutter = new Cutter();
         this.cutters[j] = cutter;
      } else {
         cutter.reset();
      }

      return cutter;
   }

   public void popCutter() {
      --this.topCutterIndex;
   }

   public ParsingState getErrorSuppressingState() {
      return this.errorSuppressingState;
   }

   private static class MemoizedData {
      public static final int SIZE_PER_SYMBOL = 2;
      private static final int MISSING = -1;
      private Object[] values = new Object[16];
      private int top;

      MemoizedData() {
      }

      public int get(Symbol symbol) {
         for(int i = 0; i < this.top; i += 2) {
            if (this.values[i] == symbol) {
               return i;
            }
         }

         return -1;
      }

      public int push(Symbol symbol) {
         int i = this.top;
         this.top += 2;
         int j = i + 1;
         int k = this.values.length;
         if (j >= k) {
            int l = Util.nextCapacity(k, j + 1);
            Object[] objects = new Object[l];
            System.arraycopy(this.values, 0, objects, 0, k);
            this.values = objects;
         }

         this.values[i] = symbol;
         return i;
      }

      @Nullable
      public MemoizedValue get(int index) {
         return (MemoizedValue)this.values[index + 1];
      }

      public void put(int index, MemoizedValue value) {
         this.values[index + 1] = value;
      }
   }

   private static class Cutter implements Cut {
      private boolean cut;

      Cutter() {
      }

      public void cut() {
         this.cut = true;
      }

      public boolean isCut() {
         return this.cut;
      }

      public void reset() {
         this.cut = false;
      }
   }

   class ErrorSuppressing implements ParsingState {
      private final ParseErrorList errors = new ParseErrorList.Noop();

      public ParseErrorList getErrors() {
         return this.errors;
      }

      public ParseResults getResults() {
         return ParsingStateImpl.this.getResults();
      }

      @Nullable
      public Object parse(ParsingRuleEntry rule) {
         return ParsingStateImpl.this.parse(rule);
      }

      public Object getReader() {
         return ParsingStateImpl.this.getReader();
      }

      public int getCursor() {
         return ParsingStateImpl.this.getCursor();
      }

      public void setCursor(int cursor) {
         ParsingStateImpl.this.setCursor(cursor);
      }

      public Cut pushCutter() {
         return ParsingStateImpl.this.pushCutter();
      }

      public void popCutter() {
         ParsingStateImpl.this.popCutter();
      }

      public ParsingState getErrorSuppressingState() {
         return this;
      }
   }

   private static record MemoizedValue(@Nullable Object value, int markAfterParse) {
      @Nullable
      final Object value;
      final int markAfterParse;
      public static final MemoizedValue EMPTY = new MemoizedValue((Object)null, -1);

      MemoizedValue(@Nullable Object object, int i) {
         this.value = object;
         this.markAfterParse = i;
      }

      public static MemoizedValue empty() {
         return EMPTY;
      }

      @Nullable
      public Object value() {
         return this.value;
      }

      public int markAfterParse() {
         return this.markAfterParse;
      }
   }
}
