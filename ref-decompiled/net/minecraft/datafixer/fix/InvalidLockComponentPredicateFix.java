package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public class InvalidLockComponentPredicateFix extends ComponentFix {
   private static final Optional DOUBLE_QUOTES = Optional.of("\"\"");

   public InvalidLockComponentPredicateFix(Schema outputSchema) {
      super(outputSchema, "InvalidLockComponentPredicateFix", "minecraft:lock");
   }

   @Nullable
   protected Dynamic fixComponent(Dynamic dynamic) {
      return validateLock(dynamic);
   }

   @Nullable
   public static Dynamic validateLock(Dynamic dynamic) {
      return isLockInvalid(dynamic) ? null : dynamic;
   }

   private static boolean isLockInvalid(Dynamic dynamic) {
      return hasMatchingKey(dynamic, "components", (componentsDynamic) -> {
         return hasMatchingKey(componentsDynamic, "minecraft:custom_name", (customNameDynamic) -> {
            return customNameDynamic.asString().result().equals(DOUBLE_QUOTES);
         });
      });
   }

   private static boolean hasMatchingKey(Dynamic dynamic, String key, Predicate predicate) {
      Optional optional = dynamic.getMapValues().result();
      return !optional.isEmpty() && ((Map)optional.get()).size() == 1 ? dynamic.get(key).result().filter(predicate).isPresent() : false;
   }
}
