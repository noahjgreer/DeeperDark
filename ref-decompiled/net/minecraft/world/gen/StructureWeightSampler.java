package net.minecraft.world.gen;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Iterator;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;

public class StructureWeightSampler implements DensityFunctionTypes.Beardifying {
   public static final int INDEX_OFFSET = 12;
   private static final int EDGE_LENGTH = 24;
   private static final float[] STRUCTURE_WEIGHT_TABLE = (float[])Util.make(new float[13824], (array) -> {
      for(int i = 0; i < 24; ++i) {
         for(int j = 0; j < 24; ++j) {
            for(int k = 0; k < 24; ++k) {
               array[i * 24 * 24 + j * 24 + k] = (float)calculateStructureWeight(j - 12, k - 12, i - 12);
            }
         }
      }

   });
   private final ObjectListIterator pieceIterator;
   private final ObjectListIterator junctionIterator;

   public static StructureWeightSampler createStructureWeightSampler(StructureAccessor world, ChunkPos pos) {
      int i = pos.getStartX();
      int j = pos.getStartZ();
      ObjectList objectList = new ObjectArrayList(10);
      ObjectList objectList2 = new ObjectArrayList(32);
      world.getStructureStarts(pos, (structure) -> {
         return structure.getTerrainAdaptation() != StructureTerrainAdaptation.NONE;
      }).forEach((start) -> {
         StructureTerrainAdaptation structureTerrainAdaptation = start.getStructure().getTerrainAdaptation();
         Iterator var7 = start.getChildren().iterator();

         while(true) {
            while(true) {
               StructurePiece structurePiece;
               do {
                  if (!var7.hasNext()) {
                     return;
                  }

                  structurePiece = (StructurePiece)var7.next();
               } while(!structurePiece.intersectsChunk(pos, 12));

               if (structurePiece instanceof PoolStructurePiece poolStructurePiece) {
                  StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                  if (projection == StructurePool.Projection.RIGID) {
                     objectList.add(new Piece(poolStructurePiece.getBoundingBox(), structureTerrainAdaptation, poolStructurePiece.getGroundLevelDelta()));
                  }

                  Iterator var11 = poolStructurePiece.getJunctions().iterator();

                  while(var11.hasNext()) {
                     JigsawJunction jigsawJunction = (JigsawJunction)var11.next();
                     int ix = jigsawJunction.getSourceX();
                     int jx = jigsawJunction.getSourceZ();
                     if (ix > i - 12 && jx > j - 12 && ix < i + 15 + 12 && jx < j + 15 + 12) {
                        objectList2.add(jigsawJunction);
                     }
                  }
               } else {
                  objectList.add(new Piece(structurePiece.getBoundingBox(), structureTerrainAdaptation, 0));
               }
            }
         }
      });
      return new StructureWeightSampler(objectList.iterator(), objectList2.iterator());
   }

   @VisibleForTesting
   public StructureWeightSampler(ObjectListIterator pieceIterator, ObjectListIterator junctionIterator) {
      this.pieceIterator = pieceIterator;
      this.junctionIterator = junctionIterator;
   }

   public double sample(DensityFunction.NoisePos pos) {
      int i = pos.blockX();
      int j = pos.blockY();
      int k = pos.blockZ();

      double d;
      int l;
      int m;
      double var10001;
      for(d = 0.0; this.pieceIterator.hasNext(); d += var10001) {
         Piece piece = (Piece)this.pieceIterator.next();
         BlockBox blockBox = piece.box();
         l = piece.groundLevelDelta();
         m = Math.max(0, Math.max(blockBox.getMinX() - i, i - blockBox.getMaxX()));
         int n = Math.max(0, Math.max(blockBox.getMinZ() - k, k - blockBox.getMaxZ()));
         int o = blockBox.getMinY() + l;
         int p = j - o;
         int var10000;
         switch (piece.terrainAdjustment()) {
            case NONE:
               var10000 = 0;
               break;
            case BURY:
            case BEARD_THIN:
               var10000 = p;
               break;
            case BEARD_BOX:
               var10000 = Math.max(0, Math.max(o - j, j - blockBox.getMaxY()));
               break;
            case ENCAPSULATE:
               var10000 = Math.max(0, Math.max(blockBox.getMinY() - j, j - blockBox.getMaxY()));
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         int q = var10000;
         switch (piece.terrainAdjustment()) {
            case NONE:
               var10001 = 0.0;
               break;
            case BURY:
               var10001 = getMagnitudeWeight((double)m, (double)q / 2.0, (double)n);
               break;
            case BEARD_THIN:
            case BEARD_BOX:
               var10001 = getStructureWeight(m, q, n, p) * 0.8;
               break;
            case ENCAPSULATE:
               var10001 = getMagnitudeWeight((double)m / 2.0, (double)q / 2.0, (double)n / 2.0) * 0.8;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }
      }

      this.pieceIterator.back(Integer.MAX_VALUE);

      while(this.junctionIterator.hasNext()) {
         JigsawJunction jigsawJunction = (JigsawJunction)this.junctionIterator.next();
         int r = i - jigsawJunction.getSourceX();
         l = j - jigsawJunction.getSourceGroundY();
         m = k - jigsawJunction.getSourceZ();
         d += getStructureWeight(r, l, m, l) * 0.4;
      }

      this.junctionIterator.back(Integer.MAX_VALUE);
      return d;
   }

   public double minValue() {
      return Double.NEGATIVE_INFINITY;
   }

   public double maxValue() {
      return Double.POSITIVE_INFINITY;
   }

   private static double getMagnitudeWeight(double x, double y, double z) {
      double d = MathHelper.magnitude(x, y, z);
      return MathHelper.clampedMap(d, 0.0, 6.0, 1.0, 0.0);
   }

   private static double getStructureWeight(int x, int y, int z, int yy) {
      int i = x + 12;
      int j = y + 12;
      int k = z + 12;
      if (indexInBounds(i) && indexInBounds(j) && indexInBounds(k)) {
         double d = (double)yy + 0.5;
         double e = MathHelper.squaredMagnitude((double)x, d, (double)z);
         double f = -d * MathHelper.fastInverseSqrt(e / 2.0) / 2.0;
         return f * (double)STRUCTURE_WEIGHT_TABLE[k * 24 * 24 + i * 24 + j];
      } else {
         return 0.0;
      }
   }

   private static boolean indexInBounds(int i) {
      return i >= 0 && i < 24;
   }

   private static double calculateStructureWeight(int x, int y, int z) {
      return structureWeight(x, (double)y + 0.5, z);
   }

   private static double structureWeight(int x, double y, int z) {
      double d = MathHelper.squaredMagnitude((double)x, y, (double)z);
      double e = Math.pow(Math.E, -d / 16.0);
      return e;
   }

   @VisibleForTesting
   public static record Piece(BlockBox box, StructureTerrainAdaptation terrainAdjustment, int groundLevelDelta) {
      public Piece(BlockBox blockBox, StructureTerrainAdaptation structureTerrainAdaptation, int i) {
         this.box = blockBox;
         this.terrainAdjustment = structureTerrainAdaptation;
         this.groundLevelDelta = i;
      }

      public BlockBox box() {
         return this.box;
      }

      public StructureTerrainAdaptation terrainAdjustment() {
         return this.terrainAdjustment;
      }

      public int groundLevelDelta() {
         return this.groundLevelDelta;
      }
   }
}
