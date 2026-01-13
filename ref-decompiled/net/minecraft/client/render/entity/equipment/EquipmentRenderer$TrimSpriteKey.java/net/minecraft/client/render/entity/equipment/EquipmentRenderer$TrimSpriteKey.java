/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
record EquipmentRenderer.TrimSpriteKey(ArmorTrim trim, EquipmentModel.LayerType layerType, RegistryKey<EquipmentAsset> equipmentAssetId) {
    public Identifier getTexture() {
        return this.trim.getTextureId(this.layerType.getTrimsDirectory(), this.equipmentAssetId);
    }
}
