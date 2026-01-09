package net.minecraft.client.render.model.json;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.state.StateManager;
import net.minecraft.util.StringIdentifiable;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface MultipartModelCondition {
   Codec CODEC = Codec.recursive("condition", (group) -> {
      Codec codec = Codec.simpleMap(MultipartModelCombinedCondition.LogicalOperator.CODEC, group.listOf(), StringIdentifiable.toKeyable(MultipartModelCombinedCondition.LogicalOperator.values())).codec().comapFlatMap((map) -> {
         if (map.size() != 1) {
            return DataResult.error(() -> {
               return "Invalid map size for combiner condition, expected exactly one element";
            });
         } else {
            Map.Entry entry = (Map.Entry)map.entrySet().iterator().next();
            return DataResult.success(new MultipartModelCombinedCondition((MultipartModelCombinedCondition.LogicalOperator)entry.getKey(), (List)entry.getValue()));
         }
      }, (multipartModelCombinedCondition) -> {
         return Map.of(multipartModelCombinedCondition.operation(), multipartModelCombinedCondition.terms());
      });
      return Codec.either(codec, SimpleMultipartModelSelector.CODEC).flatComapMap((either) -> {
         return (MultipartModelCondition)either.map((multipartModelCombinedCondition) -> {
            return multipartModelCombinedCondition;
         }, (simpleMultipartModelSelector) -> {
            return simpleMultipartModelSelector;
         });
      }, (multipartModelCondition) -> {
         Objects.requireNonNull(multipartModelCondition);
         int i = 0;
         DataResult var10000;
         switch (multipartModelCondition.typeSwitch<invokedynamic>(multipartModelCondition, i)) {
            case 0:
               MultipartModelCombinedCondition multipartModelCombinedCondition = (MultipartModelCombinedCondition)multipartModelCondition;
               var10000 = DataResult.success(Either.left(multipartModelCombinedCondition));
               break;
            case 1:
               SimpleMultipartModelSelector simpleMultipartModelSelector = (SimpleMultipartModelSelector)multipartModelCondition;
               var10000 = DataResult.success(Either.right(simpleMultipartModelSelector));
               break;
            default:
               var10000 = DataResult.error(() -> {
                  return "Unrecognized condition";
               });
         }

         DataResult dataResult = var10000;
         return dataResult;
      });
   });

   Predicate instantiate(StateManager value);
}
