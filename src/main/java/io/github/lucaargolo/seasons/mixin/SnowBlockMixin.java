package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Meltable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SnowLayerBlock.class)
public abstract class SnowBlockMixin extends Block implements Meltable {

    public SnowBlockMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (world.getBrightness(LightLayer.SKY, pos) > 0
                && world.getBiome(pos).value().getBaseTemperature() >= 0.15F
                && !FabricSeasons.getPlacedMeltablesState(world).isManuallyPlaced(pos)) {
            Block.dropResources(state, world, pos);
            BlockState replacedState = FabricSeasons.getReplacedMeltablesState(world).getReplaced(pos);
            if (replacedState != null) {
                if (replacedState.hasProperty(DoublePlantBlock.HALF)
                        && replacedState.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    BlockState replacedUpperState = replacedState.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                    if (replacedState.canSurvive(world, pos) && replacedUpperState.canSurvive(world, pos.above())) {
                        world.setBlockAndUpdate(pos, replacedState);
                        world.setBlockAndUpdate(pos.above(), replacedUpperState);
                    } else {
                        world.removeBlock(pos, false);
                    }
                } else if (replacedState.canSurvive(world, pos)) {
                    world.setBlockAndUpdate(pos, replacedState);
                } else {
                    world.removeBlock(pos, false);
                }
            } else {
                world.removeBlock(pos, false);
            }
            ci.cancel();
        }
    }
}
