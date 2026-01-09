package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.SpongeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.block.entity.ActiveSpongeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpongeBlock.class)
public class SpongeBlockMixin {
    @Inject(method = "update", at = @At("TAIL"))
    private void deeperdark$activateSponge(World world, BlockPos pos, CallbackInfo ci) {
        if (!world.isClient && world.getBlockState(pos).isOf(Blocks.WET_SPONGE)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ActiveSpongeBlockEntity activeSponge) {
                activeSponge.setActive(true);
            }
        }
    }
}

