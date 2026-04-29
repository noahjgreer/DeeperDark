package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Meltable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 0), method = "tickPrecipitation", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableIce(BlockPos pos, CallbackInfo ci, BlockPos topPos, BlockPos belowPos, Biome biome) {
        FabricSeasons.setMeltable(belowPos);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1), method = "tickPrecipitation", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableLayeredSnow(BlockPos pos, CallbackInfo ci, BlockPos topPos, BlockPos belowPos, Biome biome, int maxHeight, BlockState state, int currentLayers, BlockState newState) {
        FabricSeasons.setMeltable(topPos);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2), method = "tickPrecipitation", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableSnow(BlockPos pos, CallbackInfo ci, BlockPos topPos, BlockPos belowPos, Biome biome, int maxHeight, BlockState state) {
        FabricSeasons.setMeltable(topPos);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;handlePrecipitation(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/biome/Biome$Precipitation;)V"), method = "tickPrecipitation", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setReplacedMeltable(BlockPos pos, CallbackInfo ci, BlockPos topPos, BlockPos belowPos, Biome biome, int maxHeight, Biome.Precipitation precipitation, BlockState belowState) {
        if (FabricSeasons.CONFIG.shouldSnowReplaceVegetation())
            Meltable.replaceBlockOnSnow((ServerLevel) (Object) this, topPos, biome);
    }
}
