package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.util.Identifier;

public class IdentifierNormalizingSchema extends Schema {
   public static final PrimitiveCodec CODEC = new PrimitiveCodec() {
      public DataResult read(DynamicOps ops, Object input) {
         return ops.getStringValue(input).map(IdentifierNormalizingSchema::normalize);
      }

      public Object write(DynamicOps dynamicOps, String string) {
         return dynamicOps.createString(string);
      }

      public String toString() {
         return "NamespacedString";
      }

      // $FF: synthetic method
      public Object write(final DynamicOps ops, final Object value) {
         return this.write(ops, (String)value);
      }
   };
   private static final Type IDENTIFIER_TYPE;

   public IdentifierNormalizingSchema(int versionKey, Schema parent) {
      super(versionKey, parent);
   }

   public static String normalize(String id) {
      Identifier identifier = Identifier.tryParse(id);
      return identifier != null ? identifier.toString() : id;
   }

   public static Type getIdentifierType() {
      return IDENTIFIER_TYPE;
   }

   public Type getChoiceType(DSL.TypeReference type, String choiceName) {
      return super.getChoiceType(type, normalize(choiceName));
   }

   static {
      IDENTIFIER_TYPE = new Const.PrimitiveType(CODEC);
   }
}
