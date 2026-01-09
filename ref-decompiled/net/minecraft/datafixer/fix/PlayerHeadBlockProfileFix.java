package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;

public class PlayerHeadBlockProfileFix extends ChoiceFix {
   public PlayerHeadBlockProfileFix(Schema outputSchema) {
      super(outputSchema, false, "PlayerHeadBlockProfileFix", TypeReferences.BLOCK_ENTITY, "minecraft:skull");
   }

   protected Typed transform(Typed inputTyped) {
      return inputTyped.update(DSL.remainderFinder(), this::fixProfile);
   }

   private Dynamic fixProfile(Dynamic dynamic) {
      Optional optional = dynamic.get("SkullOwner").result();
      Optional optional2 = dynamic.get("ExtraType").result();
      Optional optional3 = optional.or(() -> {
         return optional2;
      });
      if (optional3.isEmpty()) {
         return dynamic;
      } else {
         dynamic = dynamic.remove("SkullOwner").remove("ExtraType");
         dynamic = dynamic.set("profile", ItemStackComponentizationFix.createProfileDynamic((Dynamic)optional3.get()));
         return dynamic;
      }
   }
}
