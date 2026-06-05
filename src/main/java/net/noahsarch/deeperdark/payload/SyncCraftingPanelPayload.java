package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent client→server when the player toggles the collar crafting panel button.
 * The server updates InventoryMenu's deeperdark$panelOpen so that handlePlacement
 * and getInputGridSlots use the correct grid.
 */
public record SyncCraftingPanelPayload(boolean open) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncCraftingPanelPayload> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "sync_crafting_panel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncCraftingPanelPayload> CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncCraftingPanelPayload::open,
            SyncCraftingPanelPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
