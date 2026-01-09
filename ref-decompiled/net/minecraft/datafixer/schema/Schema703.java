package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema703 extends Schema {
   public Schema703(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public Map registerEntities(Schema schema) {
      Map map = super.registerEntities(schema);
      map.remove("EntityHorse");
      schema.register(map, "Horse", () -> {
         return DSL.optionalFields("ArmorItem", TypeReferences.ITEM_STACK.in(schema), "SaddleItem", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.register(map, "Donkey", () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.register(map, "Mule", () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "SaddleItem", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.register(map, "ZombieHorse", () -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema));
      });
      schema.register(map, "SkeletonHorse", () -> {
         return DSL.optionalFields("SaddleItem", TypeReferences.ITEM_STACK.in(schema));
      });
      return map;
   }
}
