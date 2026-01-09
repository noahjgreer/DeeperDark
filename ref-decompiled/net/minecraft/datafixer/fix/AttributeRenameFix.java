package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.FixUtil;
import net.minecraft.datafixer.TypeReferences;

public class AttributeRenameFix extends DataFix {
   private final String name;
   private final UnaryOperator renamer;

   public AttributeRenameFix(Schema outputSchema, String name, UnaryOperator renamer) {
      super(outputSchema, false);
      this.name = name;
      this.renamer = renamer;
   }

   protected TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped(this.name + " (Components)", this.getInputSchema().getType(TypeReferences.DATA_COMPONENTS), this::applyToComponents), new TypeRewriteRule[]{this.fixTypeEverywhereTyped(this.name + " (Entity)", this.getInputSchema().getType(TypeReferences.ENTITY), this::applyToEntity), this.fixTypeEverywhereTyped(this.name + " (Player)", this.getInputSchema().getType(TypeReferences.PLAYER), this::applyToEntity)});
   }

   private Typed applyToComponents(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         return dynamic.update("minecraft:attribute_modifiers", (dynamicx) -> {
            return dynamicx.update("modifiers", (dynamic) -> {
               Optional var10000 = dynamic.asStreamOpt().result().map((stream) -> {
                  return stream.map(this::applyToTypeField);
               });
               Objects.requireNonNull(dynamic);
               return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createList), dynamic);
            });
         });
      });
   }

   private Typed applyToEntity(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         return dynamic.update("attributes", (dynamicx) -> {
            Optional var10000 = dynamicx.asStreamOpt().result().map((stream) -> {
               return stream.map(this::applyToIdField);
            });
            Objects.requireNonNull(dynamicx);
            return (Dynamic)DataFixUtils.orElse(var10000.map(dynamicx::createList), dynamicx);
         });
      });
   }

   private Dynamic applyToIdField(Dynamic dynamic) {
      return FixUtil.apply(dynamic, "id", this.renamer);
   }

   private Dynamic applyToTypeField(Dynamic dynamic) {
      return FixUtil.apply(dynamic, "type", this.renamer);
   }
}
