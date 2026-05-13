package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.sound.PlayerSoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerHurtSoundMixin {

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    private void deeperdark$replaceHurtSound(DamageSource source, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (PlayerSoundManager.playHurtSound(self)) {
            ci.cancel();
        }
    }
}
