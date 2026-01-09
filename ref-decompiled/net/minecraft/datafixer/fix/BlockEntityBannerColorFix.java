package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityBannerColorFix extends ChoiceFix {
   public BlockEntityBannerColorFix(Schema schema, boolean bl) {
      super(schema, bl, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
   }

   public Dynamic fixBannerColor(Dynamic bannerDynamic) {
      bannerDynamic = bannerDynamic.update("Base", (baseDynamic) -> {
         return baseDynamic.createInt(15 - baseDynamic.asInt(0));
      });
      bannerDynamic = bannerDynamic.update("Patterns", (patternsDynamic) -> {
         DataResult var10000 = patternsDynamic.asStreamOpt().map((stream) -> {
            return stream.map((patternDynamic) -> {
               return patternDynamic.update("Color", (colorDynamic) -> {
                  return colorDynamic.createInt(15 - colorDynamic.asInt(0));
               });
            });
         });
         Objects.requireNonNull(patternsDynamic);
         return (Dynamic)DataFixUtils.orElse(var10000.map(patternsDynamic::createList).result(), patternsDynamic);
      });
      return bannerDynamic;
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), this::fixBannerColor);
   }
}
