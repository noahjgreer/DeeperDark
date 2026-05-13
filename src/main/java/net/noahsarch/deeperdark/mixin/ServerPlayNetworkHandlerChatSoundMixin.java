package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.sound.PlayerSoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerChatSoundMixin {

    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleChat", at = @At("TAIL"))
    private void deeperdark$playChatSound(ServerboundChatPacket packet, CallbackInfo ci) {
        if (this.player == null) {
            return;
        }

        var server = this.player.level().getServer();
        if (server == null) {
            return;
        }

        server.execute(() -> PlayerSoundManager.playSendMessageSound(this.player));
    }
}
