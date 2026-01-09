package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum StructureTerrainAdaptation implements StringIdentifiable {
   NONE("none"),
   BURY("bury"),
   BEARD_THIN("beard_thin"),
   BEARD_BOX("beard_box"),
   ENCAPSULATE("encapsulate");

   public static final Codec CODEC = StringIdentifiable.createCodec(StructureTerrainAdaptation::values);
   private final String name;

   private StructureTerrainAdaptation(final String name) {
      this.name = name;
   }

   public String asString() {
      return this.name;
   }

   // $FF: synthetic method
   private static StructureTerrainAdaptation[] method_36756() {
      return new StructureTerrainAdaptation[]{NONE, BURY, BEARD_THIN, BEARD_BOX, ENCAPSULATE};
   }
}
