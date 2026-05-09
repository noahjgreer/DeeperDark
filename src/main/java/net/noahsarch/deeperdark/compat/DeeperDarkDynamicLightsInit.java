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
        // Ensure our custom luminance type is registered (triggers static initializer)
        @SuppressWarnings("unused")
        var type = CollarGlowBerriesLuminance.TYPE;

        context.entityLightSourceManager().onRegisterEvent().register(
            Identifier.fromNamespaceAndPath("deeperdark", "collar_glow_berries"),
            ctx -> ctx.register(EntityType.PLAYER, CollarGlowBerriesLuminance.INSTANCE)
        );
    }
}
