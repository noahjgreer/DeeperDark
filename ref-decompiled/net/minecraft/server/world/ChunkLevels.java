package net.minecraft.server.world;

import net.minecraft.world.chunk.ChunkGenerationStep;
import net.minecraft.world.chunk.ChunkGenerationSteps;
import net.minecraft.world.chunk.ChunkStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class ChunkLevels {
   private static final int FULL = 33;
   private static final int BLOCK_TICKING = 32;
   private static final int ENTITY_TICKING = 31;
   private static final ChunkGenerationStep FULL_GENERATION_STEP;
   public static final int FULL_GENERATION_REQUIRED_LEVEL;
   public static final int INACCESSIBLE;

   @Nullable
   public static ChunkStatus getStatus(int level) {
      return getStatusForAdditionalLevel(level - 33, (ChunkStatus)null);
   }

   @Nullable
   @Contract("_,!null->!null;_,_->_")
   public static ChunkStatus getStatusForAdditionalLevel(int additionalLevel, @Nullable ChunkStatus emptyStatus) {
      if (additionalLevel > FULL_GENERATION_REQUIRED_LEVEL) {
         return emptyStatus;
      } else {
         return additionalLevel <= 0 ? ChunkStatus.FULL : FULL_GENERATION_STEP.accumulatedDependencies().get(additionalLevel);
      }
   }

   public static ChunkStatus getStatusForAdditionalLevel(int level) {
      return getStatusForAdditionalLevel(level, ChunkStatus.EMPTY);
   }

   public static int getLevelFromStatus(ChunkStatus status) {
      return 33 + FULL_GENERATION_STEP.getAdditionalLevel(status);
   }

   public static ChunkLevelType getType(int level) {
      if (level <= 31) {
         return ChunkLevelType.ENTITY_TICKING;
      } else if (level <= 32) {
         return ChunkLevelType.BLOCK_TICKING;
      } else {
         return level <= 33 ? ChunkLevelType.FULL : ChunkLevelType.INACCESSIBLE;
      }
   }

   public static int getLevelFromType(ChunkLevelType type) {
      int var10000;
      switch (type) {
         case INACCESSIBLE:
            var10000 = INACCESSIBLE;
            break;
         case FULL:
            var10000 = 33;
            break;
         case BLOCK_TICKING:
            var10000 = 32;
            break;
         case ENTITY_TICKING:
            var10000 = 31;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static boolean shouldTickEntities(int level) {
      return level <= 31;
   }

   public static boolean shouldTickBlocks(int level) {
      return level <= 32;
   }

   public static boolean isAccessible(int level) {
      return level <= INACCESSIBLE;
   }

   static {
      FULL_GENERATION_STEP = ChunkGenerationSteps.GENERATION.get(ChunkStatus.FULL);
      FULL_GENERATION_REQUIRED_LEVEL = FULL_GENERATION_STEP.accumulatedDependencies().getMaxLevel();
      INACCESSIBLE = 33 + FULL_GENERATION_REQUIRED_LEVEL;
   }
}
