/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface ModelVariantOperator.Settings<T> {
    public ModelVariant apply(ModelVariant var1, T var2);

    default public ModelVariantOperator withValue(T value) {
        return setting -> this.apply((ModelVariant)setting, value);
    }
}
