package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    /**
     * Makes Depth Strider enchantment work in lava the same way it works in water.
     * Injects into the travelInFluid method to boost movement when in lava.
     */
    @Inject(method = "travelInFluid", at = @At("RETURN"))
    private void applyDepthStriderToLava(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Only apply if the entity is in lava (not water)
        if (self.isInLava() && !self.isTouchingWater()) {
            // Get the water movement efficiency (modified by Depth Strider enchantment)
            double waterEfficiency = self.getAttributeValue(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);

            // If the entity has Depth Strider (waterEfficiency > 0)
            if (waterEfficiency > 0.0) {
                Vec3d velocity = self.getVelocity();

                // Apply the same speed boost to lava movement as water gets
                // The boost factor matches how Depth Strider affects water movement
                double boostFactor = 1.0 + (waterEfficiency * 0.73);

                // Boost horizontal movement (x and z), keep vertical (y) unchanged
                self.setVelocity(
                    velocity.x * boostFactor,
                    velocity.y,
                    velocity.z * boostFactor
                );
            }
        }
    }
}

