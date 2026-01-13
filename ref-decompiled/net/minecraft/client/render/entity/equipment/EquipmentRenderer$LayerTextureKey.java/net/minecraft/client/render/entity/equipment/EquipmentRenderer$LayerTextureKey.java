/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.equipment;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.equipment.EquipmentModel;

@Environment(value=EnvType.CLIENT)
static final class EquipmentRenderer.LayerTextureKey
extends Record {
    final EquipmentModel.LayerType layerType;
    final EquipmentModel.Layer layer;

    EquipmentRenderer.LayerTextureKey(EquipmentModel.LayerType layerType, EquipmentModel.Layer layer) {
        this.layerType = layerType;
        this.layer = layer;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{EquipmentRenderer.LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EquipmentRenderer.LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EquipmentRenderer.LayerTextureKey.class, "layerType;layer", "layerType", "layer"}, this, object);
    }

    public EquipmentModel.LayerType layerType() {
        return this.layerType;
    }

    public EquipmentModel.Layer layer() {
        return this.layer;
    }
}
