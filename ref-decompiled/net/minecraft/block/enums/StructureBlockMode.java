package net.minecraft.block.enums;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public enum StructureBlockMode implements StringIdentifiable {
   SAVE("save"),
   LOAD("load"),
   CORNER("corner"),
   DATA("data");

   /** @deprecated */
   @Deprecated
   public static final Codec CODEC = Codecs.enumByName(StructureBlockMode::valueOf);
   private final String name;
   private final Text text;

   private StructureBlockMode(final String name) {
      this.name = name;
      this.text = Text.translatable("structure_block.mode_info." + name);
   }

   public String asString() {
      return this.name;
   }

   public Text asText() {
      return this.text;
   }

   // $FF: synthetic method
   private static StructureBlockMode[] method_36737() {
      return new StructureBlockMode[]{SAVE, LOAD, CORNER, DATA};
   }
}
