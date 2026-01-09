package net.minecraft.util;

import org.jetbrains.annotations.Nullable;

public interface TypeFilter {
   static TypeFilter instanceOf(final Class cls) {
      return new TypeFilter() {
         @Nullable
         public Object downcast(Object obj) {
            return cls.isInstance(obj) ? obj : null;
         }

         public Class getBaseClass() {
            return cls;
         }
      };
   }

   static TypeFilter equals(final Class cls) {
      return new TypeFilter() {
         @Nullable
         public Object downcast(Object obj) {
            return cls.equals(obj.getClass()) ? obj : null;
         }

         public Class getBaseClass() {
            return cls;
         }
      };
   }

   @Nullable
   Object downcast(Object obj);

   Class getBaseClass();
}
