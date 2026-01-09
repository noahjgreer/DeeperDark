package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.OptionalDynamic;
import java.util.Objects;
import net.minecraft.datafixer.TypeReferences;

public class EntityRedundantChanceTagsFix extends DataFix {
   private static final Codec FLOAT_LIST_CODEC;

   public EntityRedundantChanceTagsFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(TypeReferences.ENTITY), (typed) -> {
         return typed.update(DSL.remainderFinder(), (entityTyped) -> {
            if (hasZeroDropChance(entityTyped.get("HandDropChances"), 2)) {
               entityTyped = entityTyped.remove("HandDropChances");
            }

            if (hasZeroDropChance(entityTyped.get("ArmorDropChances"), 4)) {
               entityTyped = entityTyped.remove("ArmorDropChances");
            }

            return entityTyped;
         });
      });
   }

   private static boolean hasZeroDropChance(OptionalDynamic listTag, int expectedLength) {
      Codec var10001 = FLOAT_LIST_CODEC;
      Objects.requireNonNull(var10001);
      return (Boolean)listTag.flatMap(var10001::parse).map((chances) -> {
         return chances.size() == expectedLength && chances.stream().allMatch((chance) -> {
            return chance == 0.0F;
         });
      }).result().orElse(false);
   }

   static {
      FLOAT_LIST_CODEC = Codec.FLOAT.listOf();
   }
}
