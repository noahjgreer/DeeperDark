package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Right-clicking in air while holding a container item (main hand or offhand)
 * opens it directly, without needing to place it in the world first.
 *
 * Item.use() is called only when no block face is targeted, so this never
 * interferes with placing the block normally via right-click on a surface.
 */
@Mixin(Item.class)
public class ContainerItemUseInHandMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void deeperdark$openContainerOnUse(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        boolean isContainer = ContainerItemUtil.getContainerSize(stack) >= 0
                || stack.is(Items.ENDER_CHEST)
                || ContainerItemUtil.isVaultItem(stack);
        if (!isContainer) return;

        if (player instanceof ServerPlayer serverPlayer) {
            int slot = hand == InteractionHand.MAIN_HAND
                    ? serverPlayer.getInventory().getSelectedSlot()
                    : Inventory.SLOT_OFFHAND;
            Deeperdark.openContainerFromInventory(serverPlayer, slot, false);
        }
        // Consume on the client too, or it goes on to use the offhand item as well.
        cir.setReturnValue(InteractionResult.CONSUME);
    }
}
