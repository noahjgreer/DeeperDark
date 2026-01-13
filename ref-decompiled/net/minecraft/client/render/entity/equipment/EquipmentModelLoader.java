/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.equipment.EquipmentModel
 *  net.minecraft.client.render.entity.equipment.EquipmentModelLoader
 *  net.minecraft.item.equipment.EquipmentAsset
 *  net.minecraft.item.equipment.EquipmentAssetKeys
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.resource.JsonDataLoader
 *  net.minecraft.resource.ResourceFinder
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.profiler.Profiler
 */
package net.minecraft.client.render.entity.equipment;

import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class EquipmentModelLoader
extends JsonDataLoader<EquipmentModel> {
    public static final EquipmentModel EMPTY = new EquipmentModel(Map.of());
    private static final ResourceFinder FINDER = ResourceFinder.json((String)"equipment");
    private Map<RegistryKey<EquipmentAsset>, EquipmentModel> models = Map.of();

    public EquipmentModelLoader() {
        super(EquipmentModel.CODEC, FINDER);
    }

    protected void apply(Map<Identifier, EquipmentModel> map, ResourceManager resourceManager, Profiler profiler) {
        this.models = map.entrySet().stream().collect(Collectors.toUnmodifiableMap(entry -> RegistryKey.of((RegistryKey)EquipmentAssetKeys.REGISTRY_KEY, (Identifier)((Identifier)entry.getKey())), Map.Entry::getValue));
    }

    public EquipmentModel get(RegistryKey<EquipmentAsset> assetKey) {
        return this.models.getOrDefault(assetKey, EMPTY);
    }
}

