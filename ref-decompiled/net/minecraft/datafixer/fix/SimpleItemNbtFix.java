package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Predicate;

public abstract class SimpleItemNbtFix extends ItemNbtFix {
   public SimpleItemNbtFix(Schema schema, String string, Predicate predicate) {
      super(schema, string, predicate);
   }

   protected abstract Dynamic fixNbt(Dynamic dynamic);

   protected final Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixNbt);
   }
}
