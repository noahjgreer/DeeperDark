package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class BlockRotStructureProcessor extends StructureProcessor {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(RegistryCodecs.entryList(RegistryKeys.BLOCK).optionalFieldOf("rottable_blocks").forGetter((processor) -> {
         return processor.rottableBlocks;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("integrity").forGetter((processor) -> {
         return processor.integrity;
      })).apply(instance, BlockRotStructureProcessor::new);
   });
   private final Optional rottableBlocks;
   private final float integrity;

   public BlockRotStructureProcessor(RegistryEntryList rottableBlocks, float integrity) {
      this(Optional.of(rottableBlocks), integrity);
   }

   public BlockRotStructureProcessor(float integrity) {
      this(Optional.empty(), integrity);
   }

   private BlockRotStructureProcessor(Optional rottableBlocks, float integrity) {
      this.integrity = integrity;
      this.rottableBlocks = rottableBlocks;
   }

   @Nullable
   public StructureTemplate.StructureBlockInfo process(WorldView world, BlockPos pos, BlockPos pivot, StructureTemplate.StructureBlockInfo originalBlockInfo, StructureTemplate.StructureBlockInfo currentBlockInfo, StructurePlacementData data) {
      Random random = data.getRandom(currentBlockInfo.pos());
      return (!this.rottableBlocks.isPresent() || originalBlockInfo.state().isIn((RegistryEntryList)this.rottableBlocks.get())) && !(random.nextFloat() <= this.integrity) ? null : currentBlockInfo;
   }

   protected StructureProcessorType getType() {
      return StructureProcessorType.BLOCK_ROT;
   }
}
