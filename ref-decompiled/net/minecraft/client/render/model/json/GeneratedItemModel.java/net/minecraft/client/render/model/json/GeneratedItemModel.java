/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

@Environment(value=EnvType.CLIENT)
public class GeneratedItemModel
implements UnbakedModel {
    public static final Identifier GENERATED = Identifier.ofVanilla("builtin/generated");
    public static final List<String> LAYERS = List.of("layer0", "layer1", "layer2", "layer3", "layer4");
    private static final float field_32806 = 7.5f;
    private static final float field_32807 = 8.5f;
    private static final ModelTextures.Textures TEXTURES = new ModelTextures.Textures.Builder().addTextureReference("particle", "layer0").build();
    private static final ModelElementFace.UV FACING_SOUTH_UV = new ModelElementFace.UV(0.0f, 0.0f, 16.0f, 16.0f);
    private static final ModelElementFace.UV FACING_NORTH_UV = new ModelElementFace.UV(16.0f, 0.0f, 0.0f, 16.0f);
    private static final float field_64230 = 0.1f;

    @Override
    public ModelTextures.Textures textures() {
        return TEXTURES;
    }

    @Override
    public Geometry geometry() {
        return GeneratedItemModel::bakeGeometry;
    }

    @Override
    public @Nullable UnbakedModel.GuiLight guiLight() {
        return UnbakedModel.GuiLight.ITEM;
    }

    private static BakedGeometry bakeGeometry(ModelTextures textures, Baker baker, ModelBakeSettings settings, SimpleModel model) {
        String string;
        SpriteIdentifier spriteIdentifier;
        ArrayList<ModelElement> list = new ArrayList<ModelElement>();
        for (int i = 0; i < LAYERS.size() && (spriteIdentifier = textures.get(string = LAYERS.get(i))) != null; ++i) {
            SpriteContents spriteContents = baker.getSpriteGetter().get(spriteIdentifier, model).getContents();
            list.addAll(GeneratedItemModel.addLayerElements(i, string, spriteContents));
        }
        return UnbakedGeometry.bakeGeometry(list, textures, baker, settings, model);
    }

    private static List<ModelElement> addLayerElements(int tintIndex, String name, SpriteContents spriteContents) {
        Map<Direction, ModelElementFace> map = Map.of(Direction.SOUTH, new ModelElementFace(null, tintIndex, name, FACING_SOUTH_UV, AxisRotation.R0), Direction.NORTH, new ModelElementFace(null, tintIndex, name, FACING_NORTH_UV, AxisRotation.R0));
        ArrayList<ModelElement> list = new ArrayList<ModelElement>();
        list.add(new ModelElement((Vector3fc)new Vector3f(0.0f, 0.0f, 7.5f), (Vector3fc)new Vector3f(16.0f, 16.0f, 8.5f), map));
        list.addAll(GeneratedItemModel.addSubComponents(spriteContents, name, tintIndex));
        return list;
    }

    private static List<ModelElement> addSubComponents(SpriteContents sprite, String textureId, int tintIndex) {
        float f = 16.0f / (float)sprite.getWidth();
        float g = 16.0f / (float)sprite.getHeight();
        ArrayList<ModelElement> list = new ArrayList<ModelElement>();
        for (class_12295 lv : GeneratedItemModel.getFrames(sprite)) {
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
                    boolean bl2 = bl = !GeneratedItemModel.isPixelTransparent(spriteContents, k, m, l, i, j);
                    if (!bl) continue;
                    GeneratedItemModel.buildCube(Side.UP, set, spriteContents, k, m, l, i, j);
                    GeneratedItemModel.buildCube(Side.DOWN, set, spriteContents, k, m, l, i, j);
                    GeneratedItemModel.buildCube(Side.LEFT, set, spriteContents, k, m, l, i, j);
                    GeneratedItemModel.buildCube(Side.RIGHT, set, spriteContents, k, m, l, i, j);
                }
            }
        });
        return set;
    }

    private static void buildCube(Side side, Set<class_12295> set, SpriteContents spriteContents, int i, int j, int k, int l, int m) {
        if (GeneratedItemModel.isPixelTransparent(spriteContents, i, j - side.direction.getOffsetX(), k - side.direction.getOffsetY(), l, m)) {
            set.add(new class_12295(side, j, k));
        }
    }

    private static boolean isPixelTransparent(SpriteContents spriteContents, int i, int j, int k, int l, int m) {
        if (j < 0 || k < 0 || j >= l || k >= m) {
            return true;
        }
        return spriteContents.isPixelTransparent(i, j, k);
    }

    @Environment(value=EnvType.CLIENT)
    record class_12295(Side facing, int x, int y) {
    }

    @Environment(value=EnvType.CLIENT)
    static final class Side
    extends Enum<Side> {
        public static final /* enum */ Side UP = new Side(Direction.UP);
        public static final /* enum */ Side DOWN = new Side(Direction.DOWN);
        public static final /* enum */ Side LEFT = new Side(Direction.EAST);
        public static final /* enum */ Side RIGHT = new Side(Direction.WEST);
        final Direction direction;
        private static final /* synthetic */ Side[] field_4282;

        public static Side[] values() {
            return (Side[])field_4282.clone();
        }

        public static Side valueOf(String string) {
            return Enum.valueOf(Side.class, string);
        }

        private Side(Direction direction) {
            this.direction = direction;
        }

        public Direction getDirection() {
            return this.direction;
        }

        boolean isVertical() {
            return this == DOWN || this == UP;
        }

        private static /* synthetic */ Side[] method_36921() {
            return new Side[]{UP, DOWN, LEFT, RIGHT};
        }

        static {
            field_4282 = Side.method_36921();
        }
    }
}
