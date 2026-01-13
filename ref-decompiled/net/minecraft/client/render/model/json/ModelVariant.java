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
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.BlockModelPart
 *  net.minecraft.client.render.model.BlockModelPart$Unbaked
 *  net.minecraft.client.render.model.GeometryBakedModel
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.client.render.model.ResolvableModel$Resolver
 *  net.minecraft.client.render.model.json.ModelVariant
 *  net.minecraft.client.render.model.json.ModelVariant$ModelState
 *  net.minecraft.client.render.model.json.ModelVariantOperator
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.AxisRotation
 */
package net.minecraft.client.render.model.json;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.GeometryBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.json.ModelVariant;
import net.minecraft.client.render.model.json.ModelVariantOperator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;

@Environment(value=EnvType.CLIENT)
public record ModelVariant(Identifier modelId, ModelState modelState) implements BlockModelPart.Unbaked
{
    private final Identifier modelId;
    private final ModelState modelState;
    public static final MapCodec<ModelVariant> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("model").forGetter(ModelVariant::modelId), (App)ModelState.CODEC.forGetter(ModelVariant::modelState)).apply((Applicative)instance, ModelVariant::new));
    public static final Codec<ModelVariant> CODEC = MAP_CODEC.codec();

    public ModelVariant(Identifier model) {
        this(model, ModelState.DEFAULT);
    }

    public ModelVariant(Identifier location, ModelState modelState) {
        this.modelId = location;
        this.modelState = modelState;
    }

    public ModelVariant withRotationX(AxisRotation amount) {
        return this.setState(this.modelState.setRotationX(amount));
    }

    public ModelVariant withRotationY(AxisRotation amount) {
        return this.setState(this.modelState.setRotationY(amount));
    }

    public ModelVariant withRotationZ(AxisRotation amount) {
        return this.setState(this.modelState.setRotationZ(amount));
    }

    public ModelVariant withUVLock(boolean uvLock) {
        return this.setState(this.modelState.setUVLock(uvLock));
    }

    public ModelVariant withModel(Identifier modelId) {
        return new ModelVariant(modelId, this.modelState);
    }

    public ModelVariant setState(ModelState modelState) {
        return new ModelVariant(this.modelId, modelState);
    }

    public ModelVariant with(ModelVariantOperator variantOperator) {
        return (ModelVariant)variantOperator.apply((Object)this);
    }

    public BlockModelPart bake(Baker baker) {
        return GeometryBakedModel.create((Baker)baker, (Identifier)this.modelId, (ModelBakeSettings)this.modelState.asModelBakeSettings());
    }

    public void resolve(ResolvableModel.Resolver resolver) {
        resolver.markDependency(this.modelId);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelVariant.class, "modelLocation;modelState", "modelId", "modelState"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelVariant.class, "modelLocation;modelState", "modelId", "modelState"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelVariant.class, "modelLocation;modelState", "modelId", "modelState"}, this, o);
    }

    public Identifier modelId() {
        return this.modelId;
    }

    public ModelState modelState() {
        return this.modelState;
    }
}

