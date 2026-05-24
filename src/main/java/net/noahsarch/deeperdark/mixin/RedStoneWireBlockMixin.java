package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.noahsarch.deeperdark.event.CollarEvents;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {

    /**
     * Suppresses the redstone engine's power recalculation for wires that a
     * player is standing on via the collar trinket. Without this, every
     * neighborChanged cascade would immediately reset the wire back to 0,
     * causing a pulse every tick and preventing the visual from updating.
     */
    @Inject(method = "neighborChanged", at = @At("HEAD"), cancellable = true)
    private void deeperdark$holdPlayerPowered(
            BlockState state, Level level, BlockPos pos, Block block,
            @Nullable Orientation orientation, boolean movedByPiston, CallbackInfo ci) {
        if (!level.isClientSide() && CollarEvents.isPlayerPoweredWire(pos)) {
            ci.cancel();
        }
    }
}
