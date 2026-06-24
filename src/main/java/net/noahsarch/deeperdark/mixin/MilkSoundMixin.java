package net.noahsarch.deeperdark.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.noahsarch.deeperdark.fluid.MilkFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Entity.class)
public abstract class MilkSoundMixin {

    @Shadow public abstract Level level();
    @Shadow public abstract BlockPos blockPosition();

    private boolean isInMilk() {
        FluidState fs = this.level().getFluidState(this.blockPosition());
        return fs.getType() instanceof MilkFluid;
    }

    @ModifyArg(
        method = "playSwimSound",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V"),
        index = 2
    )
    private float milkSwimSoundPitch(float pitch) {
        return isInMilk() ? 0.25F : pitch;
    }

    @ModifyArgs(
        method = "doWaterSplashEffect",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;playSound(Lnet/minecraft/sounds/SoundEvent;FF)V")
    )
    private void milkSplashSoundPitch(Args args) {
        if (isInMilk()) {
            args.set(2, 0.25F);
        }
    }
}
