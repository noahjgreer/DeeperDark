package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class EntityShulkerRotationFix extends ChoiceFix {
   public EntityShulkerRotationFix(Schema outputSchema) {
      super(outputSchema, false, "EntityShulkerRotationFix", TypeReferences.ENTITY, "minecraft:shulker");
   }

   public Dynamic fixRotation(Dynamic shulkerDynamic) {
      List list = shulkerDynamic.get("Rotation").asList((rotationDynamic) -> {
         return rotationDynamic.asDouble(180.0);
      });
      if (!list.isEmpty()) {
         list.set(0, (Double)list.get(0) - 180.0);
         Stream var10003 = list.stream();
         Objects.requireNonNull(shulkerDynamic);
         return shulkerDynamic.set("Rotation", shulkerDynamic.createList(var10003.map(shulkerDynamic::createDouble)));
      } else {
         return shulkerDynamic;
      }
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), this::fixRotation);
   }
}
