package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class RenameChunkStatusFix extends DataFix {
   private final String name;
   private final UnaryOperator mapper;

   public RenameChunkStatusFix(Schema outputSchema, String name, UnaryOperator mapper) {
      super(outputSchema, false);
      this.name = name;
      this.mapper = mapper;
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(TypeReferences.CHUNK), (typed) -> {
         return typed.update(DSL.remainderFinder(), (chunk) -> {
            return chunk.update("Status", this::updateStatus).update("below_zero_retrogen", (dynamic) -> {
               return dynamic.update("target_status", this::updateStatus);
            });
         });
      });
   }

   private Dynamic updateStatus(Dynamic status) {
      Optional var10000 = status.asString().result().map(IdentifierNormalizingSchema::normalize).map(this.mapper);
      Objects.requireNonNull(status);
      Optional optional = var10000.map(status::createString);
      return (Dynamic)DataFixUtils.orElse(optional, status);
   }
}
