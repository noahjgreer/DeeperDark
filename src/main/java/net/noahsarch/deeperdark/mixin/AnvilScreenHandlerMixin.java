package net.noahsarch.deeperdark.mixin;

import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow @Final private Property levelCost;

    public AnvilScreenHandlerMixin(net.minecraft.screen.ScreenHandlerType<?> type, int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, net.minecraft.screen.ScreenHandlerContext context) {
        // Pass null for the ForgingSlotsManager as this constructor is only for compilation
        super(type, syncId, playerInventory, context, (ForgingSlotsManager)null);
    }

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/Property;set(I)V", shift = At.Shift.AFTER))
    private void capLevelCost(CallbackInfo ci) {
        if (this.levelCost.get() >= 40) {
            this.levelCost.set(39);
        }
    }
}

