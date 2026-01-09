package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class PointOfInterestRenameFix extends PointOfInterestFix {
   private final Function renamer;

   public PointOfInterestRenameFix(Schema outputSchema, String name, Function renamer) {
      super(outputSchema, name);
      this.renamer = renamer;
   }

   protected Stream update(Stream dynamics) {
      return dynamics.map((dynamic) -> {
         return dynamic.update("type", (dynamicx) -> {
            DataResult var10000 = dynamicx.asString().map(this.renamer);
            Objects.requireNonNull(dynamicx);
            return (Dynamic)DataFixUtils.orElse(var10000.map(dynamicx::createString).result(), dynamicx);
         });
      });
   }
}
