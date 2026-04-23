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
    private static final ResourceKey<Level> THE_SLIP = ResourceKey.create(Registries.DIMENSION, Identifier.fromNamespaceAndPath("minecraft", "the_slip"));

    @Inject(method = "onPlace", at = @At("HEAD"), cancellable = true)
    private void onWaterOrLavaPlaced(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        if (world.dimension().equals(THE_SLIP)) {
            LiquidBlock thisBlock = (LiquidBlock) (Object) this;

            if (thisBlock == Blocks.WATER) {
                if (SlipPortalHandler.isPortalWaterBlock(pos)) {
                    return;
                }

                if (SlipPortalHandler.isNearPortal(pos, 3)) {
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                    ci.cancel();
                    return;
                }

                world.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), Block.UPDATE_ALL);
                world.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 1.0F, 2.0F);
            }
            else if (thisBlock == Blocks.LAVA) {
                world.setBlock(pos, Blocks.BLACKSTONE.defaultBlockState(), Block.UPDATE_ALL);
                world.playSound(null, pos, Blocks.ANCIENT_DEBRIS.defaultBlockState().getSoundType().getBreakSound(),
                        SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        }
    }
}
