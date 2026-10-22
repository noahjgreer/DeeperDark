package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
//? if >=26.3 {
/*import net.minecraft.world.level.biome.BiomeResolver;
*///?} else {
import net.minecraft.world.level.biome.Climate;
//?}
import net.noahsarch.deeperdark.DeepDarkBiomeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.stream.Stream;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {

    @Shadow protected abstract Stream<Holder<Biome>> collectPossibleBiomes();

    //? if >=26.3 {
    /*// 26.3 resolves biomes through BiomeResolver lambdas, so wrap the resolvers instead
    @Inject(method = {"createResolver", "createResolverForChunk"}, at = @At("RETURN"), cancellable = true)
    private void injectDeepDarkBiome(CallbackInfoReturnable<BiomeResolver> cir) {
        BiomeResolver original = cir.getReturnValue();
        cir.setReturnValue((x, y, z) -> deeperdark$deepDarkBelow(original.getNoiseBiome(x, y, z), y));
    }
    *///?} else {
    @Inject(method = "getNoiseBiome", at = @At("RETURN"), cancellable = true)
    private void injectDeepDarkBiome(int biomeX, int biomeY, int biomeZ, Climate.Sampler noiseSampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        cir.setReturnValue(deeperdark$deepDarkBelow(cir.getReturnValue(), biomeY));
    }
    //?}

    // Looked up once; worldgen threads may race to set it, but they all find the same holder
    @Unique
    private Holder<Biome> deeperdark$deepDark;

    @Unique
    private Holder<Biome> deeperdark$deepDarkBelow(Holder<Biome> biome, int quartY) {
        if (QuartPos.toBlock(quartY) >= -54 || (biome != null && biome.is(DeepDarkBiomeModifier.DEEP_DARK))) {
            return biome;
        }
        if (this.deeperdark$deepDark == null) {
            this.deeperdark$deepDark = this.collectPossibleBiomes().filter(entry -> entry.is(DeepDarkBiomeModifier.DEEP_DARK)).findFirst().orElse(null);
        }
        return this.deeperdark$deepDark != null ? this.deeperdark$deepDark : biome;
    }
}
