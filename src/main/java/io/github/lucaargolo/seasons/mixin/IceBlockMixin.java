package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Meltable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IceBlock.class)
public abstract class IceBlockMixin extends Block implements Meltable {

    public IceBlockMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Shadow protected abstract void melt(BlockState state, Level world, BlockPos pos);

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (this == Blocks.ICE) {
            boolean warmEnough = world.getBiome(pos).value().getBaseTemperature() >= 0.15F;
            if (!warmEnough) {
                ci.cancel();
                return;
            }
            if (FabricSeasons.getPlacedMeltablesState(world).isManuallyPlaced(pos)) {
                if (FabricSeasons.CONFIG.shouldIceNearWaterMelt()) {
                    boolean nearWater = false;
                    for (BlockPos nearPos : BlockPos.withinManhattan(pos, 1, 1, 1)) {
                        if (world.getFluidState(nearPos).is(FluidTags.WATER)) {
                            nearWater = true;
                            break;
                        }
                    }
                    if (nearWater) {
                        this.melt(state, world, pos);
                    }
                }
                ci.cancel();
                return;
            }
            // Warm enough and not manually placed: melt regardless of light level.
            // Vanilla requires light >= 11, but season-induced ice must melt when warmth returns.
            this.melt(state, world, pos);
            ci.cancel();
        }
    }
}
