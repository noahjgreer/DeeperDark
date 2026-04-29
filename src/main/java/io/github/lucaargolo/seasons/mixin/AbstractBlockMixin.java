package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Meltable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class AbstractBlockMixin {

    @Inject(at = @At("HEAD"), method = "affectNeighborsAfterRemoval")
    public void checkIfMeltableReplaced(BlockState state, ServerLevel world, BlockPos pos, boolean moved, CallbackInfo ci) {
        if (state.getBlock() instanceof Meltable meltableBlock) {
            meltableBlock.onMeltableReplaced(world, pos);
        }
    }

    @Inject(at = @At("HEAD"), method = "onPlace")
    public void checkIfMeltableAdded(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (!FabricSeasons.isMeltable(pos) && world instanceof ServerLevel serverLevel && state.getBlock() instanceof Meltable meltableBlock) {
            meltableBlock.onMeltableManuallyPlaced(serverLevel, pos);
        }
    }
}
