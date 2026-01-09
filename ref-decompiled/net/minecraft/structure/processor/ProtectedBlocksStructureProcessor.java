package net.minecraft.structure.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

public class ProtectedBlocksStructureProcessor extends StructureProcessor {
   public final TagKey protectedBlocksTag;
   public static final MapCodec CODEC;

   public ProtectedBlocksStructureProcessor(TagKey protectedBlocksTag) {
      this.protectedBlocksTag = protectedBlocksTag;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
      return Feature.notInBlockTagPredicate(this.protectedBlocksTag).test(world.getBlockState(currentBlockInfo.pos())) ? currentBlockInfo : null;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.PROTECTED_BLOCKS;
   }

   static {
      CODEC = TagKey.codec(RegistryKeys.BLOCK).xmap(ProtectedBlocksStructureProcessor::new, (processor) -> {
         return processor.protectedBlocksTag;
      }).fieldOf("value");
   }
}
