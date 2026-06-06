package net.noahsarch.deeperdark.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.client.ContainerItemKeyHandler;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
import net.noahsarch.deeperdark.inventory.ItemBackedContainer;
import net.noahsarch.deeperdark.payload.OpenContainerItemPayload;
import net.noahsarch.deeperdark.payload.OpenNestedContainerPayload;
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

    @Shadow
    public abstract void onClose();

    /**
     * When Escape or the inventory key is pressed while inside a container that was
     * opened from the player's inventory screen, close the container and go back to
     * the inventory screen instead of returning to the game world.
     * This only triggers when FROM_SCREEN_MARKER_KEY is present on the item —
     * containers opened via right-click in hand skip back to game normally.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$backToInventory(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (this.menu instanceof InventoryMenu) return;
        if (!deeperdark$isOpenedFromScreen()) return;

        Minecraft mc = Minecraft.getInstance();
        boolean isEscape = event.key() == 256; // GLFW_KEY_ESCAPE
        boolean isInventoryKey = mc.options.keyInventory.matches(event);
        if (!isEscape && !isInventoryKey) return;

        this.onClose();
        mc.setScreen(new InventoryScreen(mc.player));
        cir.setReturnValue(true);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$openContainerOnKey(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!ContainerItemKeyHandler.KEY.matches(event)) return;
        if (this.hoveredSlot == null) return;

        ItemStack stack = this.hoveredSlot.getItem();
        if (stack.isEmpty()) return;
        if (!deeperdark$isOpenableContainer(stack)) return;

        if (this.hoveredSlot.container instanceof Inventory) {
            // Normal path: container item in the player's own inventory.
            ClientPlayNetworking.send(new OpenContainerItemPayload(this.hoveredSlot.getContainerSlot()));
        } else {
            // Nested path: container item inside an already-open container (e.g. shulker in ender chest).
            ClientPlayNetworking.send(new OpenNestedContainerPayload(this.hoveredSlot.index));
        }
        cir.setReturnValue(true);
    }

    @Inject(method = "extractSlots", at = @At("RETURN"))
    private void deeperdark$renderInsertIndicators(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
        ItemStack cursor = this.menu.getCarried();
        if (cursor.isEmpty()) return;

        // Advance stratum so "+" fills render above 3D item icons in the current stratum.
        graphics.nextStratum();

        for (Slot slot : this.menu.slots) {
            if (!slot.isActive()) continue;
            if (!(slot.container instanceof Inventory)) continue;
            ItemStack slotItem = slot.getItem();
            if (slotItem.isEmpty()) continue;
            if (deeperdark$isContainerOpen(slotItem)) continue;

            boolean shouldShow;
            if (slotItem.is(ItemTags.SHULKER_BOXES)) {
                // Shulker boxes block only other shulker boxes per spec.
                if (cursor.is(ItemTags.SHULKER_BOXES)) continue;
                shouldShow = ContainerItemUtil.canInsert(slotItem, cursor, 27);

            } else if (slotItem.is(ModBlocks.FLIMSY_BOX.asItem())
                    || slotItem.is(ModBlocks.STURDY_BOX.asItem())
                    || slotItem.is(ModBlocks.REINFORCED_BOX.asItem())) {
                // Custom boxes block shulker boxes and all custom boxes.
                if (cursor.is(ItemTags.SHULKER_BOXES)) continue;
                if (ContainerItemUtil.getContainerSize(cursor) >= 0) continue;
                int size = ContainerItemUtil.getContainerSize(slotItem);
                shouldShow = ContainerItemUtil.canInsert(slotItem, cursor, size);

            } else if (slotItem.is(Items.ENDER_CHEST)) {
                // Ender chests: actual capacity can't be read client-side reliably.
                // Show "+" for any non-shulker, non-vault, non-ender-chest cursor.
                if (cursor.is(ItemTags.SHULKER_BOXES)) continue;
                if (ContainerItemUtil.isVaultItem(cursor)) continue;
                if (cursor.is(Items.ENDER_CHEST)) continue;
                shouldShow = true;

            } else if (ContainerItemUtil.isVaultItem(slotItem)) {
                // Vaults block shulker boxes and other vaults.
                if (cursor.is(ItemTags.SHULKER_BOXES)) continue;
                if (ContainerItemUtil.isVaultItem(cursor)) continue;
                shouldShow = ContainerItemUtil.canVaultInsert(slotItem, cursor);

            } else {
                continue; // Not a container item we recognise
            }

            if (!shouldShow) continue;

            // Draw a thin green "+" at the top-right corner of the slot.
            int px = slot.x + 9;
            int py = slot.y;
            int color = 0xFF_55FF55;
            graphics.fill(px,     py + 3, px + 7, py + 4, color); // horizontal bar
            graphics.fill(px + 3, py,     px + 4, py + 7, color); // vertical bar
        }
    }

    @Inject(method = "getTooltipFromContainerItem", at = @At("RETURN"), cancellable = true)
    private void deeperdark$appendOpenHint(ItemStack itemStack, CallbackInfoReturnable<List<Component>> cir) {
        if (this.hoveredSlot == null) return;
        if (!deeperdark$isOpenableContainer(itemStack)) return;
        if (deeperdark$isContainerOpen(itemStack)) return;

        List<Component> tooltip = new ArrayList<>(cir.getReturnValue());
        tooltip.add(Component.translatable("tooltip.deeperdark.open_container_hint",
            ContainerItemKeyHandler.KEY.getTranslatedKeyMessage())
            .withStyle(ChatFormatting.GRAY));
        cir.setReturnValue(tooltip);
    }

    /**
     * True if any player-inventory slot in the current menu holds an item stamped
     * with FROM_SCREEN_MARKER_KEY (opened from inventory) or NESTED_FROM_KEY (opened
     * as a nested container inside another container).
     */
    private boolean deeperdark$isOpenedFromScreen() {
        for (Slot slot : this.menu.slots) {
            if (!(slot.container instanceof Inventory)) continue;
            CustomData data = slot.getItem().get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
            if (data == null) continue;
            net.minecraft.nbt.CompoundTag tag = data.copyTag();
            if (tag.contains(ItemBackedContainer.FROM_SCREEN_MARKER_KEY)) return true;
            if (tag.contains(ItemBackedContainer.NESTED_FROM_KEY)) return true;
        }
        return false;
    }

    /** True if this item has an open-container UUID marker (any open method). */
    private static boolean deeperdark$isContainerOpen(ItemStack stack) {
        CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().contains(ItemBackedContainer.OPEN_MARKER_KEY);
    }

    private static boolean deeperdark$isOpenableContainer(ItemStack stack) {
        return stack.is(ItemTags.SHULKER_BOXES)
            || stack.is(Items.ENDER_CHEST)
            || stack.is(ModBlocks.FLIMSY_BOX.asItem())
            || stack.is(ModBlocks.STURDY_BOX.asItem())
            || stack.is(ModBlocks.REINFORCED_BOX.asItem())
            || ContainerItemUtil.isVaultItem(stack);
    }
}
