package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Iterator;
import java.util.List;

@FunctionalInterface
public interface OrderedText {
   OrderedText EMPTY = (visitor) -> {
      return true;
   };

   boolean accept(CharacterVisitor visitor);

   static OrderedText styled(int codePoint, Style style) {
      return (visitor) -> {
         return visitor.accept(0, style, codePoint);
      };
   }

   static OrderedText styledForwardsVisitedString(String string, Style style) {
      return string.isEmpty() ? EMPTY : (visitor) -> {
         return TextVisitFactory.visitForwards(string, style, visitor);
      };
   }

   static OrderedText styledForwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
      return string.isEmpty() ? EMPTY : (visitor) -> {
         return TextVisitFactory.visitForwards(string, style, map(visitor, codePointMapper));
      };
   }

   static OrderedText styledBackwardsVisitedString(String string, Style style) {
      return string.isEmpty() ? EMPTY : (visitor) -> {
         return TextVisitFactory.visitBackwards(string, style, visitor);
      };
   }

   static OrderedText styledBackwardsVisitedString(String string, Style style, Int2IntFunction codePointMapper) {
      return string.isEmpty() ? EMPTY : (visitor) -> {
         return TextVisitFactory.visitBackwards(string, style, map(visitor, codePointMapper));
      };
   }

   static CharacterVisitor map(CharacterVisitor visitor, Int2IntFunction codePointMapper) {
      return (charIndex, style, charPoint) -> {
         return visitor.accept(charIndex, style, (Integer)codePointMapper.apply(charPoint));
      };
   }

   static OrderedText empty() {
      return EMPTY;
   }

   static OrderedText of(OrderedText text) {
      return text;
   }

   static OrderedText concat(OrderedText first, OrderedText second) {
      return innerConcat(first, second);
   }

   static OrderedText concat(OrderedText... texts) {
      return innerConcat(ImmutableList.copyOf(texts));
   }

   static OrderedText concat(List texts) {
      int i = texts.size();
      switch (i) {
         case 0:
            return EMPTY;
         case 1:
            return (OrderedText)texts.get(0);
         case 2:
            return innerConcat((OrderedText)texts.get(0), (OrderedText)texts.get(1));
         default:
            return innerConcat(ImmutableList.copyOf(texts));
      }
   }

   static OrderedText innerConcat(OrderedText text1, OrderedText text2) {
      return (visitor) -> {
         return text1.accept(visitor) && text2.accept(visitor);
      };
   }

   static OrderedText innerConcat(List texts) {
      return (visitor) -> {
         Iterator var2 = texts.iterator();

         OrderedText orderedText;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            orderedText = (OrderedText)var2.next();
         } while(orderedText.accept(visitor));

         return false;
      };
   }
}
