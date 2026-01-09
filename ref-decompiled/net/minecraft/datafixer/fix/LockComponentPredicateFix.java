package net.minecraft.datafixer.fix;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

public class LockComponentPredicateFix extends ComponentFix {
   public static final Escaper ESCAPER = Escapers.builder().addEscape('"', "\\\"").addEscape('\\', "\\\\").build();

   public LockComponentPredicateFix(Schema outputSchema) {
      super(outputSchema, "LockComponentPredicateFix", "minecraft:lock");
   }

   @Nullable
   protected Dynamic fixComponent(Dynamic dynamic) {
      return fixLock(dynamic);
   }

   @Nullable
   public static Dynamic fixLock(Dynamic dynamic) {
      Optional optional = dynamic.asString().result();
      if (optional.isEmpty()) {
         return null;
      } else if (((String)optional.get()).isEmpty()) {
         return null;
      } else {
         Escaper var10001 = ESCAPER;
         Dynamic dynamic2 = dynamic.createString("\"" + var10001.escape((String)optional.get()) + "\"");
         Dynamic dynamic3 = dynamic.emptyMap().set("minecraft:custom_name", dynamic2);
         return dynamic.emptyMap().set("components", dynamic3);
      }
   }
}
