/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedGeometry
 *  net.minecraft.client.render.model.BakedSimpleModel
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.Geometry
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.client.render.model.ModelTextures
 *  net.minecraft.client.render.model.ModelTextures$Builder
 *  net.minecraft.client.render.model.SimpleModel
 *  net.minecraft.client.render.model.UnbakedModel
 *  net.minecraft.client.render.model.UnbakedModel$GuiLight
 *  net.minecraft.client.render.model.json.ModelTransformation
 *  net.minecraft.client.render.model.json.Transformation
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.item.ItemDisplayContext
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public interface BakedSimpleModel
extends SimpleModel {
    public static final boolean DEFAULT_AMBIENT_OCCLUSION = true;
    public static final UnbakedModel.GuiLight DEFAULT_GUI_LIGHT = UnbakedModel.GuiLight.BLOCK;

    public UnbakedModel getModel();

    public @Nullable BakedSimpleModel getParent();

    public static ModelTextures getTextures(BakedSimpleModel model) {
        ModelTextures.Builder builder = new ModelTextures.Builder();
        for (BakedSimpleModel bakedSimpleModel = model; bakedSimpleModel != null; bakedSimpleModel = bakedSimpleModel.getParent()) {
            builder.addLast(bakedSimpleModel.getModel().textures());
        }
        return builder.build((SimpleModel)model);
    }

    default public ModelTextures getTextures() {
        return BakedSimpleModel.getTextures((BakedSimpleModel)this);
    }

    public static boolean getAmbientOcclusion(BakedSimpleModel model) {
        while (model != null) {
            Boolean boolean_ = model.getModel().ambientOcclusion();
            if (boolean_ != null) {
                return boolean_;
            }
            model = model.getParent();
        }
        return true;
    }

    default public boolean getAmbientOcclusion() {
        return BakedSimpleModel.getAmbientOcclusion((BakedSimpleModel)this);
    }

    public static UnbakedModel.GuiLight getGuiLight(BakedSimpleModel model) {
        while (model != null) {
            UnbakedModel.GuiLight guiLight = model.getModel().guiLight();
            if (guiLight != null) {
                return guiLight;
            }
            model = model.getParent();
        }
        return DEFAULT_GUI_LIGHT;
    }

    default public UnbakedModel.GuiLight getGuiLight() {
        return BakedSimpleModel.getGuiLight((BakedSimpleModel)this);
    }

    public static Geometry getGeometry(BakedSimpleModel model) {
        while (model != null) {
            Geometry geometry = model.getModel().geometry();
            if (geometry != null) {
                return geometry;
            }
            model = model.getParent();
        }
        return Geometry.EMPTY;
    }

    default public Geometry getGeometry() {
        return BakedSimpleModel.getGeometry((BakedSimpleModel)this);
    }

    default public BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings) {
        return this.getGeometry().bake(textures, baker, settings, (SimpleModel)this);
    }

    public static Sprite getParticleTexture(ModelTextures textures, Baker baker, SimpleModel model) {
        return baker.getSpriteGetter().get(textures, "particle", model);
    }

    default public Sprite getParticleTexture(ModelTextures textures, Baker baker) {
        return BakedSimpleModel.getParticleTexture((ModelTextures)textures, (Baker)baker, (SimpleModel)this);
    }

    public static Transformation extractTransformation(BakedSimpleModel model, ItemDisplayContext mode) {
        while (model != null) {
            Transformation transformation;
            ModelTransformation modelTransformation = model.getModel().transformations();
            if (modelTransformation != null && (transformation = modelTransformation.getTransformation(mode)) != Transformation.IDENTITY) {
                return transformation;
            }
            model = model.getParent();
        }
        return Transformation.IDENTITY;
    }

    public static ModelTransformation copyTransformations(BakedSimpleModel model) {
        Transformation transformation = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
        Transformation transformation2 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
        Transformation transformation3 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.FIRST_PERSON_LEFT_HAND);
        Transformation transformation4 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.FIRST_PERSON_RIGHT_HAND);
        Transformation transformation5 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.HEAD);
        Transformation transformation6 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.GUI);
        Transformation transformation7 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.GROUND);
        Transformation transformation8 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.FIXED);
        Transformation transformation9 = BakedSimpleModel.extractTransformation((BakedSimpleModel)model, (ItemDisplayContext)ItemDisplayContext.ON_SHELF);
        return new ModelTransformation(transformation, transformation2, transformation3, transformation4, transformation5, transformation6, transformation7, transformation8, transformation9);
    }

    default public ModelTransformation getTransformations() {
        return BakedSimpleModel.copyTransformations((BakedSimpleModel)this);
    }
}

