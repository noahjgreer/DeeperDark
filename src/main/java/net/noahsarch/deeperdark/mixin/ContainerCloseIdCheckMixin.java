package net.noahsarch.deeperdark.mixin;

import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.noahsarch.deeperdark.Deeperdark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Two responsibilities:
 *
 * HEAD — filters stale echo close packets.
 * When the server transitions menus (parent → child), it calls closeContainer() which sends
 * ClientboundContainerClosePacket to the client.  The client must NOT echo back a
 * ServerboundContainerClosePacket for that server-initiated close, but some code paths
 * (e.g. AbstractContainerScreen.onClose being called by setScreen) do send one.  By the
 * time that echo arrives, containerMenu is already the new child menu.  Dropping any packet
 * whose containerId does not match the current menu eliminates the spurious close.
 *
 * TAIL — synchronous parent reopen after a nested child closes.
 * ItemBackedContainer / ItemBackedVaultEntity stopOpen() puts the parent slot into
 * PENDING_PARENT_REOPENS instead of deferring via server.execute().  We drain that entry
 * here, after doCloseContainer() has already set containerMenu = inventoryMenu, so the
 * reopen is fully synchronous — the client receives ClientboundOpenScreenPacket for the
 * parent in the same network flush, eliminating the one-tick flash to the game world.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public class ContainerCloseIdCheckMixin {

    @Shadow public ServerPlayer player;

    @Inject(method = "handleContainerClose", at = @At("HEAD"), cancellable = true)
    private void deeperdark$checkContainerId(ServerboundContainerClosePacket packet, CallbackInfo ci) {
        if (packet.getContainerId() != player.containerMenu.containerId) {
            ci.cancel();
        }
    }

    @Inject(method = "handleContainerClose", at = @At("TAIL"))
    private void deeperdark$reopenParent(ServerboundContainerClosePacket packet, CallbackInfo ci) {
        Integer parentSlot = Deeperdark.PENDING_PARENT_REOPENS.remove(player.getUUID());
        if (parentSlot != null && !player.isRemoved()) {
            Deeperdark.openContainerFromInventory(player, parentSlot, true);
        }
    }
}
