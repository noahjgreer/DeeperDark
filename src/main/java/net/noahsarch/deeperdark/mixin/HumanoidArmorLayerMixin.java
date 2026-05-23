package net.noahsarch.deeperdark.mixin;

import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HumanoidArmorLayer.class)
public class HumanoidArmorLayerMixin {

    // The public shouldRender() gate in LivingEntityRenderer.extractRenderState() decides whether
    // to populate state.headItem. Saddles return false (equippable slot is BODY, not HEAD), so the
    // item gets rendered as a floating head-slot model via CustomHeadLayer. Return true here to
    // clear state.headItem instead, suppressing the floating-item render.
    @Inject(
        method = "shouldRender(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void deeperdark$saddleInHeadIsRendered(ItemStack itemStack, EquipmentSlot slot, CallbackInfoReturnable<Boolean> cir) {
        if (itemStack.is(Items.SADDLE) && slot == EquipmentSlot.HEAD) {
            cir.setReturnValue(true);
        }
    }
}
