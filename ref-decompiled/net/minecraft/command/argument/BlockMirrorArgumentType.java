package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.BlockMirror;

public class BlockMirrorArgumentType extends EnumArgumentType {
   private BlockMirrorArgumentType() {
      super(BlockMirror.CODEC, BlockMirror::values);
   }

   public static EnumArgumentType blockMirror() {
      return new BlockMirrorArgumentType();
   }

   public static BlockMirror getBlockMirror(CommandContext context, String id) {
      return (BlockMirror)context.getArgument(id, BlockMirror.class);
   }
}
