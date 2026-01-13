/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.item;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemAsset;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static final class ItemAssetsLoader.Definition
extends Record {
    final Identifier id;
    final @Nullable ItemAsset clientItemInfo;

    ItemAssetsLoader.Definition(Identifier id, @Nullable ItemAsset clientItemInfo) {
        this.id = id;
        this.clientItemInfo = clientItemInfo;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemAssetsLoader.Definition.class, "id;clientItemInfo", "id", "clientItemInfo"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemAssetsLoader.Definition.class, "id;clientItemInfo", "id", "clientItemInfo"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemAssetsLoader.Definition.class, "id;clientItemInfo", "id", "clientItemInfo"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }

    public @Nullable ItemAsset clientItemInfo() {
        return this.clientItemInfo;
    }
}
