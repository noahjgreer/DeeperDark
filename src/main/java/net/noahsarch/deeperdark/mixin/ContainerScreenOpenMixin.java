package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.client.ContainerItemKeyHandler;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;
import net.noahsarch.deeperdark.payload.OpenContainerItemPayload;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class ContainerScreenOpenMixin<T extends AbstractContainerMenu> {

    @Shadow
    protected @Nullable Slot hoveredSlot;

    @Shadow
    protected T menu;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$openContainerOnKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!ContainerItemKeyHandler.KEY.matches(event)) return;
        if (this.hoveredSlot == null) return;

        ItemStack stack = this.hoveredSlot.getItem();
        if (stack.isEmpty()) return;
        if (!(this.hoveredSlot.container instanceof Inventory)) return;
        if (!deeperdark$isOpenableContainer(stack)) return;
        // No client-side "already open" guard here — the server guard handles that correctly,
        // and a client-side check using the CUSTOM_DATA marker would block re-opening after
        // the item was dropped (the stale marker can't be cleaned up server-side in that case).

        ClientPlayNetworking.send(new OpenContainerItemPayload(this.hoveredSlot.getContainerSlot()));
        cir.setReturnValue(true);
    }

    @Inject(method = "extractSlots", at = @At("RETURN"))
    private void deeperdark$renderInsertIndicators(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        ItemStack cursor = this.menu.getCarried();
        if (cursor.isEmpty()) return;

        // Advance to the next render stratum so the "+" fills are drawn above the 3D item
        // icons (which live in a separate addItem() queue on the current stratum).
        graphics.nextStratum();

        for (Slot slot : this.menu.slots) {
            if (!slot.isActive()) continue;
            if (!(slot.container instanceof Inventory)) continue;
            ItemStack slotItem = slot.getItem();
            if (slotItem.isEmpty()) continue;
            // Skip containers that are currently open — inserting into them would duplicate items.
            if (deeperdark$isContainerOpen(slotItem)) continue;

            int size = ContainerItemUtil.getContainerSize(slotItem);
            if (size < 0) continue;
            if (!ContainerItemUtil.canInsert(slotItem, cursor, size)) continue;
            // Also skip if the cursor item itself is a container (shulker/box) — nesting is blocked.
            if (ContainerItemUtil.getContainerSize(cursor) >= 0) continue;

            // Draw a thin green "+" at the top-right corner of the slot.
            int px = slot.x + 9;
            int py = slot.y;
            int color = 0xFF_55FF55;
            graphics.fill(px,     py + 3, px + 7, py + 4, color); // horizontal bar (1px)
            graphics.fill(px + 3, py,     px + 4, py + 7, color); // vertical bar (1px)
        }
    }

    @Inject(method = "getTooltipFromContainerItem", at = @At("RETURN"), cancellable = true)
    private void deeperdark$appendOpenHint(ItemStack itemStack, CallbackInfoReturnable<List<Component>> cir) {
        if (this.hoveredSlot == null || !(this.hoveredSlot.container instanceof Inventory)) return;
        if (!deeperdark$isOpenableContainer(itemStack)) return;
        // Suppress the hint while this container is already open from inventory.
        if (deeperdark$isContainerOpen(itemStack)) return;

        List<Component> tooltip = new ArrayList<>(cir.getReturnValue());
        tooltip.add(Component.translatable("tooltip.deeperdark.open_container_hint",
            ContainerItemKeyHandler.KEY.getTranslatedKeyMessage())
            .withStyle(ChatFormatting.GRAY));
        cir.setReturnValue(tooltip);
    }

    /** Returns true if this item has an open-container marker written by ItemBackedContainer. */
    private static boolean deeperdark$isContainerOpen(ItemStack stack) {
        CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().contains(ItemBackedContainer.OPEN_MARKER_KEY);
    }

    private static boolean deeperdark$isOpenableContainer(ItemStack stack) {
        return stack.is(ItemTags.SHULKER_BOXES)
            || stack.is(ModBlocks.FLIMSY_BOX.asItem())
            || stack.is(ModBlocks.STURDY_BOX.asItem())
            || stack.is(ModBlocks.REINFORCED_BOX.asItem());
    }
}
