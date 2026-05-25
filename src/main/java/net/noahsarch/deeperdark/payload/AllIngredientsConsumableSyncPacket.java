package net.noahsarch.deeperdark.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Sent server→client to sync the all-ingredients-consumable state.
 * Sent on player join and whenever an operator changes all_ingredients_consumable via /dd config.
 */
public record AllIngredientsConsumableSyncPacket(boolean enabled) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AllIngredientsConsumableSyncPacket> ID =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("deeperdark", "all_ingredients_consumable_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AllIngredientsConsumableSyncPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AllIngredientsConsumableSyncPacket::enabled,
            AllIngredientsConsumableSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
