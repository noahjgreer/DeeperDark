package net.noahsarch.deeperdark.mixin;

import com.mojang.blaze3d.platform.InputConstants;
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
import net.noahsarch.deeperdark.DeeperDarkClient;
import net.noahsarch.deeperdark.block.ModBlocks;
import net.noahsarch.deeperdark.client.ContainerItemKeyHandler;
import net.noahsarch.deeperdark.inventory.ContainerItemUtil;
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
     * This only triggers for containers the server reports as opened from the screen —
     * containers opened via right-click in hand skip back to game normally.
     */
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void deeperdark$backToInventory(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (this.menu instanceof InventoryMenu) return;
        if (!deeperdark$isOpenedFromScreen()) return;

        Minecraft mc = Minecraft.getInstance();
        boolean isEscape = event.key() == InputConstants.KEY_ESCAPE;
        boolean isInventoryKey = mc.options.keyInventory.matches(event);
        if (!isEscape && !isInventoryKey) return;

        DeeperDarkClient.fromScreenContainerId = -1;
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
        if (cursor.isEmpty() || ContainerItemUtil.isOpen(cursor)) return;

        // Advance stratum so "+" fills render above 3D item icons in the current stratum.
        graphics.nextStratum();

        for (Slot slot : this.menu.slots) {
            if (!slot.isActive()) continue;
            if (!(slot.container instanceof Inventory)) continue;
            ItemStack slotItem = slot.getItem();
            if (slotItem.isEmpty()) continue;
            if (ContainerItemUtil.isOpen(slotItem)) continue;

            boolean shouldShow;
            if (slotItem.is(Items.ENDER_CHEST)) {
                // Actual ender capacity can't be read client-side; ender-on-ender stacks instead.
                shouldShow = !cursor.is(Items.ENDER_CHEST);
            } else if (ContainerItemUtil.isVaultItem(slotItem)) {
                shouldShow = ContainerItemUtil.canStore(ContainerItemUtil.VAULT_TIER, cursor)
                        && ContainerItemUtil.canVaultInsert(slotItem, cursor);
            } else if (ContainerItemUtil.tier(slotItem) > 0) {
                shouldShow = ContainerItemUtil.canStore(ContainerItemUtil.tier(slotItem), cursor)
                        && ContainerItemUtil.canInsert(slotItem, cursor, ContainerItemUtil.getContainerSize(slotItem));
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
        if (ContainerItemUtil.isOpen(itemStack)) return;

        List<Component> tooltip = new ArrayList<>(cir.getReturnValue());
        tooltip.add(Component.translatable("tooltip.deeperdark.open_container_hint",
            ContainerItemKeyHandler.KEY.getTranslatedKeyMessage())
            .withStyle(ChatFormatting.GRAY));
        cir.setReturnValue(tooltip);
    }

    /**
     * True only when this container was opened from the inventory screen via the
     * Alt key quick-open feature. The server sends OpenFromScreenPayload with the
     * containerId whenever such an open occurs; we simply compare that stored ID
     * to this menu's containerId. This is immune to stale NBT markers and nested
     * container chains (a child has a different containerId than the parent).
     */
    private boolean deeperdark$isOpenedFromScreen() {
        return this.menu.containerId == DeeperDarkClient.fromScreenContainerId;
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
