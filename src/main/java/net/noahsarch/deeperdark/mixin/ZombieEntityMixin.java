package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make zombie follow range configurable.
 * Vanilla default is 35.0 blocks.
 */
@Mixin(Zombie.class)
public class ZombieEntityMixin {

    /**
     * Modify the zombie attributes builder to use config value for FOLLOW_RANGE
     */
    @Inject(method = "createZombieAttributes", at = @At("RETURN"), cancellable = true)
    private static void deeperdark$modifyZombieAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();
        // Override the FOLLOW_RANGE with config value
        double followRange = DeeperDarkConfig.get().zombieFollowRange;
        builder.add(Attributes.FOLLOW_RANGE, followRange);
        cir.setReturnValue(builder);
    }
}
