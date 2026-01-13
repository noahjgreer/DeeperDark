/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Floats
 *  it.unimi.dsi.fastutil.ints.IntArrays
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.mojang.blaze3d.systems;

import com.google.common.primitives.Floats;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vec3fArray;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public interface VertexSorter {
    public static final VertexSorter BY_DISTANCE = VertexSorter.byDistance(0.0f, 0.0f, 0.0f);
    public static final VertexSorter BY_Z = VertexSorter.of(vec -> -vec.z());

    public static VertexSorter byDistance(float originX, float originY, float originZ) {
        return VertexSorter.byDistance((Vector3fc)new Vector3f(originX, originY, originZ));
    }

    public static VertexSorter byDistance(Vector3fc origin) {
        return VertexSorter.of(arg_0 -> ((Vector3fc)origin).distanceSquared(arg_0));
    }

    public static VertexSorter of(SortKeyMapper mapper) {
        return vectors -> {
            Vector3f vector3f = new Vector3f();
            float[] fs = new float[vectors.size()];
            int[] is = new int[vectors.size()];
            for (int i = 0; i < vectors.size(); ++i) {
                fs[i] = mapper.apply(vectors.get(i, vector3f));
                is[i] = i;
            }
            IntArrays.mergeSort((int[])is, (a, b) -> Floats.compare((float)fs[b], (float)fs[a]));
            return is;
        };
    }

    public int[] sort(Vec3fArray var1);

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface SortKeyMapper {
        public float apply(Vector3f var1);
    }
}
