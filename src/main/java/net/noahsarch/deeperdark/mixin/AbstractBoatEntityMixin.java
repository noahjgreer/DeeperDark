package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoat.class)
public abstract class AbstractBoatEntityMixin extends Entity {

    @Shadow private AbstractBoat.Status location;

    @Unique
    private float deeperdark$speed = 1.0f;

    public AbstractBoatEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void deeperdark$tick(CallbackInfo ci) {
        // Allow stepping up 1 block
    }

    @Override
    public float getStepHeight() {
        return 1.2F;
    }

    @Inject(method = "getNearbySlipperiness", at = @At("HEAD"), cancellable = true)
    private void deeperdark$getNearbySlipperiness(CallbackInfoReturnable<Float> cir) {
        if (this.location == AbstractBoat.Status.IN_AIR) {
            // Return high slipperiness to maintain horizontal velocity
            cir.setReturnValue(0.98F);
        }
    }

    @Inject(method = "updateVelocity", at = @At("TAIL"))
    private void deeperdark$updateVelocity(CallbackInfo ci) {
        if (this.deeperdark$speed != 1.0f) {
             this.setVelocity(this.getVelocity().multiply(this.deeperdark$speed, 1.0, this.deeperdark$speed));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(ValueOutput view, CallbackInfo ci) {
        view.putFloat("DeeperDarkSpeed", this.deeperdark$speed);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ValueInput view, CallbackInfo ci) {
        this.deeperdark$speed = view.getFloatOr("DeeperDarkSpeed", 1.0f);
    }
}

