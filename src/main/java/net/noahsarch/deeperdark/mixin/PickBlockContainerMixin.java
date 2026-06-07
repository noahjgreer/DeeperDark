package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.noahsarch.deeperdark.payload.PickFromContainerPayload;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts pick-block at the earliest possible point (Minecraft.pickBlockOrEntity)
 * so we fire before ItemSwapper's shulker-box handler, which cancels the vanilla flow
 * when the item isn't in the direct inventory. When the target item is found inside a
 * DD container (box, vault, bundle) we send PickFromContainerPayload and cancel so the
 * server handler can extract it and select the hotbar slot.
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public class PickBlockContainerMixin {

    @Shadow public LocalPlayer player;
    @Shadow public ClientLevel level;
    @Shadow public HitResult hitResult;

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger("deeperdark-pick");

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void deeperdark$pickFromContainers(CallbackInfo ci) {
        LOG.info("[DD-pick] pickBlockOrEntity fired, creative={}", player != null && player.getAbilities().instabuild);
        if (player == null || player.getAbilities().instabuild) return;
        if (!(hitResult instanceof BlockHitResult blockHit)) return;
        if (level == null) return;

        ClientLevel safeLevel = level;
        ItemStack target = safeLevel.getBlockState(blockHit.getBlockPos()).getCloneItemStack(safeLevel, blockHit.getBlockPos(), false);
        LOG.info("[DD-pick] target={} empty={}", target, target.isEmpty());
        if (target.isEmpty()) return;
        if (!target.isItemEnabled(safeLevel.enabledFeatures())) return;

        Inventory inventory = player.getInventory();
        int direct = inventory.findSlotMatchingItem(target);
        LOG.info("[DD-pick] direct slot={}", direct);
        if (direct != -1) return;

        if (!deeperdark$clientContainerHasItem(inventory, target)) {
            LOG.info("[DD-pick] not in any DD container, skipping");
            return;
        }

        LOG.info("[DD-pick] found in DD container, sending packet");
        ClientPlayNetworking.send(new PickFromContainerPayload(blockHit.getBlockPos()));
        ci.cancel();
    }

    private static boolean deeperdark$clientContainerHasItem(Inventory inventory, ItemStack target) {
        for (int i = 0; i < 36; i++) {
            ItemStack carrier = inventory.getItem(i);
            if (carrier.isEmpty()) continue;
            ItemContainerContents contents = carrier.get(DataComponents.CONTAINER);
            if (contents != null) {
                for (ItemStack stored : contents.allItemsCopyStream().toList()) {
                    if (ItemStack.isSameItem(stored, target)) return true;
                }
            }
            BundleContents bundle = carrier.get(DataComponents.BUNDLE_CONTENTS);
            if (bundle != null) {
                for (ItemStackTemplate tmpl : bundle.items()) {
                    if (ItemStack.isSameItem(tmpl.create(), target)) return true;
                }
            }
        }
        return false;
    }
}
