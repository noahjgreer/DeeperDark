package net.noahsarch.deeperdark.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// When A (passenger) sits on B's (vehicle's) head, A's AABB intersects B's upward ray-cast,
// causing left-click to target A instead of the block behind them. Making A non-pickable from
// B's own client lets the ray-cast pass through A and reach the block normally.
// Other clients are unaffected: the check only fires when the local player IS the vehicle.
@Mixin(Player.class)
public class PassengerPickableMixin {

    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    private void deeperdark$passengerNotPickableFromVehicle(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;
        if (!self.isPassenger()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player == self.getVehicle()) {
            cir.setReturnValue(false);
        }
    }
}
