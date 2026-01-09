package net.minecraft.server.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public record BlockEvent(BlockPos pos, Block block, int type, int data) {
   public BlockEvent(BlockPos pos, Block block, int type, int data) {
      this.pos = pos;
      this.block = block;
      this.type = type;
      this.data = data;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public Block block() {
      return this.block;
   }

   public int type() {
      return this.type;
   }

   public int data() {
      return this.data;
   }
}
