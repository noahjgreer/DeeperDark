package net.minecraft.datafixer.fix;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;

public class BlockEntityUuidFix extends AbstractUuidFix {
   public BlockEntityUuidFix(Schema outputSchema) {
      super(outputSchema, TypeReferences.BLOCK_ENTITY);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), (typed) -> {
         typed = this.updateTyped(typed, "minecraft:conduit", this::updateConduit);
         typed = this.updateTyped(typed, "minecraft:skull", this::updateSkull);
         return typed;
      });
   }

   private Dynamic updateSkull(Dynamic skullDynamic) {
      return (Dynamic)skullDynamic.get("Owner").get().map((ownerDynamic) -> {
         return (Dynamic)updateStringUuid(ownerDynamic, "Id", "Id").orElse(ownerDynamic);
      }).map((ownerDynamic) -> {
         return skullDynamic.remove("Owner").set("SkullOwner", ownerDynamic);
      }).result().orElse(skullDynamic);
   }

   private Dynamic updateConduit(Dynamic conduitDynamic) {
      return (Dynamic)updateCompoundUuid(conduitDynamic, "target_uuid", "Target").orElse(conduitDynamic);
   }
}
