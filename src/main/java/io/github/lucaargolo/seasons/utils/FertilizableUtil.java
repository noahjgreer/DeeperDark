package io.github.lucaargolo.seasons.utils;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.resources.CropConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class FertilizableUtil {

    private static boolean seasons$shouldInject = true;

    @SuppressWarnings("deprecation")
    public static <F extends Block & BonemealableBlock> void randomTickInject(F fertilizable, BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (FabricSeasons.CONFIG.isSeasonMessingCrops() && seasons$shouldInject) {
            float multiplier = 1f + getMultiplier(world, pos, state);
            while (multiplier > 0f) {
                multiplier -= 1f;
                float rand = random.nextFloat();
                if (multiplier >= rand) {
                    seasons$shouldInject = false;
                    fertilizable.randomTick(state, world, pos, random);
                    multiplier -= 1f;
                }
            }
            seasons$shouldInject = true;
            ci.cancel();
        }
    }

    public static <F extends Block & BonemealableBlock> void growInject(F fertilizable, ServerLevel world, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (FabricSeasons.CONFIG.isSeasonMessingBonemeal() && seasons$shouldInject) {
            float multiplier = 1f + getMultiplier(world, pos, state);
            while (multiplier > 0f) {
                multiplier -= 1f;
                float rand = random.nextFloat();
                if (multiplier >= rand) {
                    seasons$shouldInject = false;
                    fertilizable.performBonemeal(world, random, pos, state);
                }
            }
            seasons$shouldInject = true;
            ci.cancel();
        }
    }

    public static float getMultiplier(ServerLevel world, BlockPos pos, BlockState state) {
        float multiplier;
        if (FabricSeasons.CONFIG.doCropsGrowsNormallyUnderground() && world.getBrightness(LightLayer.SKY, pos) == 0) {
            multiplier = 1f;
        } else {
            Identifier cropIdentifier = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            Season warmestSeason = GreenhouseCache.test(world, pos);
            multiplier = CropConfigs.getSeasonCropMultiplier(cropIdentifier, warmestSeason);
        }
        return multiplier;
    }
}
