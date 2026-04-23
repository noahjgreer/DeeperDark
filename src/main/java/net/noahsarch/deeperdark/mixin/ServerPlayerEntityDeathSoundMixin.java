package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.sound.ChatSoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityDeathSoundMixin {

    @Inject(method = "die", at = @At("HEAD"))
    private void deeperdark$playDeathSound(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        ChatSoundManager.playDeathMessageSound(self);
    }
}
