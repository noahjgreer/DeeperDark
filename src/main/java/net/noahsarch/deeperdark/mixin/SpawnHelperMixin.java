package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.noahsarch.deeperdark.event.WorldBorderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public class SpawnHelperMixin {
    @Inject(method = "isValidSpawnPostitionForType", at = @At("HEAD"), cancellable = true)
    private static void checkWorldBorder(ServerLevel world, MobCategory group, StructureManager structureAccessor, ChunkGenerator chunkGenerator, MobSpawnSettings.SpawnerData spawnEntry, BlockPos.MutableBlockPos pos, double squaredDistance, CallbackInfoReturnable<Boolean> cir) {
        if (!WorldBorderHandler.isSafe(pos.getX(), pos.getZ())) {
            cir.setReturnValue(false);
        }
    }
}

