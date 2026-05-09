package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.noahsarch.deeperdark.util.BabyCreeperAccessor;
import net.noahsarch.deeperdark.util.BabySkeletonAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "travelInFluid", at = @At("RETURN"))
    private void applyDepthStriderToLava(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.isInLava() && !self.isInWater()) {
            double waterEfficiency = self.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);

            if (waterEfficiency > 0.0) {
                Vec3 velocity = self.getDeltaMovement();

                double boostFactor = 1.0 + (waterEfficiency * 0.73);

                self.setDeltaMovement(
                    velocity.x * boostFactor,
                    velocity.y,
                    velocity.z * boostFactor
                );
            }
        }
    }

@Inject(method = "getVoicePitch", at = @At("RETURN"), cancellable = true)
    private void deeperdark$getVoicePitch(CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self instanceof Skeleton && self instanceof BabySkeletonAccessor accessor) {
            if (accessor.deeperdark$isBaby()) {
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }

        if (self instanceof Creeper && self instanceof BabyCreeperAccessor accessor) {
            if (accessor.deeperdark$isBabyCreeper()) {
                cir.setReturnValue((self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F + 1.5F);
            }
        }
    }
}
