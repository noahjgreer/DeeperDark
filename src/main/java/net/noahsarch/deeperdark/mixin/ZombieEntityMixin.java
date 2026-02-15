package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.noahsarch.deeperdark.DeeperDarkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to make zombie follow range configurable.
 * Vanilla default is 35.0 blocks.
 */
@Mixin(ZombieEntity.class)
public class ZombieEntityMixin {

    /**
     * Modify the zombie attributes builder to use config value for FOLLOW_RANGE
     */
    @Inject(method = "createZombieAttributes", at = @At("RETURN"), cancellable = true)
    private static void deeperdark$modifyZombieAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        DefaultAttributeContainer.Builder builder = cir.getReturnValue();
        // Override the FOLLOW_RANGE with config value
        double followRange = DeeperDarkConfig.get().zombieFollowRange;
        builder.add(EntityAttributes.FOLLOW_RANGE, followRange);
        cir.setReturnValue(builder);
    }
}
