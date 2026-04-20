package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.noahsarch.deeperdark.sound.ChatSoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityDeathSoundMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void deeperdark$playDeathSound(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        ChatSoundManager.playDeathMessageSound(self);
    }
}
