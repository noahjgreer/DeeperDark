package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.fluid.MilkFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MilkMovementMixin {

    @Inject(method = "travelInWater", at = @At("RETURN"))
    private void applyMilkViscosity(Vec3 input, double baseGravity, boolean isFalling, double oldY, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        FluidState fs = self.level().getFluidState(self.blockPosition());
        if (fs.getType() instanceof MilkFluid) {
            Vec3 v = self.getDeltaMovement();
            self.setDeltaMovement(new Vec3(v.x * 0.9, v.y, v.z * 0.9));
        }
    }
}
