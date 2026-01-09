package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class EntityElderGuardianSplitFix extends EntitySimpleTransformFix {
   public EntityElderGuardianSplitFix(Schema schema, boolean bl) {
      super("EntityElderGuardianSplitFix", schema, bl);
   }

   protected Pair transform(String choice, Dynamic entityDynamic) {
      return Pair.of(Objects.equals(choice, "Guardian") && entityDynamic.get("Elder").asBoolean(false) ? "ElderGuardian" : choice, entityDynamic);
   }
}
