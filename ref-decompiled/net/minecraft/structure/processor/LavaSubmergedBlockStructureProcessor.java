package net.minecraft.structure.processor;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class LavaSubmergedBlockStructureProcessor extends StructureProcessor {
   public static final MapCodec CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });
   public static final LavaSubmergedBlockStructureProcessor INSTANCE = new LavaSubmergedBlockStructureProcessor();

   @Nullable
   public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
      BlockPos blockPos = currentBlockInfo.pos();
      boolean bl = world.getBlockState(blockPos).isOf(Blocks.LAVA);
      return bl && !Block.isShapeFullCube(currentBlockInfo.state().getOutlineShape(world, blockPos)) ? new StructureTemplate.StructureBlockInfo(blockPos, Blocks.LAVA.getDefaultState(), currentBlockInfo.nbt()) : currentBlockInfo;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.LAVA_SUBMERGED_BLOCK;
   }
}
