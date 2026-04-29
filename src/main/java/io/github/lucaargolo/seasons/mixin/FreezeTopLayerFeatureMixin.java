package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SnowAndFreezeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SnowAndFreezeFeature.class)
public class FreezeTopLayerFeatureMixin {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0), method = "place", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableIce(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir, WorldGenLevel level, BlockPos origin, BlockPos.MutableBlockPos mutable, BlockPos.MutableBlockPos mutable2, int i, int j, int k, int l, int m, Biome biome) {
        FabricSeasons.setMeltable(mutable2);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), method = "place", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void setMeltableSnow(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> cir, WorldGenLevel level, BlockPos origin, BlockPos.MutableBlockPos mutable, BlockPos.MutableBlockPos mutable2, int i, int j, int k, int l, int m, Biome biome) {
        FabricSeasons.setMeltable(mutable);
    }
}
