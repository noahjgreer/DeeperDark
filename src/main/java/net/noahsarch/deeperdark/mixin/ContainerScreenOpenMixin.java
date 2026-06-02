package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.client.ContainerItemKeyHandler;
import net.noahsarch.deeperdark.payload.OpenContainerItemPayload;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenOpenMixin<T extends AbstractContainerMenu> {

    @Shadow
    protected @Nullable Slot hoveredSlot;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$openContainerOnKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!ContainerItemKeyHandler.KEY.matches(event)) return;
        if (this.hoveredSlot == null) return;

        ItemStack stack = this.hoveredSlot.getItem();
        if (stack.isEmpty()) return;
        if (!(this.hoveredSlot.container instanceof Inventory)) return;
        if (!deeperdark$isOpenableContainer(stack)) return;

        ClientPlayNetworking.send(new OpenContainerItemPayload(this.hoveredSlot.getContainerSlot()));
        cir.setReturnValue(true);
    }

    private static boolean deeperdark$isOpenableContainer(ItemStack stack) {
        return stack.is(ItemTags.SHULKER_BOXES)
            || stack.is(ModBlocks.FLIMSY_BOX.asItem())
            || stack.is(ModBlocks.STURDY_BOX.asItem())
            || stack.is(ModBlocks.REINFORCED_BOX.asItem());
    }
}
