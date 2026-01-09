package net.minecraft.world.dimension;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;

public class PortalForcer {
   public static final int field_31810 = 3;
   private static final int field_52248 = 16;
   private static final int field_52249 = 128;
   private static final int field_31813 = 5;
   private static final int field_31814 = 4;
   private static final int field_31815 = 3;
   private static final int field_31816 = -1;
   private static final int field_31817 = 4;
   private static final int field_31818 = -1;
   private static final int field_31819 = 3;
   private static final int field_31820 = -1;
   private static final int field_31821 = 2;
   private static final int field_31822 = -1;
   private final ServerWorld world;

   public PortalForcer(ServerWorld world) {
      this.world = world;
   }

   public Optional getPortalPos(BlockPos pos, boolean destIsNether, WorldBorder worldBorder) {
      PointOfInterestStorage pointOfInterestStorage = this.world.getPointOfInterestStorage();
      int i = destIsNether ? 16 : 128;
      pointOfInterestStorage.preloadChunks(this.world, pos, i);
      Stream var10000 = pointOfInterestStorage.getInSquare((poiType) -> {
         return poiType.matchesKey(PointOfInterestTypes.NETHER_PORTAL);
      }, pos, i, PointOfInterestStorage.OccupationStatus.ANY).map(PointOfInterest::getPos);
      Objects.requireNonNull(worldBorder);
      return var10000.filter(worldBorder::contains).filter((portalPos) -> {
         return this.world.getBlockState(portalPos).contains(Properties.HORIZONTAL_AXIS);
      }).min(Comparator.comparingDouble((portalPos) -> {
         return portalPos.getSquaredDistance(pos);
      }).thenComparingInt(Vec3i::getY));
   }

   public Optional createPortal(BlockPos pos, Direction.Axis axis) {
      Direction direction = Direction.get(Direction.AxisDirection.POSITIVE, axis);
      double d = -1.0;
      BlockPos blockPos = null;
      double e = -1.0;
      BlockPos blockPos2 = null;
      WorldBorder worldBorder = this.world.getWorldBorder();
      int i = Math.min(this.world.getTopYInclusive(), this.world.getBottomY() + this.world.getLogicalHeight() - 1);
      int j = true;
      BlockPos.Mutable mutable = pos.mutableCopy();
      Iterator var14 = BlockPos.iterateInSquare(pos, 16, Direction.EAST, Direction.SOUTH).iterator();

      while(true) {
         BlockPos.Mutable mutable2;
         int k;
         int l;
         int m;
         int n;
         do {
            do {
               if (!var14.hasNext()) {
                  if (d == -1.0 && e != -1.0) {
                     blockPos = blockPos2;
                     d = e;
                  }

                  int o;
                  int p;
                  if (d == -1.0) {
                     o = Math.max(this.world.getBottomY() - -1, 70);
                     p = i - 9;
                     if (p < o) {
                        return Optional.empty();
                     }

                     blockPos = (new BlockPos(pos.getX() - direction.getOffsetX() * 1, MathHelper.clamp(pos.getY(), o, p), pos.getZ() - direction.getOffsetZ() * 1)).toImmutable();
                     blockPos = worldBorder.clampFloored(blockPos);
                     Direction direction2 = direction.rotateYClockwise();

                     for(l = -1; l < 2; ++l) {
                        for(m = 0; m < 2; ++m) {
                           for(n = -1; n < 3; ++n) {
                              BlockState blockState = n < 0 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
                              mutable.set((Vec3i)blockPos, m * direction.getOffsetX() + l * direction2.getOffsetX(), n, m * direction.getOffsetZ() + l * direction2.getOffsetZ());
                              this.world.setBlockState(mutable, blockState);
                           }
                        }
                     }
                  }

                  for(o = -1; o < 3; ++o) {
                     for(p = -1; p < 4; ++p) {
                        if (o == -1 || o == 2 || p == -1 || p == 3) {
                           mutable.set((Vec3i)blockPos, o * direction.getOffsetX(), p, o * direction.getOffsetZ());
                           this.world.setBlockState(mutable, Blocks.OBSIDIAN.getDefaultState(), 3);
                        }
                     }
                  }

                  BlockState blockState2 = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, axis);

                  for(p = 0; p < 2; ++p) {
                     for(k = 0; k < 3; ++k) {
                        mutable.set((Vec3i)blockPos, p * direction.getOffsetX(), k, p * direction.getOffsetZ());
                        this.world.setBlockState(mutable, blockState2, 18);
                     }
                  }

                  return Optional.of(new BlockLocating.Rectangle(blockPos.toImmutable(), 2, 3));
               }

               mutable2 = (BlockPos.Mutable)var14.next();
               k = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, mutable2.getX(), mutable2.getZ()));
            } while(!worldBorder.contains((BlockPos)mutable2));
         } while(!worldBorder.contains((BlockPos)mutable2.move(direction, 1)));

         mutable2.move(direction.getOpposite(), 1);

         for(l = k; l >= this.world.getBottomY(); --l) {
            mutable2.setY(l);
            if (this.isBlockStateValid(mutable2)) {
               for(m = l; l > this.world.getBottomY() && this.isBlockStateValid(mutable2.move(Direction.DOWN)); --l) {
               }

               if (l + 4 <= i) {
                  n = m - l;
                  if (n <= 0 || n >= 3) {
                     mutable2.setY(l);
                     if (this.isValidPortalPos(mutable2, mutable, direction, 0)) {
                        double f = pos.getSquaredDistance(mutable2);
                        if (this.isValidPortalPos(mutable2, mutable, direction, -1) && this.isValidPortalPos(mutable2, mutable, direction, 1) && (d == -1.0 || d > f)) {
                           d = f;
                           blockPos = mutable2.toImmutable();
                        }

                        if (d == -1.0 && (e == -1.0 || e > f)) {
                           e = f;
                           blockPos2 = mutable2.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isBlockStateValid(BlockPos.Mutable pos) {
      BlockState blockState = this.world.getBlockState(pos);
      return blockState.isReplaceable() && blockState.getFluidState().isEmpty();
   }

   private boolean isValidPortalPos(BlockPos pos, BlockPos.Mutable temp, Direction portalDirection, int distanceOrthogonalToPortal) {
      Direction direction = portalDirection.rotateYClockwise();

      for(int i = -1; i < 3; ++i) {
         for(int j = -1; j < 4; ++j) {
            temp.set((Vec3i)pos, portalDirection.getOffsetX() * i + direction.getOffsetX() * distanceOrthogonalToPortal, j, portalDirection.getOffsetZ() * i + direction.getOffsetZ() * distanceOrthogonalToPortal);
            if (j < 0 && !this.world.getBlockState(temp).isSolid()) {
               return false;
            }

            if (j >= 0 && !this.isBlockStateValid(temp)) {
               return false;
            }
         }
      }

      return true;
   }
}
