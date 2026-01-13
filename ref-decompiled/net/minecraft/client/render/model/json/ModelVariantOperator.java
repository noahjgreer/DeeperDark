/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.json.ModelVariant
 *  net.minecraft.client.render.model.json.ModelVariantOperator
 *  net.minecraft.client.render.model.json.ModelVariantOperator$Settings
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.AxisRotation
 */
package net.minecraft.client.render.model.json;

import java.util.function.UnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public interface ModelVariantOperator
extends UnaryOperator<ModelVariant> {
    public static final Settings<AxisRotation> ROTATION_X = ModelVariant::withRotationX;
    public static final Settings<AxisRotation> ROTATION_Y = ModelVariant::withRotationY;
    public static final Settings<AxisRotation> ROTATION_Z = ModelVariant::withRotationZ;
    public static final Settings<Identifier> MODEL = ModelVariant::withModel;
    public static final Settings<Boolean> UV_LOCK = ModelVariant::withUVLock;

    default public ModelVariantOperator then(ModelVariantOperator variant) {
        return variantx -> (ModelVariant)variant.apply((Object)((ModelVariant)this.apply(variantx)));
    }
}

