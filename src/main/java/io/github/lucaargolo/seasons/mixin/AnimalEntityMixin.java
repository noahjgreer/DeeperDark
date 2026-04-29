package io.github.lucaargolo.seasons.mixin;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public class AnimalEntityMixin {

    @Inject(at = @At("HEAD"), method = "spawnChildFromBreeding", cancellable = true)
    public void breedInject(ServerLevel serverLevel, Animal animal, CallbackInfo info) {
        if (FabricSeasons.getCurrentSeason(serverLevel) == Season.WINTER && !FabricSeasons.CONFIG.doAnimalsBreedInWinter()) {
            info.cancel();
        }
    }
}
