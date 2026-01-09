package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class RenameEnchantmentFix extends DataFix {
   final String name;
   final Map oldToNewIds;

   public RenameEnchantmentFix(Schema outputSchema, String name, Map oldToNewIds) {
      super(outputSchema, false);
      this.name = name;
      this.oldToNewIds = oldToNewIds;
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = type.findField("tag");
      return this.fixTypeEverywhereTyped(this.name, type, (itemStackTyped) -> {
         return itemStackTyped.updateTyped(opticFinder, (itemTagTyped) -> {
            return itemTagTyped.update(DSL.remainderFinder(), this::fixIds);
         });
      });
   }

   private Dynamic fixIds(Dynamic itemTagDynamic) {
      itemTagDynamic = this.fixIds(itemTagDynamic, "Enchantments");
      itemTagDynamic = this.fixIds(itemTagDynamic, "StoredEnchantments");
      return itemTagDynamic;
   }

   private Dynamic fixIds(Dynamic itemTagDynamic, String enchantmentsKey) {
      return itemTagDynamic.update(enchantmentsKey, (enchantmentsDynamic) -> {
         DataResult var10000 = enchantmentsDynamic.asStreamOpt().map((enchantments) -> {
            return enchantments.map((enchantmentDynamic) -> {
               return enchantmentDynamic.update("id", (idDynamic) -> {
                  return (Dynamic)idDynamic.asString().map((oldId) -> {
                     return enchantmentDynamic.createString((String)this.oldToNewIds.getOrDefault(IdentifierNormalizingSchema.normalize(oldId), oldId));
                  }).mapOrElse(Function.identity(), (error) -> {
                     return idDynamic;
                  });
               });
            });
         });
         Objects.requireNonNull(enchantmentsDynamic);
         return (Dynamic)var10000.map(enchantmentsDynamic::createList).mapOrElse(Function.identity(), (error) -> {
            return enchantmentsDynamic;
         });
      });
   }
}
