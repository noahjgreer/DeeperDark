package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.TypeReferences;

public class RenameEntityAttributesFix extends DataFix {
   private final String description;
   private final UnaryOperator renames;

   public RenameEntityAttributesFix(Schema outputSchema, String description, UnaryOperator renames) {
      super(outputSchema, false);
      this.description = description;
      this.renames = renames;
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = type.findField("tag");
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped(this.description + " (ItemStack)", type, (itemStackTyped) -> {
         return itemStackTyped.updateTyped(opticFinder, this::updateAttributeModifiers);
      }), new TypeRewriteRule[]{this.fixTypeEverywhereTyped(this.description + " (Entity)", this.getInputSchema().getType(TypeReferences.ENTITY), this::updateEntityAttributes), this.fixTypeEverywhereTyped(this.description + " (Player)", this.getInputSchema().getType(TypeReferences.PLAYER), this::updateEntityAttributes)});
   }

   private Dynamic updateAttributeName(Dynamic attributeNameDynamic) {
      Optional var10000 = attributeNameDynamic.asString().result().map(this.renames);
      Objects.requireNonNull(attributeNameDynamic);
      return (Dynamic)DataFixUtils.orElse(var10000.map(attributeNameDynamic::createString), attributeNameDynamic);
   }

   private Typed updateAttributeModifiers(Typed tagTyped) {
      return tagTyped.update(DSL.remainderFinder(), (tagDynamic) -> {
         return tagDynamic.update("AttributeModifiers", (attributeModifiersDynamic) -> {
            Optional var10000 = attributeModifiersDynamic.asStreamOpt().result().map((attributeModifiers) -> {
               return attributeModifiers.map((attributeModifierDynamic) -> {
                  return attributeModifierDynamic.update("AttributeName", this::updateAttributeName);
               });
            });
            Objects.requireNonNull(attributeModifiersDynamic);
            return (Dynamic)DataFixUtils.orElse(var10000.map(attributeModifiersDynamic::createList), attributeModifiersDynamic);
         });
      });
   }

   private Typed updateEntityAttributes(Typed entityTyped) {
      return entityTyped.update(DSL.remainderFinder(), (entityDynamic) -> {
         return entityDynamic.update("Attributes", (attributesDynamic) -> {
            Optional var10000 = attributesDynamic.asStreamOpt().result().map((attributes) -> {
               return attributes.map((attributeDynamic) -> {
                  return attributeDynamic.update("Name", this::updateAttributeName);
               });
            });
            Objects.requireNonNull(attributesDynamic);
            return (Dynamic)DataFixUtils.orElse(var10000.map(attributesDynamic::createList), attributesDynamic);
         });
      });
   }
}
