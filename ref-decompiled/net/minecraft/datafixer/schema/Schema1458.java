package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import net.minecraft.datafixer.TypeReferences;

public class Schema1458 extends IdentifierNormalizingSchema {
   public Schema1458(int i, Schema schema) {
      super(i, schema);
   }

   public void registerTypes(Schema schema, Map map, Map map2) {
      super.registerTypes(schema, map, map2);
      schema.registerType(true, TypeReferences.ENTITY, () -> {
         return DSL.and(TypeReferences.ENTITY_EQUIPMENT.in(schema), DSL.optionalFields("CustomName", TypeReferences.TEXT_COMPONENT.in(schema), DSL.taggedChoiceLazy("id", getIdentifierType(), map)));
      });
   }

   public Map registerBlockEntities(Schema schema) {
      Map map = super.registerBlockEntities(schema);
      schema.register(map, "minecraft:beacon", () -> {
         return customName(schema);
      });
      schema.register(map, "minecraft:banner", () -> {
         return customName(schema);
      });
      schema.register(map, "minecraft:brewing_stand", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:chest", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:trapped_chest", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:dispenser", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:dropper", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:enchanting_table", () -> {
         return customName(schema);
      });
      schema.register(map, "minecraft:furnace", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:hopper", () -> {
         return itemsAndCustomName(schema);
      });
      schema.register(map, "minecraft:shulker_box", () -> {
         return itemsAndCustomName(schema);
      });
      return map;
   }

   public static TypeTemplate itemsAndCustomName(Schema schema) {
      return DSL.optionalFields("Items", DSL.list(TypeReferences.ITEM_STACK.in(schema)), "CustomName", TypeReferences.TEXT_COMPONENT.in(schema));
   }

   public static TypeTemplate customName(Schema schema) {
      return DSL.optionalFields("CustomName", TypeReferences.TEXT_COMPONENT.in(schema));
   }
}
