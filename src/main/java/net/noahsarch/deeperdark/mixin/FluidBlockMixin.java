package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.portal.SlipPortalHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LiquidBlock.class)
public class FluidBlockMixin {
    @Unique
    private static final ResourceKey<Level> THE_SLIP = ResourceKey.create(Registries.WORLD, Identifier.fromNamespaceAndPath("minecraft", "the_slip"));

    @Inject(method = "onBlockAdded", at = @At("HEAD"), cancellable = true)
    private void onWaterOrLavaPlaced(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (world.getRegistryKey().equals(THE_SLIP)) {
            LiquidBlock thisBlock = (LiquidBlock) (Object) this;

            // Check if this is water
            if (thisBlock == Blocks.WATER) {
                // Don't convert portal water to ice!
                if (SlipPortalHandler.isPortalWaterBlock(pos)) {
                    return;
                }

                // Also check if this is near a portal (within 3 blocks) - prevents cocoon
                if (SlipPortalHandler.isNearPortal(pos, 3)) {
                    // Remove the water instead of converting to ice
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
                    ci.cancel();
                    return;
                }

                // Convert to packed ice
                world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), Block.NOTIFY_ALL);
                world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0F, 2.0F);
            }
            // Check if this is lava
            else if (thisBlock == Blocks.LAVA) {
                // Convert to blackstone
                world.setBlockState(pos, Blocks.BLACKSTONE.getDefaultState(), Block.NOTIFY_ALL);
                world.playSound(null, pos, Blocks.ANCIENT_DEBRIS.getDefaultState().getSoundGroup().getBreakSound(),
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
