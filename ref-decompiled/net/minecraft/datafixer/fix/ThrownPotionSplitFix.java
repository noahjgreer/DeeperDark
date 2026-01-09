package net.minecraft.datafixer.fix;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.function.Supplier;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ThrownPotionSplitFix extends EntityTransformFix {
   private final Supplier field_56251 = Suppliers.memoize(() -> {
      Type type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "minecraft:potion");
      Type type2 = FixUtil.withTypeChanged(type, this.getInputSchema().getType(TypeReferences.ENTITY), this.getOutputSchema().getType(TypeReferences.ENTITY));
      OpticFinder opticFinder = type2.findField("Item");
      OpticFinder opticFinder2 = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
      return new class_10681(opticFinder, opticFinder2);
   });

   public ThrownPotionSplitFix(Schema schema) {
      super("ThrownPotionSplitFix", schema, true);
   }

   protected Pair transform(String choice, Typed entityTyped) {
      if (!choice.equals("minecraft:potion")) {
         return Pair.of(choice, entityTyped);
      } else {
         String string = ((class_10681)this.field_56251.get()).method_67102(entityTyped);
         return "minecraft:lingering_potion".equals(string) ? Pair.of("minecraft:lingering_potion", entityTyped) : Pair.of("minecraft:splash_potion", entityTyped);
      }
   }

   private static record class_10681(OpticFinder itemFinder, OpticFinder itemIdFinder) {
      class_10681(OpticFinder opticFinder, OpticFinder opticFinder2) {
         this.itemFinder = opticFinder;
         this.itemIdFinder = opticFinder2;
      }

      public String method_67102(Typed typed) {
         return (String)typed.getOptionalTyped(this.itemFinder).flatMap((typedx) -> {
            return typedx.getOptional(this.itemIdFinder);
         }).map(Pair::getSecond).map(IdentifierNormalizingSchema::normalize).orElse("");
      }

      public OpticFinder itemFinder() {
         return this.itemFinder;
      }

      public OpticFinder itemIdFinder() {
         return this.itemIdFinder;
      }
   }
}
