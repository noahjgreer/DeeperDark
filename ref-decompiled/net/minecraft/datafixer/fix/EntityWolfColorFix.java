package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class EntityWolfColorFix extends ChoiceFix {
   public EntityWolfColorFix(Schema schema, boolean bl) {
      super(schema, bl, "EntityWolfColorFix", TypeReferences.ENTITY, "minecraft:wolf");
   }

   public Dynamic fixCollarColor(Dynamic wolfDynamic) {
      return wolfDynamic.update("CollarColor", (colorDynamic) -> {
         return colorDynamic.createByte((byte)(15 - colorDynamic.asInt(0)));
      });
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), this::fixCollarColor);
   }
}
