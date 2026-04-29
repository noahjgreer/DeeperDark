package io.github.lucaargolo.seasons.payload;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.CropConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;

public record UpdateCropsPaycket(CropConfig cropConfig, HashMap<Identifier, CropConfig> cropConfigMap) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateCropsPaycket> ID = new CustomPacketPayload.Type<>(FabricSeasons.identifier("update_crops"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateCropsPaycket> CODEC = StreamCodec.composite(
        CropConfig.STREAM_CODEC.cast(), UpdateCropsPaycket::cropConfig,
        ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, CropConfig.STREAM_CODEC.cast()), UpdateCropsPaycket::cropConfigMap,
        UpdateCropsPaycket::new
    );

    public static UpdateCropsPaycket fromConfig(CropConfig config, HashMap<Identifier, CropConfig> configMap) {
        return new UpdateCropsPaycket(config, configMap);
    }

    @Override
    public CustomPacketPayload.Type<UpdateCropsPaycket> type() {
        return ID;
    }
}
