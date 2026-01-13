/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedGeometry
 *  net.minecraft.client.render.model.BakedGeometry$Builder
 *  net.minecraft.client.render.model.BakedQuad
 *  net.minecraft.client.render.model.BakedQuadFactory
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.Baker$Vec3fInterner
 *  net.minecraft.client.render.model.Geometry
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.client.render.model.ModelTextures
 *  net.minecraft.client.render.model.SimpleModel
 *  net.minecraft.client.render.model.UnbakedGeometry
 *  net.minecraft.client.render.model.UnbakedGeometry$1
 *  net.minecraft.client.render.model.json.ModelElement
 *  net.minecraft.client.render.model.json.ModelElementFace
 *  net.minecraft.client.render.model.json.ModelElementRotation
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.util.math.Direction
 *  org.joml.Matrix4fc
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
import net.minecraft.client.render.model.UnbakedGeometry;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public record UnbakedGeometry(List<ModelElement> elements) implements Geometry
{
    private final List<ModelElement> elements;

    public UnbakedGeometry(List<ModelElement> elements) {
        this.elements = elements;
    }

    public BakedGeometry bake(ModelTextures modelTextures, Baker baker, ModelBakeSettings modelBakeSettings, SimpleModel simpleModel) {
        return UnbakedGeometry.bakeGeometry((List)this.elements, (ModelTextures)modelTextures, (Baker)baker, (ModelBakeSettings)modelBakeSettings, (SimpleModel)simpleModel);
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
            for (Map.Entry entry : modelElement.faces().entrySet()) {
                boolean bl4;
                Direction direction = (Direction)entry.getKey();
                ModelElementFace modelElementFace = (ModelElementFace)entry.getValue();
                if (!(bl4 = (switch (1.field_64676[direction.getAxis().ordinal()]) {
                    default -> throw new MatchException(null, null);
                    case 1 -> bl;
                    case 2 -> bl2;
                    case 3 -> bl3;
                }))) continue;
                Sprite sprite = baker.getSpriteGetter().get(textures, modelElementFace.textureId(), model);
                BakedQuad bakedQuad = BakedQuadFactory.bake((Baker.Vec3fInterner)baker.getVec3fInterner(), (Vector3fc)vector3fc, (Vector3fc)vector3fc2, (ModelElementFace)modelElementFace, (Sprite)sprite, (Direction)direction, (ModelBakeSettings)settings, (ModelElementRotation)modelElement.rotation(), (boolean)modelElement.shade(), (int)modelElement.lightEmission());
                if (modelElementFace.cullFace() == null) {
                    builder.add(bakedQuad);
                    continue;
                }
                builder.add(Direction.transform((Matrix4fc)settings.getRotation().getMatrix(), (Direction)modelElementFace.cullFace()), bakedQuad);
            }
        }
        return builder.build();
    }

    public List<ModelElement> elements() {
        return this.elements;
    }
}

