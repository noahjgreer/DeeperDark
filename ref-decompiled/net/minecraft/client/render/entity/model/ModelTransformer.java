/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.render.entity.model.ModelTransformer
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface ModelTransformer {
    public static final ModelTransformer NO_OP = data -> data;

    public static ModelTransformer scaling(float scale) {
        float f = 24.016f * (1.0f - scale);
        return data -> data.transform(transform -> transform.scaled(scale).moveOrigin(0.0f, f, 0.0f));
    }

    public ModelData apply(ModelData var1);
}

