/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedQuadFactory;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public record UnbakedGeometry(List<ModelElement> elements) implements Geometry
{
    @Override
    public BakedGeometry bake(ModelTextures modelTextures, Baker baker, ModelBakeSettings modelBakeSettings, SimpleModel simpleModel) {
        return UnbakedGeometry.bakeGeometry(this.elements, modelTextures, baker, modelBakeSettings, simpleModel);
    }

    public static BakedGeometry bakeGeometry(List<ModelElement> elements, ModelTextures textures, Baker baker, ModelBakeSettings settings, SimpleModel model) {
        BakedGeometry.Builder builder = new BakedGeometry.Builder();
        for (ModelElement modelElement : elements) {
            boolean bl = true;
            boolean bl2 = true;
            boolean bl3 = true;
            Vector3fc vector3fc = modelElement.from();
            Vector3fc vector3fc2 = modelElement.to();
            if (vector3fc.x() == vector3fc2.x()) {
                bl2 = false;
                bl3 = false;
            }
            if (vector3fc.y() == vector3fc2.y()) {
                bl = false;
                bl3 = false;
            }
            if (vector3fc.z() == vector3fc2.z()) {
                bl = false;
                bl2 = false;
            }
            if (!bl && !bl2 && !bl3) continue;
            for (Map.Entry<Direction, ModelElementFace> entry : modelElement.faces().entrySet()) {
                boolean bl4;
                Direction direction = entry.getKey();
                ModelElementFace modelElementFace = entry.getValue();
                if (!(bl4 = (switch (direction.getAxis()) {
                    default -> throw new MatchException(null, null);
                    case Direction.Axis.X -> bl;
                    case Direction.Axis.Y -> bl2;
                    case Direction.Axis.Z -> bl3;
                }))) continue;
                Sprite sprite = baker.getSpriteGetter().get(textures, modelElementFace.textureId(), model);
                BakedQuad bakedQuad = BakedQuadFactory.bake(baker.getVec3fInterner(), vector3fc, vector3fc2, modelElementFace, sprite, direction, settings, modelElement.rotation(), modelElement.shade(), modelElement.lightEmission());
                if (modelElementFace.cullFace() == null) {
                    builder.add(bakedQuad);
                    continue;
                }
                builder.add(Direction.transform(settings.getRotation().getMatrix(), modelElementFace.cullFace()), bakedQuad);
            }
        }
        return builder.build();
    }
}
