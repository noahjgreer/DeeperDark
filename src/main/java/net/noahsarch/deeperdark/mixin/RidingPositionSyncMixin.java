package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerGamePacketListenerImpl.class)
public class RidingPositionSyncMixin {

    @Shadow private ServerPlayer player;
    @Shadow private double firstGoodX;
    @Shadow private double firstGoodY;
    @Shadow private double firstGoodZ;

    @Inject(method = "tickPlayer", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/server/level/ServerPlayer;doTick()V",
        shift = At.Shift.AFTER
    ))
    private void deeperdark$preserveRidingPosition(CallbackInfoReturnable<Boolean> cir) {
        if (this.player.isPassenger()) {
            this.firstGoodX = this.player.getX();
            this.firstGoodY = this.player.getY();
            this.firstGoodZ = this.player.getZ();
        }
    }
}
