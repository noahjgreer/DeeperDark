package net.minecraft.command.argument;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.BlockRotation;

public class BlockRotationArgumentType extends EnumArgumentType {
   private BlockRotationArgumentType() {
      super(BlockRotation.CODEC, BlockRotation::values);
   }

   public static BlockRotationArgumentType blockRotation() {
      return new BlockRotationArgumentType();
   }

   public static BlockRotation getBlockRotation(CommandContext context, String id) {
      return (BlockRotation)context.getArgument(id, BlockRotation.class);
   }
}
