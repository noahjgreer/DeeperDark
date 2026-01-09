package net.minecraft.util.dynamic;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.ListBuilder;
import java.util.function.UnaryOperator;

abstract class AbstractListBuilder implements ListBuilder {
   private final DynamicOps ops;
   protected DataResult builder = DataResult.success(this.initBuilder(), Lifecycle.stable());

   protected AbstractListBuilder(DynamicOps ops) {
      this.ops = ops;
   }

   public DynamicOps ops() {
      return this.ops;
   }

   protected abstract Object initBuilder();

   protected abstract Object add(Object builder, Object value);

   protected abstract DataResult build(Object builder, Object prefix);

   public ListBuilder add(Object value) {
      this.builder = this.builder.map((object2) -> {
         return this.add(object2, value);
      });
      return this;
   }

   public ListBuilder add(DataResult value) {
      this.builder = this.builder.apply2stable(this::add, value);
      return this;
   }

   public ListBuilder withErrorsFrom(DataResult result) {
      this.builder = this.builder.flatMap((object) -> {
         return result.map((object2) -> {
            return object;
         });
      });
      return this;
   }

   public ListBuilder mapError(UnaryOperator onError) {
      this.builder = this.builder.mapError(onError);
      return this;
   }

   public DataResult build(Object prefix) {
      DataResult dataResult = this.builder.flatMap((object2) -> {
         return this.build(object2, prefix);
      });
      this.builder = DataResult.success(this.initBuilder(), Lifecycle.stable());
      return dataResult;
   }
}
