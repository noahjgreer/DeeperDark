package net.minecraft.predicate.block;

import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockPredicate implements Predicate {
   private final Block block;

   public BlockPredicate(Block block) {
      this.block = block;
   }

   public static BlockPredicate make(Block block) {
      return new BlockPredicate(block);
   }

   public boolean test(@Nullable BlockState blockState) {
      return blockState != null && blockState.isOf(this.block);
   }

   // $FF: synthetic method
   public boolean test(@Nullable final Object context) {
      return this.test((BlockState)context);
   }
}
