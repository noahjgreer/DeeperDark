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
import net.minecraft.client.model.ModelPart;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public static interface Transformation.Target {
    public void apply(ModelPart var1, Vector3f var2);
}
