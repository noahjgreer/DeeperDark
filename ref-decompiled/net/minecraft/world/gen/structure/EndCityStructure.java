package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.structure.EndCityGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

public class EndCityStructure extends Structure {
   public static final MapCodec CODEC = createCodec(EndCityStructure::new);

   public EndCityStructure(Structure.Config config) {
      super(config);
   }

   public Optional getStructurePosition(Structure.Context context) {
      BlockRotation blockRotation = BlockRotation.random(context.random());
      BlockPos blockPos = this.getShiftedPos(context, blockRotation);
      return blockPos.getY() < 60 ? Optional.empty() : Optional.of(new Structure.StructurePosition(blockPos, (collector) -> {
         this.addPieces(collector, blockPos, blockRotation, context);
      }));
   }

   private void addPieces(StructurePiecesCollector collector, BlockPos pos, BlockRotation rotation, Structure.Context context) {
      List list = Lists.newArrayList();
      EndCityGenerator.addPieces(context.structureTemplateManager(), pos, rotation, list, context.random());
      Objects.requireNonNull(collector);
      list.forEach(collector::addPiece);
   }

   public StructureType getType() {
      return StructureType.END_CITY;
   }
}
