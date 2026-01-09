package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.datafixer.TypeReferences;

public class EquipmentFormatFix extends DataFix {
   public EquipmentFormatFix(Schema outputSchema) {
      super(outputSchema, true);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
      Type type2 = this.getOutputSchema().getTypeRaw(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = type.findField("id");
      return this.method_66619(type, type2, opticFinder);
   }

   private TypeRewriteRule method_66619(Type type, Type type2, OpticFinder opticFinder) {
      Type type3 = DSL.named(TypeReferences.ENTITY_EQUIPMENT.typeName(), DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(type))), DSL.optional(DSL.field("HandItems", DSL.list(type))), DSL.optional(DSL.field("body_armor_item", type)), DSL.optional(DSL.field("saddle", type))));
      Type type4 = DSL.named(TypeReferences.ENTITY_EQUIPMENT.typeName(), DSL.optional(DSL.field("equipment", DSL.and(DSL.optional(DSL.field("mainhand", type2)), DSL.optional(DSL.field("offhand", type2)), DSL.optional(DSL.field("feet", type2)), DSL.and(DSL.optional(DSL.field("legs", type2)), DSL.optional(DSL.field("chest", type2)), DSL.optional(DSL.field("head", type2)), DSL.and(DSL.optional(DSL.field("body", type2)), DSL.optional(DSL.field("saddle", type2)), DSL.remainderType()))))));
      if (!type3.equals(this.getInputSchema().getType(TypeReferences.ENTITY_EQUIPMENT))) {
         throw new IllegalStateException("Input entity_equipment type does not match expected");
      } else if (!type4.equals(this.getOutputSchema().getType(TypeReferences.ENTITY_EQUIPMENT))) {
         throw new IllegalStateException("Output entity_equipment type does not match expected");
      } else {
         return this.fixTypeEverywhere("EquipmentFormatFix", type3, type4, (dynamicOps) -> {
            Predicate predicate = (object) -> {
               Typed typed = new Typed(type, dynamicOps, object);
               return typed.getOptional(opticFinder).isEmpty();
            };
            return (pair) -> {
               String string = (String)pair.getFirst();
               Pair pair2 = (Pair)pair.getSecond();
               List list = (List)((Either)pair2.getFirst()).map(Function.identity(), (unit) -> {
                  return List.of();
               });
               List list2 = (List)((Either)((Pair)pair2.getSecond()).getFirst()).map(Function.identity(), (unit) -> {
                  return List.of();
               });
               Either either = (Either)((Pair)((Pair)pair2.getSecond()).getSecond()).getFirst();
               Either either2 = (Either)((Pair)((Pair)pair2.getSecond()).getSecond()).getSecond();
               Either either3 = method_66617(0, list, predicate);
               Either either4 = method_66617(1, list, predicate);
               Either either5 = method_66617(2, list, predicate);
               Either either6 = method_66617(3, list, predicate);
               Either either7 = method_66617(0, list2, predicate);
               Either either8 = method_66617(1, list2, predicate);
               return method_66623(either, either2, either3, either4, either5, either6, either7, either8) ? Pair.of(string, Either.right(Unit.INSTANCE)) : Pair.of(string, Either.left(Pair.of(either7, Pair.of(either8, Pair.of(either3, Pair.of(either4, Pair.of(either5, Pair.of(either6, Pair.of(either, Pair.of(either2, new Dynamic(dynamicOps)))))))))));
            };
         });
      }
   }

   @SafeVarargs
   private static boolean method_66623(Either... eithers) {
      Either[] var1 = eithers;
      int var2 = eithers.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Either either = var1[var3];
         if (either.right().isEmpty()) {
            return false;
         }
      }

      return true;
   }

   private static Either method_66617(int i, List list, Predicate predicate) {
      if (i >= list.size()) {
         return Either.right(Unit.INSTANCE);
      } else {
         Object object = list.get(i);
         return predicate.test(object) ? Either.right(Unit.INSTANCE) : Either.left(object);
      }
   }
}
