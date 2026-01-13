/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedGeometry
 *  net.minecraft.client.render.model.Baker
 *  net.minecraft.client.render.model.Geometry
 *  net.minecraft.client.render.model.ModelBakeSettings
 *  net.minecraft.client.render.model.ModelTextures
 *  net.minecraft.client.render.model.ModelTextures$Textures
 *  net.minecraft.client.render.model.ModelTextures$Textures$Builder
 *  net.minecraft.client.render.model.SimpleModel
 *  net.minecraft.client.render.model.UnbakedGeometry
 *  net.minecraft.client.render.model.UnbakedModel
 *  net.minecraft.client.render.model.UnbakedModel$GuiLight
 *  net.minecraft.client.render.model.json.GeneratedItemModel
 *  net.minecraft.client.render.model.json.GeneratedItemModel$Side
 *  net.minecraft.client.render.model.json.GeneratedItemModel$class_12295
 *  net.minecraft.client.render.model.json.ModelElement
 *  net.minecraft.client.render.model.json.ModelElementFace
 *  net.minecraft.client.render.model.json.ModelElementFace$UV
 *  net.minecraft.client.texture.SpriteContents
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.AxisRotation
 *  net.minecraft.util.math.Direction
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.Geometry;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.render.model.UnbakedGeometry;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.GeneratedItemModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.texture.SpriteContents;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class GeneratedItemModel
implements UnbakedModel {
    public static final Identifier GENERATED = Identifier.ofVanilla((String)"builtin/generated");
    public static final List<String> LAYERS = List.of("layer0", "layer1", "layer2", "layer3", "layer4");
    private static final float field_32806 = 7.5f;
    private static final float field_32807 = 8.5f;
    private static final ModelTextures.Textures TEXTURES = new ModelTextures.Textures.Builder().addTextureReference("particle", "layer0").build();
    private static final ModelElementFace.UV FACING_SOUTH_UV = new ModelElementFace.UV(0.0f, 0.0f, 16.0f, 16.0f);
    private static final ModelElementFace.UV FACING_NORTH_UV = new ModelElementFace.UV(16.0f, 0.0f, 0.0f, 16.0f);
    private static final float field_64230 = 0.1f;

    public ModelTextures.Textures textures() {
        return TEXTURES;
    }

    public Geometry geometry() {
        return GeneratedItemModel::bakeGeometry;
    }

    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable UnbakedModel.GuiLight guiLight() {
        return UnbakedModel.GuiLight.ITEM;
    }

    private static BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings, SimpleModel model) {
        String string;
        SpriteIdentifier spriteIdentifier;
        ArrayList list = new ArrayList();
        for (int i = 0; i < LAYERS.size() && (spriteIdentifier = textures.get(string = (String)LAYERS.get(i))) != null; ++i) {
            SpriteContents spriteContents = baker.getSpriteGetter().get(spriteIdentifier, model).getContents();
            list.addAll(GeneratedItemModel.addLayerElements((int)i, (String)string, (SpriteContents)spriteContents));
        }
        return UnbakedGeometry.bakeGeometry(list, (ModelTextures)textures, (Baker)baker, (ModelBakeSettings)settings, (SimpleModel)model);
    }

    private static List<ModelElement> addLayerElements(int tintIndex, String name, SpriteContents spriteContents) {
        Map<Direction, ModelElementFace> map = Map.of(Direction.SOUTH, new ModelElementFace(null, tintIndex, name, FACING_SOUTH_UV, AxisRotation.R0), Direction.NORTH, new ModelElementFace(null, tintIndex, name, FACING_NORTH_UV, AxisRotation.R0));
        ArrayList<ModelElement> list = new ArrayList<ModelElement>();
        list.add(new ModelElement((Vector3fc)new Vector3f(0.0f, 0.0f, 7.5f), (Vector3fc)new Vector3f(16.0f, 16.0f, 8.5f), map));
        list.addAll(GeneratedItemModel.addSubComponents((SpriteContents)spriteContents, (String)name, (int)tintIndex));
        return list;
    }

    private static List<ModelElement> addSubComponents(SpriteContents sprite, String textureId, int tintIndex) {
        float f = 16.0f / (float)sprite.getWidth();
        float g = 16.0f / (float)sprite.getHeight();
        ArrayList<ModelElement> list = new ArrayList<ModelElement>();
        for (class_12295 lv : GeneratedItemModel.getFrames((SpriteContents)sprite)) {
            float m;
            float l;
            float h = lv.x();
            float i = lv.y();
            Side side = lv.facing();
            float j = h + 0.1f;
            float k = h + 1.0f - 0.1f;
            if (side.isVertical()) {
                l = i + 0.1f;
                m = i + 1.0f - 0.1f;
            } else {
                l = i + 1.0f - 0.1f;
                m = i + 0.1f;
            }
            float n = h;
            float o = i;
            float p = h;
            float q = i;
            switch (side.ordinal()) {
                case 0: {
                    p += 1.0f;
                    break;
                }
                case 1: {
                    p += 1.0f;
                    o += 1.0f;
                    q += 1.0f;
                    break;
                }
                case 2: {
                    q += 1.0f;
                    break;
                }
                case 3: {
                    n += 1.0f;
                    p += 1.0f;
                    q += 1.0f;
                }
            }
            n *= f;
            p *= f;
            o *= g;
            q *= g;
            o = 16.0f - o;
            q = 16.0f - q;
            Map<Direction, ModelElementFace> map = Map.of(side.getDirection(), new ModelElementFace(null, tintIndex, textureId, new ModelElementFace.UV(j * f, l * f, k * g, m * g), AxisRotation.R0));
            switch (side.ordinal()) {
                case 0: {
                    list.add(new ModelElement((Vector3fc)new Vector3f(n, o, 7.5f), (Vector3fc)new Vector3f(p, o, 8.5f), map));
                    break;
                }
                case 1: {
                    list.add(new ModelElement((Vector3fc)new Vector3f(n, q, 7.5f), (Vector3fc)new Vector3f(p, q, 8.5f), map));
                    break;
                }
                case 2: {
                    list.add(new ModelElement((Vector3fc)new Vector3f(n, o, 7.5f), (Vector3fc)new Vector3f(n, q, 8.5f), map));
                    break;
                }
                case 3: {
                    list.add(new ModelElement((Vector3fc)new Vector3f(p, o, 7.5f), (Vector3fc)new Vector3f(p, q, 8.5f), map));
                }
            }
        }
        return list;
    }

    private static Collection<class_12295> getFrames(SpriteContents spriteContents) {
        int i = spriteContents.getWidth();
        int j = spriteContents.getHeight();
        HashSet<class_12295> set = new HashSet<class_12295>();
        spriteContents.getDistinctFrameCount().forEach(k -> {
            for (int l = 0; l < j; ++l) {
                for (int m = 0; m < i; ++m) {
                    boolean bl;
                    boolean bl2 = bl = !GeneratedItemModel.isPixelTransparent((SpriteContents)spriteContents, (int)k, (int)m, (int)l, (int)i, (int)j);
                    if (!bl) continue;
                    GeneratedItemModel.buildCube((Side)Side.UP, (Set)set, (SpriteContents)spriteContents, (int)k, (int)m, (int)l, (int)i, (int)j);
                    GeneratedItemModel.buildCube((Side)Side.DOWN, (Set)set, (SpriteContents)spriteContents, (int)k, (int)m, (int)l, (int)i, (int)j);
                    GeneratedItemModel.buildCube((Side)Side.LEFT, (Set)set, (SpriteContents)spriteContents, (int)k, (int)m, (int)l, (int)i, (int)j);
                    GeneratedItemModel.buildCube((Side)Side.RIGHT, (Set)set, (SpriteContents)spriteContents, (int)k, (int)m, (int)l, (int)i, (int)j);
                }
            }
        });
        return set;
    }

    private static void buildCube(Side side, Set<class_12295> set, SpriteContents spriteContents, int i, int j, int k, int l, int m) {
        if (GeneratedItemModel.isPixelTransparent((SpriteContents)spriteContents, (int)i, (int)(j - side.direction.getOffsetX()), (int)(k - side.direction.getOffsetY()), (int)l, (int)m)) {
            set.add(new class_12295(side, j, k));
        }
    }

    private static boolean isPixelTransparent(SpriteContents spriteContents, int i, int j, int k, int l, int m) {
        if (j < 0 || k < 0 || j >= l || k >= m) {
            return true;
        }
        return spriteContents.isPixelTransparent(i, j, k);
    }
}

