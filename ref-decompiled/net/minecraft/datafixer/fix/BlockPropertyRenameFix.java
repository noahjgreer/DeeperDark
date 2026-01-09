package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class BlockPropertyRenameFix extends DataFix {
   private final String name;

   public BlockPropertyRenameFix(Schema outputSchema, String name) {
      super(outputSchema, false);
      this.name = name;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(TypeReferences.BLOCK_STATE), (typed) -> {
         return typed.update(DSL.remainderFinder(), this::fix);
      });
   }

   private Dynamic fix(Dynamic blockState) {
      Optional optional = blockState.get("Name").asString().result().map(IdentifierNormalizingSchema::normalize);
      return optional.isPresent() && this.shouldFix((String)optional.get()) ? blockState.update("Properties", (properties) -> {
         return this.fix((String)optional.get(), properties);
      }) : blockState;
   }

   protected abstract boolean shouldFix(String id);

   protected abstract Dynamic fix(String id, Dynamic properties);
}
