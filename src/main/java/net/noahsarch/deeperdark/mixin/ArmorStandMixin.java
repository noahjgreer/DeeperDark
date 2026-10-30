package net.noahsarch.deeperdark.mixin;

import net.noahsarch.deeperdark.DeeperDarkConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStand.class)
public class ArmorStandMixin {

    @Shadow private boolean swapItem(Player player, EquipmentSlot slot, ItemStack playerItemStack, InteractionHand hand) {
        throw new AssertionError();
    }

    // Save the stand's own flag; otherwise every stand saved with the setting on keeps its arms forever
    @Redirect(method = "addAdditionalSaveData", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/decoration/ArmorStand;showArms()Z"))
    private boolean deeperdark$saveOwnShowArms(ArmorStand stand) {
        return (stand.getEntityData().get(ArmorStand.DATA_CLIENT_FLAGS) & ArmorStand.CLIENT_FLAG_SHOW_ARMS) != 0;
    }

    @Inject(method = "showArms", at = @At("HEAD"), cancellable = true)
    private void deeperdark$alwaysShowArms(CallbackInfoReturnable<Boolean> cir) {
        if (DeeperDarkConfig.get().armorStandArms) cir.setReturnValue(true);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void deeperdark$shiftEquipOffhand(Player player, InteractionHand hand, Vec3 location, CallbackInfoReturnable<InteractionResult> cir) {
        if (!player.isShiftKeyDown() || !DeeperDarkConfig.get().armorStandOffhand) return;
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
