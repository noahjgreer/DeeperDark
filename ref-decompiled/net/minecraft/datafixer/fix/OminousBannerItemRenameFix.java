package net.minecraft.datafixer.fix;

import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.util.Util;

public class OminousBannerItemRenameFix extends ItemNbtFix {
   public OminousBannerItemRenameFix(Schema outputSchema) {
      super(outputSchema, "OminousBannerRenameFix", (itemId) -> {
         return itemId.equals("minecraft:white_banner");
      });
   }

   private Dynamic fixBannerNbt(Dynamic nbt) {
      return nbt.update("display", (display) -> {
         return display.update("Name", (name) -> {
            Optional optional = name.asString().result();
            return optional.isPresent() ? name.createString(((String)optional.get()).replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"")) : name;
         });
      });
   }

   protected Typed fix(Typed typed) {
      return Util.apply(typed, typed.getType(), this::fixBannerNbt);
   }
}
