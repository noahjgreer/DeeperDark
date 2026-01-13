/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.Octree;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
public static interface Octree.Visitor {
    public void visit(Octree.Node var1, boolean var2, int var3, boolean var4);
}
