package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class RuleStructureProcessor extends StructureProcessor {
   public static final MapCodec CODEC;
   private final ImmutableList rules;

   public RuleStructureProcessor(List rules) {
      this.rules = ImmutableList.copyOf(rules);
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
      Random random = Random.create(MathHelper.hashCode(currentBlockInfo.pos()));
      BlockState blockState = world.getBlockState(currentBlockInfo.pos());
      UnmodifiableIterator var9 = this.rules.iterator();

      StructureProcessorRule structureProcessorRule;
      do {
         if (!var9.hasNext()) {
            return currentBlockInfo;
         }

         structureProcessorRule = (StructureProcessorRule)var9.next();
      } while(!structureProcessorRule.test(currentBlockInfo.state(), blockState, originalBlockInfo.pos(), currentBlockInfo.pos(), pivot, random));

      return new StructureTemplate.StructureBlockInfo(currentBlockInfo.pos(), structureProcessorRule.getOutputState(), structureProcessorRule.getOutputNbt(random, currentBlockInfo.nbt()));
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.RULE;
   }

   static {
      CODEC = StructureProcessorRule.CODEC.listOf().fieldOf("rules").xmap(RuleStructureProcessor::new, (processor) -> {
         return processor.rules;
      });
   }
}
