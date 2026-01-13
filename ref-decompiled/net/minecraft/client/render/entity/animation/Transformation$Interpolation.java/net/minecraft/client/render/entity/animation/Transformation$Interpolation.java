/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package net.minecraft.client.render.entity.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Keyframe;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public static interface Transformation.Interpolation {
    public Vector3f apply(Vector3f var1, float var2, Keyframe[] var3, int var4, int var5, float var6);
}
