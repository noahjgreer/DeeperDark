package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;
import net.minecraft.util.Util;

public class ItemStackCustomNameToItemNameFix extends DataFix {
   private static final Set EXPLORER_MAP_NAMES = Set.of("filled_map.buried_treasure", "filled_map.explorer_jungle", "filled_map.explorer_swamp", "filled_map.mansion", "filled_map.monument", "filled_map.trial_chambers", "filled_map.village_desert", "filled_map.village_plains", "filled_map.village_savanna", "filled_map.village_snowy", "filled_map.village_taiga");

   public ItemStackCustomNameToItemNameFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   public final TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
      OpticFinder opticFinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.getIdentifierType()));
      OpticFinder opticFinder2 = type.findField("components");
      return this.fixTypeEverywhereTyped("ItemStack custom_name to item_name component fix", type, (itemStackTyped) -> {
         Optional optional = itemStackTyped.getOptional(opticFinder);
         Optional optional2 = optional.map(Pair::getSecond);
         if (optional2.filter((itemId) -> {
            return itemId.equals("minecraft:white_banner");
         }).isPresent()) {
            return itemStackTyped.updateTyped(opticFinder2, ItemStackCustomNameToItemNameFix::fixOminousBanner);
         } else {
            return optional2.filter((itemId) -> {
               return itemId.equals("minecraft:filled_map");
            }).isPresent() ? itemStackTyped.updateTyped(opticFinder2, ItemStackCustomNameToItemNameFix::fixExplorerMaps) : itemStackTyped;
         }
      });
   }

   private static Typed fixExplorerMaps(Typed typed) {
      Set var10001 = EXPLORER_MAP_NAMES;
      Objects.requireNonNull(var10001);
      return fix(typed, var10001::contains);
   }

   private static Typed fixOminousBanner(Typed typed) {
      return fix(typed, (name) -> {
         return name.equals("block.minecraft.ominous_banner");
      });
   }

   private static Typed fix(Typed typed, Predicate namePredicate) {
      return Util.apply(typed, typed.getType(), (dynamic) -> {
         OptionalDynamic optionalDynamic = dynamic.get("minecraft:custom_name");
         Optional optional = optionalDynamic.asString().result().flatMap(TextFixes::getTranslate).filter(namePredicate);
         return optional.isPresent() ? dynamic.renameField("minecraft:custom_name", "minecraft:item_name") : dynamic;
      });
   }
}
