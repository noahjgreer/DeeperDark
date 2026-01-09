package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.Iterator;
import java.util.List;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class AttributeIdPrefixFix extends AttributeRenameFix {
   private static final List PREFIXES = List.of("generic.", "horse.", "player.", "zombie.");

   public AttributeIdPrefixFix(Schema outputSchema) {
      super(outputSchema, "AttributeIdPrefixFix", AttributeIdPrefixFix::removePrefix);
   }

   private static String removePrefix(String id) {
      String string = IdentifierNormalizingSchema.normalize(id);
      Iterator var2 = PREFIXES.iterator();

      String string3;
      do {
         if (!var2.hasNext()) {
            return id;
         }

         String string2 = (String)var2.next();
         string3 = IdentifierNormalizingSchema.normalize(string2);
      } while(!string.startsWith(string3));

      String var10000 = string.substring(string3.length());
      return "minecraft:" + var10000;
   }
}
