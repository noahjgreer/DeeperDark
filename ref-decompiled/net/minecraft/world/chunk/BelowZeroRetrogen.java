package net.minecraft.world.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.BitSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.LongStream;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeSupplier;

public final class BelowZeroRetrogen {
   private static final BitSet EMPTY_MISSING_BEDROCK_BIT_SET = new BitSet(0);
   private static final Codec MISSING_BEDROCK_CODEC;
   private static final Codec STATUS_CODEC;
   public static final Codec CODEC;
   private static final Set CAVE_BIOMES;
   public static final HeightLimitView BELOW_ZERO_VIEW;
   private final ChunkStatus targetStatus;
   private final BitSet missingBedrock;

   private BelowZeroRetrogen(ChunkStatus targetStatus, Optional missingBedrock) {
      this.targetStatus = targetStatus;
      this.missingBedrock = (BitSet)missingBedrock.orElse(EMPTY_MISSING_BEDROCK_BIT_SET);
   }

   public static void replaceOldBedrock(ProtoChunk chunk) {
      int i = true;
      BlockPos.iterate(0, 0, 0, 15, 4, 15).forEach((pos) -> {
         if (chunk.getBlockState(pos).isOf(Blocks.BEDROCK)) {
            chunk.setBlockState(pos, Blocks.DEEPSLATE.getDefaultState());
         }

      });
   }

   public void fillColumnsWithAirIfMissingBedrock(ProtoChunk chunk) {
      HeightLimitView heightLimitView = chunk.getHeightLimitView();
      int i = heightLimitView.getBottomY();
      int j = heightLimitView.getTopYInclusive();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            if (this.isColumnMissingBedrock(k, l)) {
               BlockPos.iterate(k, i, l, k, j, l).forEach((pos) -> {
                  chunk.setBlockState(pos, Blocks.AIR.getDefaultState());
               });
            }
         }
      }

   }

   public ChunkStatus getTargetStatus() {
      return this.targetStatus;
   }

   public boolean hasMissingBedrock() {
      return !this.missingBedrock.isEmpty();
   }

   public boolean isColumnMissingBedrock(int x, int z) {
      return this.missingBedrock.get((z & 15) * 16 + (x & 15));
   }

   public static BiomeSupplier getBiomeSupplier(BiomeSupplier biomeSupplier, Chunk chunk) {
      if (!chunk.hasBelowZeroRetrogen()) {
         return biomeSupplier;
      } else {
         Set var10000 = CAVE_BIOMES;
         Objects.requireNonNull(var10000);
         Predicate predicate = var10000::contains;
         return (x, y, z, noise) -> {
            RegistryEntry registryEntry = biomeSupplier.getBiome(x, y, z, noise);
            return registryEntry.matches(predicate) ? registryEntry : chunk.getBiomeForNoiseGen(x, 0, z);
         };
      }
   }

   static {
      MISSING_BEDROCK_CODEC = Codec.LONG_STREAM.xmap((serializedBedrockBitSet) -> {
         return BitSet.valueOf(serializedBedrockBitSet.toArray());
      }, (bedrockBitSet) -> {
         return LongStream.of(bedrockBitSet.toLongArray());
      });
      STATUS_CODEC = Registries.CHUNK_STATUS.getCodec().comapFlatMap((status) -> {
         return status == ChunkStatus.EMPTY ? DataResult.error(() -> {
            return "target_status cannot be empty";
         }) : DataResult.success(status);
      }, Function.identity());
      CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(STATUS_CODEC.fieldOf("target_status").forGetter(BelowZeroRetrogen::getTargetStatus), MISSING_BEDROCK_CODEC.lenientOptionalFieldOf("missing_bedrock").forGetter((belowZeroRetrogen) -> {
            return belowZeroRetrogen.missingBedrock.isEmpty() ? Optional.empty() : Optional.of(belowZeroRetrogen.missingBedrock);
         })).apply(instance, BelowZeroRetrogen::new);
      });
      CAVE_BIOMES = Set.of(BiomeKeys.LUSH_CAVES, BiomeKeys.DRIPSTONE_CAVES, BiomeKeys.DEEP_DARK);
      BELOW_ZERO_VIEW = new HeightLimitView() {
         public int getHeight() {
            return 64;
         }

         public int getBottomY() {
            return -64;
         }
      };
   }
}
