package net.noahsarch.deeperdark.mixin;

import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public class BundleContentsComponentMixin {

    @Inject(method = "getOccupancy(Lnet/minecraft/item/ItemStack;)Lorg/apache/commons/lang3/math/Fraction;", at = @At("HEAD"), cancellable = true)
    private static void getOccupancy(ItemStack stack, CallbackInfoReturnable<Fraction> cir) {
        if (stack.isEmpty()) {
             cir.setReturnValue(Fraction.ZERO);
        } else {
             // Allow 256 items in the bundle.
             // Each item takes 1/256 of the space.
             cir.setReturnValue(Fraction.getFraction(stack.getCount(), 256));
        }
    }
}

