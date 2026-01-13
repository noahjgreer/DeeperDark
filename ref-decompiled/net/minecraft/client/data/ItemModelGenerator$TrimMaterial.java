/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.equipment.trim.ArmorTrimAssets;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.registry.RegistryKey;

@Environment(value=EnvType.CLIENT)
public static final class ItemModelGenerator.TrimMaterial
extends Record {
    private final ArmorTrimAssets assets;
    final RegistryKey<ArmorTrimMaterial> materialKey;

    public ItemModelGenerator.TrimMaterial(ArmorTrimAssets assets, RegistryKey<ArmorTrimMaterial> materialKey) {
        this.assets = assets;
        this.materialKey = materialKey;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemModelGenerator.TrimMaterial.class, "assets;materialKey", "assets", "materialKey"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemModelGenerator.TrimMaterial.class, "assets;materialKey", "assets", "materialKey"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemModelGenerator.TrimMaterial.class, "assets;materialKey", "assets", "materialKey"}, this, object);
    }

    public ArmorTrimAssets assets() {
        return this.assets;
    }

    public RegistryKey<ArmorTrimMaterial> materialKey() {
        return this.materialKey;
    }
}
