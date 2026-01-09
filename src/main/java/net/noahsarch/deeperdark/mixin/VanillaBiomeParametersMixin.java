package net.noahsarch.deeperdark.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VanillaBiomeParameters.class)
public class VanillaBiomeParametersMixin {

    @Unique
    private static final RegistryKey<Biome> BLACK_MESA = RegistryKey.of(RegistryKeys.BIOME, Identifier.of("minecraft", "black_mesa"));

    @Inject(method = "getBadlandsBiome", at = @At("HEAD"), cancellable = true)
    private void deeperdark$replaceBadlandsWithBlackMesa(int humidity, MultiNoiseUtil.ParameterRange weirdness, CallbackInfoReturnable<RegistryKey<Biome>> cir) {
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
