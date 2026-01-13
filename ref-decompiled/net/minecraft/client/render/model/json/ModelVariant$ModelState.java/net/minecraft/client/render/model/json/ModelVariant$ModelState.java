/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.util.math.AxisRotation;

@Environment(value=EnvType.CLIENT)
public record ModelVariant.ModelState(AxisRotation x, AxisRotation y, AxisRotation z, boolean uvLock) {
    public static final MapCodec<ModelVariant.ModelState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)AxisRotation.CODEC.optionalFieldOf("x", (Object)AxisRotation.R0).forGetter(ModelVariant.ModelState::x), (App)AxisRotation.CODEC.optionalFieldOf("y", (Object)AxisRotation.R0).forGetter(ModelVariant.ModelState::y), (App)AxisRotation.CODEC.optionalFieldOf("z", (Object)AxisRotation.R0).forGetter(ModelVariant.ModelState::z), (App)Codec.BOOL.optionalFieldOf("uvlock", (Object)false).forGetter(ModelVariant.ModelState::uvLock)).apply((Applicative)instance, ModelVariant.ModelState::new));
    public static final ModelVariant.ModelState DEFAULT = new ModelVariant.ModelState(AxisRotation.R0, AxisRotation.R0, AxisRotation.R0, false);

    public ModelBakeSettings asModelBakeSettings() {
        ModelRotation modelRotation = ModelRotation.fromDirectionTransformation(AxisRotation.method_76600(this.x, this.y, this.z));
        return this.uvLock ? modelRotation.getUVModel() : modelRotation;
    }

    public ModelVariant.ModelState setRotationX(AxisRotation amount) {
        return new ModelVariant.ModelState(amount, this.y, this.z, this.uvLock);
    }

    public ModelVariant.ModelState setRotationY(AxisRotation amount) {
        return new ModelVariant.ModelState(this.x, amount, this.z, this.uvLock);
    }

    public ModelVariant.ModelState setRotationZ(AxisRotation amount) {
        return new ModelVariant.ModelState(this.x, this.y, amount, this.uvLock);
    }

    public ModelVariant.ModelState setUVLock(boolean uvLock) {
        return new ModelVariant.ModelState(this.x, this.y, this.z, uvLock);
    }
}
