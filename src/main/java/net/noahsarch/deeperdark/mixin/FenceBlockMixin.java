package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FenceBlock.class)
public class FenceBlockMixin {

    @Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
    private void deeperdark$connectToWalls(BlockState state, boolean faceSolid, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(BlockTags.WALLS)) cir.setReturnValue(true);
    }
}
