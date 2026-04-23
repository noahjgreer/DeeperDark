package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.sound.ChatSoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerManagerJoinSoundMixin {

    @Inject(method = "placeNewPlayer", at = @At("TAIL"))
    private void deeperdark$playJoinSound(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        ChatSoundManager.playJoinMessageSound(player);
    }
}
