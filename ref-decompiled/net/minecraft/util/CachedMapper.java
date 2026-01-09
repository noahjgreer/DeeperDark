package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;

public class CachedMapper {
   private final Function mapper;
   @Nullable
   private Object cachedInput = null;
   @Nullable
   private Object cachedOutput;

   public CachedMapper(Function mapper) {
      this.mapper = mapper;
   }

   public Object map(Object input) {
      if (this.cachedOutput == null || !Objects.equals(this.cachedInput, input)) {
         this.cachedOutput = this.mapper.apply(input);
         this.cachedInput = input;
      }

      return this.cachedOutput;
   }
}
