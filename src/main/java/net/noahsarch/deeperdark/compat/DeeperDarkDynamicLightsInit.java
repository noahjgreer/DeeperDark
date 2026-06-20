package net.noahsarch.deeperdark.compat;

import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

/**
 * LambDynamicLights integration entrypoint for DeeperDark.
 * Registered under the "lambdynlights:initializer" entrypoint key in fabric.mod.json.
 * This class is only loaded when LambDynamicLights is present.
 */
public class DeeperDarkDynamicLightsInit implements DynamicLightsInitializer {

    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        // Ensure our custom luminance types are registered (triggers static initializers)
        @SuppressWarnings("unused")
        var type1 = CollarGlowBerriesLuminance.TYPE;
        @SuppressWarnings("unused")
        var type2 = CollarLavaBucketLuminance.TYPE;
        @SuppressWarnings("unused")
        var type3 = CollarLanternLuminance.TYPE;

        context.entityLightSourceManager().onRegisterEvent().register(
            Identifier.fromNamespaceAndPath("deeperdark", "collar_glow_berries"),
            ctx -> ctx.register(EntityType.PLAYER, CollarGlowBerriesLuminance.INSTANCE)
        );

        context.entityLightSourceManager().onRegisterEvent().register(
            Identifier.fromNamespaceAndPath("deeperdark", "collar_lava_bucket"),
            ctx -> ctx.register(EntityType.PLAYER, CollarLavaBucketLuminance.INSTANCE)
        );

        context.entityLightSourceManager().onRegisterEvent().register(
            Identifier.fromNamespaceAndPath("deeperdark", "collar_lantern"),
            ctx -> ctx.register(EntityType.PLAYER, CollarLanternLuminance.INSTANCE)
        );
    }
}
