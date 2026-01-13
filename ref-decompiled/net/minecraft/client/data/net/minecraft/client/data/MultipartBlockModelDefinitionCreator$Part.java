/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.data;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelComponent;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.client.render.model.json.WeightedVariant;

@Environment(value=EnvType.CLIENT)
record MultipartBlockModelDefinitionCreator.Part(Optional<MultipartModelCondition> condition, WeightedVariant variants) {
    public MultipartModelComponent toComponent() {
        return new MultipartModelComponent(this.condition, this.variants.toModel());
    }
}
