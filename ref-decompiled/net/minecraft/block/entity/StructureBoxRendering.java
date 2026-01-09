package net.minecraft.block.entity;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public interface StructureBoxRendering {
   RenderMode getRenderMode();

   StructureBox getStructureBox();

   public static enum RenderMode {
      NONE,
      BOX,
      BOX_AND_INVISIBLE_BLOCKS;

      // $FF: synthetic method
      private static RenderMode[] method_66715() {
         return new RenderMode[]{NONE, BOX, BOX_AND_INVISIBLE_BLOCKS};
      }
   }

   public static record StructureBox(BlockPos localPos, Vec3i size) {
      public StructureBox(BlockPos blockPos, Vec3i vec3i) {
         this.localPos = blockPos;
         this.size = vec3i;
      }

      public static StructureBox create(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
         int i = Math.min(minX, maxX);
         int j = Math.min(minY, maxY);
         int k = Math.min(minZ, maxZ);
         return new StructureBox(new BlockPos(i, j, k), new Vec3i(Math.max(minX, maxX) - i, Math.max(minY, maxY) - j, Math.max(minZ, maxZ) - k));
      }

      public BlockPos localPos() {
         return this.localPos;
      }

      public Vec3i size() {
         return this.size;
      }
   }
}
