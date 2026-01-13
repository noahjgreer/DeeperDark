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
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public static interface Baker.Vec3fInterner {
    default public Vector3fc intern(float x, float y, float z) {
        return this.intern((Vector3fc)new Vector3f(x, y, z));
    }

    public Vector3fc intern(Vector3fc var1);
}
