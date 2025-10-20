package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Unique
    private static final RegistryKey<World> THE_SLIP = RegistryKey.of(RegistryKeys.WORLD, Identifier.of("minecraft", "the_slip"));

    @Inject(method = "onBlockAdded", at = @At("HEAD"))
    private void onWaterOrLavaPlaced(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (world.getRegistryKey().equals(THE_SLIP)) {
            FluidBlock thisBlock = (FluidBlock) (Object) this;

            // Check if this is water
            if (thisBlock == Blocks.WATER) {
                // Convert to packed ice
                world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), Block.NOTIFY_ALL);
                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.BLOCKS, 1.0F, 2.0F);
            }
            // Check if this is lava
            else if (thisBlock == Blocks.LAVA) {
                // Convert to blackstone
                world.setBlockState(pos, Blocks.BLACKSTONE.getDefaultState(), Block.NOTIFY_ALL);
                world.playSound(null, pos, Blocks.ANCIENT_DEBRIS.getDefaultState().getSoundGroup().getBreakSound(),
                        SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
