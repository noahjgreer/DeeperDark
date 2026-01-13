/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.ErrorCollectingSpriteGetter;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public interface Baker {
    public BakedSimpleModel getModel(Identifier var1);

    public BlockModelPart getBlockPart();

    public ErrorCollectingSpriteGetter getSpriteGetter();

    public Vec3fInterner getVec3fInterner();

    public <T> T compute(ResolvableCacheKey<T> var1);

    @Environment(value=EnvType.CLIENT)
    public static interface Vec3fInterner {
        default public Vector3fc intern(float x, float y, float z) {
            return this.intern((Vector3fc)new Vector3f(x, y, z));
        }

        public Vector3fc intern(Vector3fc var1);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    public static interface ResolvableCacheKey<T> {
        public T compute(Baker var1);
    }
}
