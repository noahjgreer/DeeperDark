package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {

    @Shadow private boolean swapItem(Player player, EquipmentSlot slot, ItemStack playerItemStack, InteractionHand hand) {
        throw new AssertionError();
    }

    @Overwrite
    public boolean showArms() {
        return true;
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void deeperdark$shiftEquipOffhand(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isShiftKeyDown()) return;
        if (player.isSpectator()) return;
        if (player.level().isClientSide()) {
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
            return;
        }

        ArmorStand stand = (ArmorStand)(Object)this;
        if (stand.isMarker()) return;

        ItemStack heldItem = player.getItemInHand(hand);
        if (heldItem.isEmpty()) return;

        if (swapItem(player, EquipmentSlot.OFFHAND, heldItem, hand)) {
            cir.setReturnValue(InteractionResult.SUCCESS_SERVER);
        }
    }
}
