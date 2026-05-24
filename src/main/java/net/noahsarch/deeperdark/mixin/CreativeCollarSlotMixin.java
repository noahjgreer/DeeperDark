package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.noahsarch.deeperdark.menu.CollarSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
@Environment(EnvType.CLIENT)
public class CreativeCollarSlotMixin {

    /**
     * After selectTab rebuilds the INVENTORY slot list, the CollarSlot wrapper ends up at
     * a wrong position because vanilla's formula treats it like a hotbar slot.
     * Patch its x/y to the coordinates defined in Collars.md (x: 127, y: 20).
     *
     * Avoids @Shadow for inherited fields (minecraft from Screen, menu from AbstractContainerScreen)
     * since Mixin cannot locate them without a refMap when they are not declared on the target class.
     */
    @Inject(method = "selectTab", at = @At("TAIL"))
    private void deeperdark$repositionCollarSlot(CreativeModeTab tab, CallbackInfo ci) {
        if (tab.getType() != CreativeModeTab.Type.INVENTORY) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        var invMenu = mc.player.inventoryMenu;
        AbstractContainerMenu creativeMenu = ((AbstractContainerScreen<?>) (Object) this).getMenu();
        if (creativeMenu == null) return;

        for (int i = 0; i < invMenu.slots.size(); i++) {
            if (invMenu.slots.get(i) instanceof CollarSlot && i < creativeMenu.slots.size()) {
                Slot wrapper = creativeMenu.slots.get(i);
                ((SlotPositionAccessor) wrapper).deeperdark$setX(127);
                ((SlotPositionAccessor) wrapper).deeperdark$setY(20);
                return;
            }
        }
    }
}
