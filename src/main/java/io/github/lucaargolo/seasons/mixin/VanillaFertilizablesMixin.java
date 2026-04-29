package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.utils.FertilizableUtil;
import io.github.lucaargolo.seasons.utils.SeasonalFertilizable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {CropBlock.class, CocoaBlock.class, StemBlock.class, SaplingBlock.class, SweetBerryBushBlock.class})
public abstract class VanillaFertilizablesMixin extends Block implements BonemealableBlock, SeasonalFertilizable {

    public VanillaFertilizablesMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "randomTick", cancellable = true)
    public void randomTickInject(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        FertilizableUtil.randomTickInject(this, state, world, pos, random, ci);
    }

    @Inject(at = @At("HEAD"), method = "performBonemeal", cancellable = true)
    public void growInject(ServerLevel world, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        FertilizableUtil.growInject(this, world, random, pos, state, ci);
    }
}
