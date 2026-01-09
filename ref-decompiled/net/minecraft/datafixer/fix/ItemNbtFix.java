package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class ItemNbtFix extends DataFix {
   private final String name;
   private final Predicate itemIdPredicate;

   public ItemNbtFix(Schema outputSchema, String name, Predicate itemIdPredicate) {
      super(outputSchema, false);
      this.name = name;
      this.itemIdPredicate = itemIdPredicate;
   }

   public final TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      return this.fixTypeEverywhereTyped(this.name, type, fixNbt(type, this.itemIdPredicate, this::fix));
   }

   public static UnaryOperator fixNbt(Type itemStackType, Predicate itemIdPredicate, UnaryOperator nbtFixer) {
      OpticFinder opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
      OpticFinder opticFinder2 = itemStackType.findField("tag");
      return (itemStackTyped) -> {
         Optional optional = itemStackTyped.getOptional(opticFinder);
         return optional.isPresent() && itemIdPredicate.test((String)((Pair)optional.get()).getSecond()) ? itemStackTyped.updateTyped(opticFinder2, nbtFixer) : itemStackTyped;
      };
   }

   protected abstract Typed fix(Typed typed);
}
