package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class IceBlockMixin {

    @Inject(method = "onPlaced", at = @At("HEAD"))
    private void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        // Only run on the server
        if (world.isClient()) return;

        // Check if we're in the Nether
        if (world.getRegistryKey() == World.NETHER) {
            // Detect ice blocks: explicit vanilla ice blocks and any block whose translation key contains "ice"
            Block block = state.getBlock();
            boolean isIce = block == Blocks.ICE
                    || block == Blocks.PACKED_ICE
                    || block == Blocks.BLUE_ICE
                    || block == Blocks.FROSTED_ICE
                    || block.getTranslationKey().toLowerCase().contains("ice");

            if (isIce) {
                // Remove the block (evaporate)
                world.removeBlock(pos, false);

                // Spawn particles and sound on the server for clients to see/hear
                if (world instanceof ServerWorld server) {
                    server.spawnParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.02);
                    server.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
            }
        }
    }

}
