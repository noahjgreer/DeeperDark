package net.minecraft.structure.processor;

import com.mojang.serialization.MapCodec;

public class NopStructureProcessor extends StructureProcessor {
   public static final MapCodec CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final NopStructureProcessor INSTANCE = new NopStructureProcessor();

   private NopStructureProcessor() {
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.NOP;
   }
}
