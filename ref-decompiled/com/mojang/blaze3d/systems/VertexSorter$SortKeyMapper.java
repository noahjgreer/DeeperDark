/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3f
 */
package com.mojang.blaze3d.systems;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface VertexSorter.SortKeyMapper {
    public float apply(Vector3f var1);
}
