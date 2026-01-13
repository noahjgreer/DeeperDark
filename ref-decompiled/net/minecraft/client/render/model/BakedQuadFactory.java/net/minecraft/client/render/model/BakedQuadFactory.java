/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.GeometryUtils
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.model;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.CubeFace;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementRotation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AxisRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import org.joml.GeometryUtils;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BakedQuadFactory {
    private static final Vector3fc CENTER = new Vector3f(0.5f, 0.5f, 0.5f);

    @VisibleForTesting
    static ModelElementFace.UV setDefaultUV(Vector3fc from, Vector3fc to, Direction facing) {
        return switch (facing) {
            default -> throw new MatchException(null, null);
            case Direction.DOWN -> new ModelElementFace.UV(from.x(), 16.0f - to.z(), to.x(), 16.0f - from.z());
            case Direction.UP -> new ModelElementFace.UV(from.x(), from.z(), to.x(), to.z());
            case Direction.NORTH -> new ModelElementFace.UV(16.0f - to.x(), 16.0f - to.y(), 16.0f - from.x(), 16.0f - from.y());
            case Direction.SOUTH -> new ModelElementFace.UV(from.x(), 16.0f - to.y(), to.x(), 16.0f - from.y());
            case Direction.WEST -> new ModelElementFace.UV(from.z(), 16.0f - to.y(), to.z(), 16.0f - from.y());
            case Direction.EAST -> new ModelElementFace.UV(16.0f - to.z(), 16.0f - to.y(), 16.0f - from.z(), 16.0f - from.y());
        };
    }

    public static BakedQuad bake(Baker.Vec3fInterner interner, Vector3fc from, Vector3fc to, ModelElementFace face, Sprite sprite, Direction direction, ModelBakeSettings settings, @Nullable ModelElementRotation rotation, boolean shade, int lightEmission) {
        ModelElementFace.UV uV = face.uvs();
        if (uV == null) {
            uV = BakedQuadFactory.setDefaultUV(from, to, direction);
        }
        Matrix4fc matrix4fc = settings.reverse(direction);
        Vector3fc[] vector3fcs = new Vector3fc[4];
        long[] ls = new long[4];
        CubeFace cubeFace = CubeFace.getFace(direction);
        for (int i = 0; i < 4; ++i) {
            BakedQuadFactory.packVertexData(i, cubeFace, uV, face.rotation(), matrix4fc, from, to, sprite, settings.getRotation(), rotation, vector3fcs, ls, interner);
        }
        Direction direction2 = BakedQuadFactory.decodeDirection(vector3fcs);
        if (rotation == null && direction2 != null) {
            BakedQuadFactory.encodeDirection(vector3fcs, ls, direction2);
        }
        return new BakedQuad(vector3fcs[0], vector3fcs[1], vector3fcs[2], vector3fcs[3], ls[0], ls[1], ls[2], ls[3], face.tintIndex(), Objects.requireNonNullElse(direction2, Direction.UP), sprite, shade, lightEmission);
    }

    private static void packVertexData(int corner, CubeFace cubeFace, ModelElementFace.UV uv, AxisRotation axisRotation, Matrix4fc matrix, Vector3fc from, Vector3fc to, Sprite sprite, AffineTransformation affineTransformation, @Nullable ModelElementRotation rotation, Vector3fc[] positions, long[] packedUvs, Baker.Vec3fInterner interner) {
        float i;
        float h;
        CubeFace.Corner corner2 = cubeFace.getCorner(corner);
        Vector3f vector3f = corner2.get(from, to).div(16.0f);
        if (rotation != null) {
            BakedQuadFactory.transformVertex(vector3f, rotation.origin(), rotation.transform());
        }
        if (affineTransformation != AffineTransformation.identity()) {
            BakedQuadFactory.transformVertex(vector3f, CENTER, affineTransformation.getMatrix());
        }
        float f = ModelElementFace.getUValue(uv, axisRotation, corner);
        float g = ModelElementFace.getVValue(uv, axisRotation, corner);
        if (MatrixUtil.isIdentity(matrix)) {
            h = f;
            i = g;
        } else {
            Vector3f vector3f2 = matrix.transformPosition(new Vector3f(BakedQuadFactory.setCenterBack(f), BakedQuadFactory.setCenterBack(g), 0.0f));
            h = BakedQuadFactory.setCenterForward(vector3f2.x);
            i = BakedQuadFactory.setCenterForward(vector3f2.y);
        }
        positions[corner] = interner.intern((Vector3fc)vector3f);
        packedUvs[corner] = Vector2f.toLong(sprite.getFrameU(h), sprite.getFrameV(i));
    }

    private static float setCenterBack(float f) {
        return f - 0.5f;
    }

    private static float setCenterForward(float f) {
        return f + 0.5f;
    }

    private static void transformVertex(Vector3f vertex, Vector3fc vector3fc, Matrix4fc matrix4fc) {
        vertex.sub(vector3fc);
        matrix4fc.transformPosition(vertex);
        vertex.add(vector3fc);
    }

    private static @Nullable Direction decodeDirection(Vector3fc[] vecs) {
        Vector3f vector3f = new Vector3f();
        GeometryUtils.normal((Vector3fc)vecs[0], (Vector3fc)vecs[1], (Vector3fc)vecs[2], (Vector3f)vector3f);
        return BakedQuadFactory.getDirection(vector3f);
    }

    private static @Nullable Direction getDirection(Vector3f vec) {
        if (!vec.isFinite()) {
            return null;
        }
        Direction direction = null;
        float f = 0.0f;
        for (Direction direction2 : Direction.values()) {
            float g = vec.dot(direction2.getFloatVector());
            if (!(g >= 0.0f) || !(g > f)) continue;
            f = g;
            direction = direction2;
        }
        return direction;
    }

    private static void encodeDirection(Vector3fc[] vector3fcs, long[] ls, Direction direction) {
        float o;
        float n;
        float f = 999.0f;
        float g = 999.0f;
        float h = 999.0f;
        float i = -999.0f;
        float j = -999.0f;
        float k = -999.0f;
        for (int l = 0; l < 4; ++l) {
            Vector3fc vector3fc = vector3fcs[l];
            float m = vector3fc.x();
            n = vector3fc.y();
            o = vector3fc.z();
            if (m < f) {
                f = m;
            }
            if (n < g) {
                g = n;
            }
            if (o < h) {
                h = o;
            }
            if (m > i) {
                i = m;
            }
            if (n > j) {
                j = n;
            }
            if (!(o > k)) continue;
            k = o;
        }
        CubeFace cubeFace = CubeFace.getFace(direction);
        for (int p = 0; p < 4; ++p) {
            float q;
            CubeFace.Corner corner = cubeFace.getCorner(p);
            n = corner.xSide().get(f, g, h, i, j, k);
            int r = BakedQuadFactory.method_76655(vector3fcs, p, n, o = corner.ySide().get(f, g, h, i, j, k), q = corner.zSide().get(f, g, h, i, j, k));
            if (r == -1) {
                throw new IllegalStateException("Can't find vertex to swap");
            }
            if (r == p) continue;
            BakedQuadFactory.method_76656(vector3fcs, r, p);
            BakedQuadFactory.method_76654(ls, r, p);
        }
    }

    private static int method_76655(Vector3fc[] vector3fcs, int i, float f, float g, float h) {
        for (int j = i; j < 4; ++j) {
            Vector3fc vector3fc = vector3fcs[j];
            if (f != vector3fc.x() || g != vector3fc.y() || h != vector3fc.z()) continue;
            return j;
        }
        return -1;
    }

    private static void method_76656(Vector3fc[] vector3fcs, int i, int j) {
        Vector3fc vector3fc = vector3fcs[i];
        vector3fcs[i] = vector3fcs[j];
        vector3fcs[j] = vector3fc;
    }

    private static void method_76654(long[] ls, int i, int j) {
        long l = ls[i];
        ls[i] = ls[j];
        ls[j] = l;
    }
}
