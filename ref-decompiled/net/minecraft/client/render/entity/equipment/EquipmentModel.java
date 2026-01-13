/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.equipment.EquipmentModel
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$Builder
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$Layer
 *  net.minecraft.client.render.entity.equipment.EquipmentModel$LayerType
 *  net.minecraft.util.dynamic.Codecs
 */
package net.minecraft.client.render.entity.equipment;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public record EquipmentModel(Map<LayerType, List<Layer>> layers) {
    private final Map<LayerType, List<Layer>> layers;
    private static final Codec<List<Layer>> LAYER_LIST_CODEC = Codecs.nonEmptyList((Codec)Layer.CODEC.listOf());
    public static final Codec<EquipmentModel> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codecs.nonEmptyMap((Codec)Codec.unboundedMap((Codec)LayerType.CODEC, (Codec)LAYER_LIST_CODEC)).fieldOf("layers").forGetter(EquipmentModel::layers)).apply((Applicative)instance, EquipmentModel::new));

    public EquipmentModel(Map<LayerType, List<Layer>> layers) {
        this.layers = layers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Layer> getLayers(LayerType layerType) {
        return this.layers.getOrDefault(layerType, List.of());
    }

    public Map<LayerType, List<Layer>> layers() {
        return this.layers;
    }
}

