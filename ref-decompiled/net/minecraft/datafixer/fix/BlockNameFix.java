package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public abstract class BlockNameFix extends DataFix {
   private final String name;

   public BlockNameFix(Schema outputSchema, String name) {
      super(outputSchema, false);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.BLOCK_NAME);
      Type type2 = DSL.named(TypeReferences.BLOCK_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType());
      if (!Objects.equals(type, type2)) {
         throw new IllegalStateException("block type is not what was expected.");
      } else {
         TypeRewriteRule typeRewriteRule = this.fixTypeEverywhere(this.name + " for block", type2, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(this::rename);
            };
         });
         TypeRewriteRule typeRewriteRule2 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(TypeReferences.BLOCK_STATE), (typed) -> {
            return typed.update(DSL.remainderFinder(), this::fixBlockState);
         });
         TypeRewriteRule typeRewriteRule3 = this.fixTypeEverywhereTyped(this.name + " for flat_block_state", this.getInputSchema().getType(TypeReferences.FLAT_BLOCK_STATE), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
               Optional var10000 = dynamic.asString().result().map(this::fixFlatBlockState);
               Objects.requireNonNull(dynamic);
               return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createString), dynamic);
            });
         });
         return TypeRewriteRule.seq(typeRewriteRule, new TypeRewriteRule[]{typeRewriteRule2, typeRewriteRule3});
      }
   }

   private Dynamic fixBlockState(Dynamic blockStateDynamic) {
      Optional optional = blockStateDynamic.get("Name").asString().result();
      return optional.isPresent() ? blockStateDynamic.set("Name", blockStateDynamic.createString(this.rename((String)optional.get()))) : blockStateDynamic;
   }

   private String fixFlatBlockState(String flatBlockState) {
      int i = flatBlockState.indexOf(91);
      int j = flatBlockState.indexOf(123);
      int k = flatBlockState.length();
      if (i > 0) {
         k = i;
      }

      if (j > 0) {
         k = Math.min(k, j);
      }

      String string = flatBlockState.substring(0, k);
      String string2 = this.rename(string);
      return string2 + flatBlockState.substring(k);
   }

   protected abstract String rename(String oldName);

   public static DataFix create(Schema outputSchema, String name, final Function rename) {
      return new BlockNameFix(outputSchema, name) {
         protected String rename(String oldName) {
            return (String)rename.apply(oldName);
         }
      };
   }
}
