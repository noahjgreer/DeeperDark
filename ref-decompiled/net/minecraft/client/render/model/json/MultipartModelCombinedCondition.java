package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.StateManager;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public record MultipartModelCombinedCondition(LogicalOperator operation, List terms) implements MultipartModelCondition {
   public MultipartModelCombinedCondition(LogicalOperator logicalOperator, List list) {
      this.operation = logicalOperator;
      this.terms = list;
   }

   public Predicate instantiate(StateManager stateManager) {
      return this.operation.apply(Lists.transform(this.terms, (condition) -> {
         return condition.instantiate(stateManager);
      }));
   }

   public LogicalOperator operation() {
      return this.operation;
   }

   public List terms() {
      return this.terms;
   }

   @Environment(EnvType.CLIENT)
   public static enum LogicalOperator implements StringIdentifiable {
      AND("AND") {
         public Predicate apply(List conditions) {
            return Util.allOf(conditions);
         }
      },
      OR("OR") {
         public Predicate apply(List conditions) {
            return Util.anyOf(conditions);
         }
      };

      public static final Codec CODEC = StringIdentifiable.createCodec(LogicalOperator::values);
      private final String name;

      LogicalOperator(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      public abstract Predicate apply(List conditions);

      // $FF: synthetic method
      private static LogicalOperator[] method_36940() {
         return new LogicalOperator[]{AND, OR};
      }
   }
}
