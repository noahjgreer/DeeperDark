package net.noahsarch.deeperdark.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.noahsarch.deeperdark.worldgen.ModWorldgen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MultiNoiseBiomeSourceParameterLists.class)
public class MultiNoiseBiomeSourceParameterListsMixin {

    @WrapMethod(method = "bootstrap")
    private static void deeperdark$flagVanillaBootstrap(BootstrapContext<MultiNoiseBiomeSourceParameterList> context, Operation<Void> original) {
        ModWorldgen.BOOTSTRAPPING_VANILLA_REGISTRIES.set(true);
        try {
            original.call(context);
        } finally {
            ModWorldgen.BOOTSTRAPPING_VANILLA_REGISTRIES.set(false);
        }
    }
}
