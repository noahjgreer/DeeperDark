package net.minecraft.structure;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum StructureLiquidSettings implements StringIdentifiable {
   IGNORE_WATERLOGGING("ignore_waterlogging"),
   APPLY_WATERLOGGING("apply_waterlogging");

   public static Codec codec = StringIdentifiable.createBasicCodec(StructureLiquidSettings::values);
   private final String id;

   private StructureLiquidSettings(final String id) {
      this.id = id;
   }

   public String asString() {
      return this.id;
   }

   // $FF: synthetic method
   private static StructureLiquidSettings[] method_61019() {
      return new StructureLiquidSettings[]{IGNORE_WATERLOGGING, APPLY_WATERLOGGING};
   }
}
