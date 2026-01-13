/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.Geometry
 *  net.minecraft.client.render.model.ModelTextures$Textures
 *  net.minecraft.client.render.model.UnbakedModel
 *  net.minecraft.client.render.model.json.JsonUnbakedModel
 *  net.minecraft.client.render.model.json.JsonUnbakedModel$Deserializer
 *  net.minecraft.client.render.model.json.ModelElement
 *  net.minecraft.client.render.model.json.ModelElement$Deserializer
 *  net.minecraft.client.render.model.json.ModelElementFace
 *  net.minecraft.client.render.model.json.ModelElementFace$Deserializer
 *  net.minecraft.client.render.model.json.ModelTransformation
 *  net.minecraft.client.render.model.json.ModelTransformation$Deserializer
 *  net.minecraft.client.render.model.json.Transformation
 *  net.minecraft.client.render.model.json.Transformation$Deserializer
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.JsonHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record JsonUnbakedModel(@Nullable Geometry geometry, // Could not load outer class - annotation placement on inner may be incorrect
@Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ModelTransformation transformations, ModelTextures.Textures textures, @Nullable Identifier parent) implements UnbakedModel
{
    private final @Nullable Geometry geometry;
    private final // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable UnbakedModel.GuiLight guiLight;
    private final @Nullable Boolean ambientOcclusion;
    private final @Nullable ModelTransformation transformations;
    private final ModelTextures.Textures textures;
    private final @Nullable Identifier parent;
    @VisibleForTesting
    static final Gson GSON = new GsonBuilder().registerTypeAdapter(JsonUnbakedModel.class, (Object)new Deserializer()).registerTypeAdapter(ModelElement.class, (Object)new ModelElement.Deserializer()).registerTypeAdapter(ModelElementFace.class, (Object)new ModelElementFace.Deserializer()).registerTypeAdapter(Transformation.class, (Object)new Transformation.Deserializer()).registerTypeAdapter(ModelTransformation.class, (Object)new ModelTransformation.Deserializer()).create();

    public JsonUnbakedModel(@Nullable Geometry geometry, // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable UnbakedModel.GuiLight guiLight, @Nullable Boolean ambientOcclusion, @Nullable ModelTransformation transformations, ModelTextures.Textures textures, @Nullable Identifier parent) {
        this.geometry = geometry;
        this.guiLight = guiLight;
        this.ambientOcclusion = ambientOcclusion;
        this.transformations = transformations;
        this.textures = textures;
        this.parent = parent;
    }

    public static JsonUnbakedModel deserialize(Reader input) {
        return (JsonUnbakedModel)JsonHelper.deserialize((Gson)GSON, (Reader)input, JsonUnbakedModel.class);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{JsonUnbakedModel.class, "geometry;guiLight;ambientOcclusion;transforms;textureSlots;parent", "geometry", "guiLight", "ambientOcclusion", "transformations", "textures", "parent"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{JsonUnbakedModel.class, "geometry;guiLight;ambientOcclusion;transforms;textureSlots;parent", "geometry", "guiLight", "ambientOcclusion", "transformations", "textures", "parent"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{JsonUnbakedModel.class, "geometry;guiLight;ambientOcclusion;transforms;textureSlots;parent", "geometry", "guiLight", "ambientOcclusion", "transformations", "textures", "parent"}, this, object);
    }

    public @Nullable Geometry geometry() {
        return this.geometry;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable UnbakedModel.GuiLight guiLight() {
        return this.guiLight;
    }

    public @Nullable Boolean ambientOcclusion() {
        return this.ambientOcclusion;
    }

    public @Nullable ModelTransformation transformations() {
        return this.transformations;
    }

    public ModelTextures.Textures textures() {
        return this.textures;
    }

    public @Nullable Identifier parent() {
        return this.parent;
    }
}

