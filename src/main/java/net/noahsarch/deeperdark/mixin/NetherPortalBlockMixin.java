package net.noahsarch.deeperdark.mixin;

import net.minecraft.block.NetherPortalBlock;
import net.minecraft.world.dimension.DimensionType;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Mixin to override nether portal coordinate scaling using the configurable multiplier.
 * This allows for custom coordinate ratios between overworld and nether to keep players within borders.
 */
@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    /**
     * Redirect the getCoordinateScaleFactor call to use our custom multiplier from config
     */
    @Redirect(
        method = "createTeleportTarget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/dimension/DimensionType;getCoordinateScaleFactor(Lnet/minecraft/world/dimension/DimensionType;Lnet/minecraft/world/dimension/DimensionType;)D"
        )
    )
    private double deeperdark$useCustomNetherMultiplier(DimensionType fromDimension, DimensionType toDimension) {
        DeeperDarkConfig.ConfigInstance config = DeeperDarkConfig.get();

        // Use the configured nether coordinate multiplier instead of vanilla calculation
        // The multiplier works as: overworld coords * multiplier = nether coords
        // So when going FROM nether TO overworld, we multiply by the multiplier
        // When going FROM overworld TO nether, we divide by the multiplier (multiply by 1/multiplier)

        double multiplier = config.netherCoordinateMultiplier;

        // Determine which direction we're traveling
        // In vanilla: nether has scale 8.0, overworld has scale 1.0
        // When going overworld -> nether: fromScale=1.0, toScale=8.0, result=1.0/8.0=0.125 (divide coords by 8)
        // When going nether -> overworld: fromScale=8.0, toScale=1.0, result=8.0/1.0=8.0 (multiply coords by 8)

        double fromScale = fromDimension.coordinateScale();
        double toScale = toDimension.coordinateScale();

        // If traveling from higher scale to lower scale (nether -> overworld in vanilla)
        if (fromScale > toScale) {
            // Multiply by our multiplier (going back to overworld)
            return multiplier;
        } else {
            // Divide by our multiplier (going to nether)
            return 1.0 / multiplier;
        }
    }
}

