package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WallBlock.class)
public class WallBlockMixin {

    @Inject(method = "connectsTo", at = @At("RETURN"), cancellable = true)
    private void deeperdark$connectToFences(BlockState state, boolean faceSolid, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (state.is(BlockTags.FENCES)) cir.setReturnValue(true);
    }
}
