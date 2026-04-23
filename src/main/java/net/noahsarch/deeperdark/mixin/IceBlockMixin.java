package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class IceBlockMixin {

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void onPlaced(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (world.isClientSide()) return;

        if (world.dimension() == Level.NETHER) {
            Block block = state.getBlock();
            boolean isIce = block == Blocks.ICE
                    || block == Blocks.PACKED_ICE
                    || block == Blocks.BLUE_ICE
                    || block == Blocks.FROSTED_ICE
                    || block.getDescriptionId().toLowerCase().contains("ice");

            if (isIce) {
                world.removeBlock(pos, false);

                if (world instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.02);
                    server.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
    }

}
