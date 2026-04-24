package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.noahsarch.deeperdark.duck.LeafDecayTickerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class LeavesBeGoneServerLevelMixin extends Level implements LeafDecayTickerLevel {

    @Unique
    private final LevelTicks<Block> deeperdark$leafDecayTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded);

    @Unique
    private boolean deeperdark$leafWasNotRandomlyTicking;

    @Shadow
    public abstract boolean isPositionTickingWithEntitiesLoaded(long key);

    protected LeavesBeGoneServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Override
    public LevelTicks<Block> deeperdark$getLeafDecayTicks() {
        return this.deeperdark$leafDecayTicks;
    }

    @Inject(method = "tick", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/ticks/LevelTicks;tick(JILjava/util/function/BiConsumer;)V",
            shift = At.Shift.AFTER,
            ordinal = 0
    ))
    private void deeperdark$tickLeafDecay(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (!DeeperDarkConfig.get().leavesBeGoneEnabled) return;
        this.deeperdark$leafDecayTicks.tick(this.getGameTime(), 65536, (pos, block) -> {
            BlockState blockState = this.getBlockState(pos);
            if (blockState.is(block)) {
                blockState.randomTick(ServerLevel.class.cast(this), pos, this.random);
            }
        });
    }

    @Inject(method = "tickBlock", at = @At("HEAD"))
    private void deeperdark$captureLeafState(BlockPos pos, Block block, CallbackInfo ci) {
        if (!DeeperDarkConfig.get().leavesBeGoneEnabled) {
            this.deeperdark$leafWasNotRandomlyTicking = false;
            return;
        }
        BlockState state = this.getBlockState(pos);
        this.deeperdark$leafWasNotRandomlyTicking = state.is(block) && state.is(BlockTags.LEAVES) && !state.isRandomlyTicking();
    }

    @Inject(method = "tickBlock", at = @At("TAIL"))
    private void deeperdark$scheduleLeafDecay(BlockPos pos, Block block, CallbackInfo ci) {
        if (!this.deeperdark$leafWasNotRandomlyTicking) return;
        if (!this.getBlockState(pos).isRandomlyTicking()) return;

        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();
        int minDelay = config.leavesDecayMinTicks;
        int maxDelay = Math.max(minDelay, config.leavesDecayMaxTicks);
        int delay = minDelay + this.random.nextInt(Math.max(1, maxDelay - minDelay));
        this.deeperdark$leafDecayTicks.schedule(new ScheduledTick<>(block, pos, this.getLevelData().getGameTime() + delay, this.nextSubTickCount()));
    }
}
