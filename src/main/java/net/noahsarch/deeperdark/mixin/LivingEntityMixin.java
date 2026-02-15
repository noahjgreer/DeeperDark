package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "getSoundPitch", at = @At("RETURN"), cancellable = true)
    private void deeperdark$getSoundPitch(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // Baby skeleton sounds
        if (self instanceof SkeletonEntity && self instanceof BabySkeletonAccessor accessor) {
            if (accessor.deeperdark$isBaby()) {
                // Baby pitch logic: (random.nextFloat() - random.nextFloat()) * 0.2F + 1.5F
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }

        // Baby creeper sounds
        if (self instanceof CreeperEntity && self instanceof BabyCreeperAccessor accessor) {
            if (accessor.deeperdark$isBabyCreeper()) {
                // Baby pitch logic: (random.nextFloat() - random.nextFloat()) * 0.2F + 1.5F
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }
    }
}
