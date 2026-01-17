package net.noahsarch.deeperdark.mixin;

import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(net.minecraft.screen.ScreenHandlerType<?> type, int syncId, net.minecraft.entity.player.PlayerInventory playerInventory, net.minecraft.screen.ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }
}
