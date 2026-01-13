/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
static class ReferencedModelsCollector.Holder
implements BakedSimpleModel {
    private static final ReferencedModelsCollector.Property<Boolean> AMBIENT_OCCLUSION_PROPERTY = ReferencedModelsCollector.Holder.createProperty(0);
    private static final ReferencedModelsCollector.Property<UnbakedModel.GuiLight> GUI_LIGHT_PROPERTY = ReferencedModelsCollector.Holder.createProperty(1);
    private static final ReferencedModelsCollector.Property<Geometry> GEOMETRY_PROPERTY = ReferencedModelsCollector.Holder.createProperty(2);
    private static final ReferencedModelsCollector.Property<ModelTransformation> TRANSFORMATIONS_PROPERTY = ReferencedModelsCollector.Holder.createProperty(3);
    private static final ReferencedModelsCollector.Property<ModelTextures> TEXTURE_PROPERTY = ReferencedModelsCollector.Holder.createProperty(4);
    private static final ReferencedModelsCollector.Property<Sprite> PARTICLE_TEXTURE_PROPERTY = ReferencedModelsCollector.Holder.createProperty(5);
    private static final ReferencedModelsCollector.Property<BakedGeometry> BAKED_GEOMETRY_PROPERTY = ReferencedModelsCollector.Holder.createProperty(6);
    private static final int PROPERTY_COUNT = 7;
    private final Identifier id;
    boolean valid;
    @Nullable ReferencedModelsCollector.Holder parent;
    final UnbakedModel model;
    private final AtomicReferenceArray<@Nullable Object> properties = new AtomicReferenceArray(7);
    private final Map<ModelBakeSettings, BakedGeometry> bakeCache = new ConcurrentHashMap<ModelBakeSettings, BakedGeometry>();

    private static <T> ReferencedModelsCollector.Property<T> createProperty(int i) {
        Objects.checkIndex(i, 7);
        return new ReferencedModelsCollector.Property(i);
    }

    ReferencedModelsCollector.Holder(Identifier id, UnbakedModel model, boolean valid) {
        this.id = id;
        this.model = model;
        this.valid = valid;
    }

    @Override
    public UnbakedModel getModel() {
        return this.model;
    }

    @Override
    public @Nullable BakedSimpleModel getParent() {
        return this.parent;
    }

    @Override
    public String name() {
        return this.id.toString();
    }

    private <T> @Nullable T getProperty(ReferencedModelsCollector.Property<T> property) {
        return (T)this.properties.get(property.index);
    }

    private <T> T setProperty(ReferencedModelsCollector.Property<T> property, T value) {
        T object = this.properties.compareAndExchange(property.index, null, value);
        if (object == null) {
            return value;
        }
        return object;
    }

    private <T> T getProperty(ReferencedModelsCollector.Property<T> property, Function<BakedSimpleModel, T> fallback) {
        T object = this.getProperty(property);
        if (object != null) {
            return object;
        }
        return this.setProperty(property, fallback.apply(this));
    }

    @Override
    public boolean getAmbientOcclusion() {
        return this.getProperty(AMBIENT_OCCLUSION_PROPERTY, BakedSimpleModel::getAmbientOcclusion);
    }

    @Override
    public UnbakedModel.GuiLight getGuiLight() {
        return this.getProperty(GUI_LIGHT_PROPERTY, BakedSimpleModel::getGuiLight);
    }

    @Override
    public ModelTransformation getTransformations() {
        return this.getProperty(TRANSFORMATIONS_PROPERTY, BakedSimpleModel::copyTransformations);
    }

    @Override
    public Geometry getGeometry() {
        return this.getProperty(GEOMETRY_PROPERTY, BakedSimpleModel::getGeometry);
    }

    @Override
    public ModelTextures getTextures() {
        return this.getProperty(TEXTURE_PROPERTY, BakedSimpleModel::getTextures);
    }

    @Override
    public Sprite getParticleTexture(ModelTextures textures, Baker baker) {
        Sprite sprite = this.getProperty(PARTICLE_TEXTURE_PROPERTY);
        if (sprite != null) {
            return sprite;
        }
        return this.setProperty(PARTICLE_TEXTURE_PROPERTY, BakedSimpleModel.getParticleTexture(textures, baker, this));
    }

    private BakedGeometry getBakedGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
        BakedGeometry bakedGeometry = this.getProperty(BAKED_GEOMETRY_PROPERTY);
        if (bakedGeometry != null) {
            return bakedGeometry;
        }
        return this.setProperty(BAKED_GEOMETRY_PROPERTY, this.getGeometry().bake(textures, baker, settings, this));
    }

    @Override
    public BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
        if (settings == ModelRotation.IDENTITY) {
            return this.getBakedGeometry(textures, baker, settings);
        }
        return this.bakeCache.computeIfAbsent(settings, settings1 -> {
            Geometry geometry = this.getGeometry();
            return geometry.bake(textures, baker, (ModelBakeSettings)settings1, this);
        });
    }
}
