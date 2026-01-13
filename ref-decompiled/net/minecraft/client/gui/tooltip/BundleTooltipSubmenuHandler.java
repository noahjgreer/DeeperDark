/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.tooltip.BundleTooltipSubmenuHandler
 *  net.minecraft.client.gui.tooltip.TooltipSubmenuHandler
 *  net.minecraft.client.input.Scroller
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.item.BundleItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  org.joml.Vector2i
 */
package net.minecraft.client.gui.tooltip;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipSubmenuHandler;
import net.minecraft.client.input.Scroller;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BundleItemSelectedC2SPacket;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.joml.Vector2i;

@Environment(value=EnvType.CLIENT)
public class BundleTooltipSubmenuHandler
implements TooltipSubmenuHandler {
    private final MinecraftClient client;
    private final Scroller scroller;

    public BundleTooltipSubmenuHandler(MinecraftClient client) {
        this.client = client;
        this.scroller = new Scroller();
    }

    public boolean isApplicableTo(Slot slot) {
        return slot.getStack().isIn(ItemTags.BUNDLES);
    }

    public boolean onScroll(double horizontal, double vertical, int slotId, ItemStack item) {
        int l;
        int k;
        int j;
        int i = BundleItem.getNumberOfStacksShown((ItemStack)item);
        if (i == 0) {
            return false;
        }
        Vector2i vector2i = this.scroller.update(horizontal, vertical);
        int n = j = vector2i.y == 0 ? -vector2i.x : vector2i.y;
        if (j != 0 && (k = BundleItem.getSelectedStackIndex((ItemStack)item)) != (l = Scroller.scrollCycling((double)j, (int)k, (int)i))) {
            this.sendPacket(item, slotId, l);
        }
        return true;
    }

    public void reset(Slot slot) {
        this.reset(slot.getStack(), slot.id);
    }

    public void onMouseClick(Slot slot, SlotActionType actionType) {
        if (actionType == SlotActionType.QUICK_MOVE || actionType == SlotActionType.SWAP) {
            this.reset(slot.getStack(), slot.id);
        }
    }

    private void sendPacket(ItemStack item, int slotId, int selectedItemIndex) {
        if (this.client.getNetworkHandler() != null && selectedItemIndex < BundleItem.getNumberOfStacksShown((ItemStack)item)) {
            ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
            BundleItem.setSelectedStackIndex((ItemStack)item, (int)selectedItemIndex);
            clientPlayNetworkHandler.sendPacket((Packet)new BundleItemSelectedC2SPacket(slotId, selectedItemIndex));
        }
    }

    public void reset(ItemStack item, int slotId) {
        this.sendPacket(item, slotId, -1);
    }
}

