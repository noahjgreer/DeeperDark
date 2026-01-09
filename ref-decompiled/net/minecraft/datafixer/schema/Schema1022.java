package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1022 extends Schema {
   public Schema1022(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public void registerTypes(Schema schema, Map entityTypes, Map blockEntityTypes) {
      super.registerTypes(schema, entityTypes, blockEntityTypes);
      schema.registerType(false, TypeReferences.RECIPE, () -> {
         return DSL.constType(IdentifierNormalizingSchema.getIdentifierType());
      });
      schema.registerType(false, TypeReferences.PLAYER, () -> {
         return DSL.optionalFields(new Pair[]{Pair.of("RootVehicle", DSL.optionalFields("Entity", TypeReferences.ENTITY_TREE.in(schema))), Pair.of("ender_pearls", DSL.list(TypeReferences.ENTITY_TREE.in(schema))), Pair.of("Inventory", DSL.list(TypeReferences.ITEM_STACK.in(schema))), Pair.of("EnderItems", DSL.list(TypeReferences.ITEM_STACK.in(schema))), Pair.of("ShoulderEntityLeft", TypeReferences.ENTITY_TREE.in(schema)), Pair.of("ShoulderEntityRight", TypeReferences.ENTITY_TREE.in(schema)), Pair.of("recipeBook", DSL.optionalFields("recipes", DSL.list(TypeReferences.RECIPE.in(schema)), "toBeDisplayed", DSL.list(TypeReferences.RECIPE.in(schema))))});
      });
      schema.registerType(false, TypeReferences.HOTBAR, () -> {
         return DSL.compoundList(DSL.list(TypeReferences.ITEM_STACK.in(schema)));
      });
   }
}
