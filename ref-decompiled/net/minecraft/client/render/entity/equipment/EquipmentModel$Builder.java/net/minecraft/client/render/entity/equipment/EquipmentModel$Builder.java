/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static class EquipmentModel.Builder {
    private final Map<EquipmentModel.LayerType, List<EquipmentModel.Layer>> layers = new EnumMap<EquipmentModel.LayerType, List<EquipmentModel.Layer>>(EquipmentModel.LayerType.class);

    EquipmentModel.Builder() {
    }

    public EquipmentModel.Builder addHumanoidLayers(Identifier textureId) {
        return this.addHumanoidLayers(textureId, false);
    }

    public EquipmentModel.Builder addHumanoidLayers(Identifier textureId, boolean dyeable) {
        this.addLayers(EquipmentModel.LayerType.HUMANOID_LEGGINGS, EquipmentModel.Layer.createWithLeatherColor(textureId, dyeable));
        this.addMainHumanoidLayer(textureId, dyeable);
        return this;
    }

    public EquipmentModel.Builder addMainHumanoidLayer(Identifier textureId, boolean dyeable) {
        return this.addLayers(EquipmentModel.LayerType.HUMANOID, EquipmentModel.Layer.createWithLeatherColor(textureId, dyeable));
    }

    public EquipmentModel.Builder addLayers(EquipmentModel.LayerType layerType, EquipmentModel.Layer ... layers) {
        Collections.addAll(this.layers.computeIfAbsent(layerType, type -> new ArrayList()), layers);
        return this;
    }

    public EquipmentModel build() {
        return new EquipmentModel((Map)this.layers.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> List.copyOf((Collection)entry.getValue()))));
    }
}
