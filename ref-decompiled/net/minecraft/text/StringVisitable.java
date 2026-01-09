package net.minecraft.text;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;

public interface StringVisitable {
   Optional TERMINATE_VISIT = Optional.of(Unit.INSTANCE);
   StringVisitable EMPTY = new StringVisitable() {
      public Optional visit(Visitor visitor) {
         return Optional.empty();
      }

      public Optional visit(StyledVisitor styledVisitor, Style style) {
         return Optional.empty();
      }
   };

   Optional visit(Visitor visitor);

   Optional visit(StyledVisitor styledVisitor, Style style);

   static StringVisitable plain(final String string) {
      return new StringVisitable() {
         public Optional visit(Visitor visitor) {
            return visitor.accept(string);
         }

         public Optional visit(StyledVisitor styledVisitor, Style style) {
            return styledVisitor.accept(style, string);
         }
      };
   }

   static StringVisitable styled(final String string, final Style style) {
      return new StringVisitable() {
         public Optional visit(Visitor visitor) {
            return visitor.accept(string);
         }

         public Optional visit(StyledVisitor styledVisitor, Style stylex) {
            return styledVisitor.accept(style.withParent(stylex), string);
         }
      };
   }

   static StringVisitable concat(StringVisitable... visitables) {
      return concat((List)ImmutableList.copyOf(visitables));
   }

   static StringVisitable concat(final List visitables) {
      return new StringVisitable() {
         public Optional visit(Visitor visitor) {
            Iterator var2 = visitables.iterator();

            Optional optional;
            do {
               if (!var2.hasNext()) {
                  return Optional.empty();
               }

               StringVisitable stringVisitable = (StringVisitable)var2.next();
               optional = stringVisitable.visit(visitor);
            } while(!optional.isPresent());

            return optional;
         }

         public Optional visit(StyledVisitor styledVisitor, Style style) {
            Iterator var3 = visitables.iterator();

            Optional optional;
            do {
               if (!var3.hasNext()) {
                  return Optional.empty();
               }

               StringVisitable stringVisitable = (StringVisitable)var3.next();
               optional = stringVisitable.visit(styledVisitor, style);
            } while(!optional.isPresent());

            return optional;
         }
      };
   }

   default String getString() {
      StringBuilder stringBuilder = new StringBuilder();
      this.visit((string) -> {
         stringBuilder.append(string);
         return Optional.empty();
      });
      return stringBuilder.toString();
   }

   public interface Visitor {
      Optional accept(String asString);
   }

   public interface StyledVisitor {
      Optional accept(Style style, String asString);
   }
}
