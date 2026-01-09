package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class FixedPlacementModifier extends PlacementModifier {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(BlockPos.CODEC.listOf().fieldOf("positions").forGetter((placementModifier) -> {
         return placementModifier.positions;
      })).apply(instance, FixedPlacementModifier::new);
   });
   private final List positions;

   public static FixedPlacementModifier of(BlockPos... positions) {
      return new FixedPlacementModifier(List.of(positions));
   }

   private FixedPlacementModifier(List positions) {
      this.positions = positions;
   }

   public Stream getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
      int i = ChunkSectionPos.getSectionCoord(pos.getX());
      int j = ChunkSectionPos.getSectionCoord(pos.getZ());
      boolean bl = false;
      Iterator var7 = this.positions.iterator();

      while(var7.hasNext()) {
         BlockPos blockPos = (BlockPos)var7.next();
         if (chunkSectionMatchesPos(i, j, blockPos)) {
            bl = true;
            break;
         }
      }

      return !bl ? Stream.empty() : this.positions.stream().filter((posx) -> {
         return chunkSectionMatchesPos(i, j, posx);
      });
   }

   private static boolean chunkSectionMatchesPos(int chunkSectionX, int chunkSectionZ, BlockPos pos) {
      return chunkSectionX == ChunkSectionPos.getSectionCoord(pos.getX()) && chunkSectionZ == ChunkSectionPos.getSectionCoord(pos.getZ());
   }

   public PlacementModifierType getType() {
      return PlacementModifierType.FIXED_PLACEMENT;
   }
}
