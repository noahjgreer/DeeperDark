package net.noahsarch.deeperdark.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.Deeperdark;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Server-side pick-block extension. Fires inside tryPickItem before vanilla's
 * findSlotMatchingItem check. When the item is not in the main inventory, we
 * search all container/bundle items and extract it. Vanilla then finds the item
 * in the main inventory and handles hotbar selection naturally.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PickBlockContainerServerMixin {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("deeperdark-pick-server");

    @Shadow public ServerPlayer player;

    @Inject(method = "tryPickItem", at = @At("HEAD"))
    private void deeperdark$searchContainersForPick(ItemStack target, CallbackInfo ci) {
        LOG.info("[DD-pick-srv] tryPickItem fired target={}", target);
        if (player.hasInfiniteMaterials()) { LOG.info("[DD-pick-srv] skip: creative"); return; }
        Inventory inventory = player.getInventory();
        int direct = inventory.findSlotMatchingItem(target);
        LOG.info("[DD-pick-srv] direct slot={}", direct);
        if (direct != -1) return;
        boolean extracted = Deeperdark.searchAndExtractForPickBlock(inventory, target);
        LOG.info("[DD-pick-srv] extracted={}", extracted);
    }
}
