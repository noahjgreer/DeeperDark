package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBoatEntity.class)
public abstract class AbstractBoatEntityMixin extends Entity {

    @Shadow private AbstractBoatEntity.Location location;

    @Unique
    private float deeperdark$speed = 1.0f;

    public AbstractBoatEntityMixin(EntityType<?> type, World world) {
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
        if (this.location == AbstractBoatEntity.Location.IN_AIR) {
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

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void deeperdark$writeCustomData(WriteView view, CallbackInfo ci) {
        view.putFloat("DeeperDarkSpeed", this.deeperdark$speed);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void deeperdark$readCustomData(ReadView view, CallbackInfo ci) {
        this.deeperdark$speed = view.getFloat("DeeperDarkSpeed", 1.0f);
    }
}

