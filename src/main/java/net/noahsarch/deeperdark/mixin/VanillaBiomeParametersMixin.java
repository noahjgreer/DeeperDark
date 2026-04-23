package net.noahsarch.deeperdark.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OverworldBiomeBuilder.class)
public class VanillaBiomeParametersMixin {

    @Unique
    private static final ResourceKey<Biome> BLACK_MESA = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("minecraft", "black_mesa"));

    @Inject(method = "pickBadlandsBiome", at = @At("HEAD"), cancellable = true)
    private void deeperdark$replaceBadlandsWithBlackMesa(int humidity, Climate.Parameter weirdness, CallbackInfoReturnable<ResourceKey<Biome>> cir) {
        if (humidity == 2) {
            // Make black_mesa much rarer: deterministic selection based on the weirdness range.
            // This keeps worldgen deterministic while reducing the overall size of black_mesa regions.
            long seed = weirdness.min() * 31L + weirdness.max() * 13L + humidity;
            long pick = Math.abs(seed % 5L); // ~1 in 5 chance
            if (pick == 0L) {
                cir.setReturnValue(BLACK_MESA);
            }
        }
    }
}
