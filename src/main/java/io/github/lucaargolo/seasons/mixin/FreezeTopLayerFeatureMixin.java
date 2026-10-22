package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SnowAndFreezeFeature.class)
public class FreezeTopLayerFeatureMixin {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), method = "place", index = 0)
    public BlockPos setMeltableIce(BlockPos pos) {
        FabricSeasons.setMeltable(pos);
        return pos;
    }

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), method = "place", index = 0)
    public BlockPos setMeltableSnow(BlockPos pos) {
        FabricSeasons.setMeltable(pos);
        return pos;
    }
}
