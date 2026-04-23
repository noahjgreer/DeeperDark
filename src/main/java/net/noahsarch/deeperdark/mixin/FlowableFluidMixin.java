package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlowingFluid.class)
public class FlowableFluidMixin {
    @Inject(method = "canPassThroughWall", at = @At("HEAD"), cancellable = true)
    private static void deeperdark$blockStructureVoidFlow(Direction face, BlockGetter world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState, CallbackInfoReturnable<Boolean> cir) {
        // Check if we are in range of an active sponge
        if (world instanceof net.noahsarch.deeperdark.util.ActiveSpongeTracker tracker) {
             java.util.Set<BlockPos> sponges = tracker.getActiveSponges();
             if (!sponges.isEmpty()) {
                 int range = 3;
                 // Optimization: quick check if any sponge is close
                 for (BlockPos spongePos : sponges) {
                     if (spongePos.distSqr(pos) <= (range * range) + 2) { // +2 for margin/geometry
                          // More precise check if matches intent
                          // Iterate radius 3 box
                          if (Math.abs(spongePos.getX() - pos.getX()) <= range &&
                              Math.abs(spongePos.getY() - pos.getY()) <= range &&
                              Math.abs(spongePos.getZ() - pos.getZ()) <= range) {
                                  cir.setReturnValue(false);
                                  return;
                          }
                     }
                 }
             }
        }
    }
}

