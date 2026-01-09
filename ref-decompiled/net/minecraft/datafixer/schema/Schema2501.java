package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema2501 extends IdentifierNormalizingSchema {
   public Schema2501(int i, Schema schema) {
      super(i, schema);
   }

   private static void registerFurnace(Schema schema, Map map, String name) {
      schema.register(map, name, () -> {
         return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "CustomName", TypeReferences.TEXT_COMPONENT.in(schema), "RecipesUsed", DSL.compoundList(TypeReferences.RECIPE.in(schema), DSL.constType(DSL.intType())));
      });
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      registerFurnace(schema, map, "minecraft:furnace");
      registerFurnace(schema, map, "minecraft:smoker");
      registerFurnace(schema, map, "minecraft:blast_furnace");
      return map;
   }
}
