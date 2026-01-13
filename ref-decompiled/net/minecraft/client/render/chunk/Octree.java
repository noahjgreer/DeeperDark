/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.chunk.ChunkBuilder$BuiltChunk
 *  net.minecraft.client.render.chunk.Octree
 *  net.minecraft.client.render.chunk.Octree$Branch
 *  net.minecraft.client.render.chunk.Octree$Visitor
 *  net.minecraft.util.math.BlockBox
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkSectionPos
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class Octree {
    private final Branch root;
    final BlockPos centerPos;

    public Octree(ChunkSectionPos sectionPos, int viewDistance, int sizeY, int bottomY) {
        int i = viewDistance * 2 + 1;
        int j = MathHelper.smallestEncompassingPowerOfTwo((int)i);
        int k = viewDistance * 16;
        BlockPos blockPos = sectionPos.getMinPos();
        this.centerPos = sectionPos.getCenterPos();
        int l = blockPos.getX() - k;
        int m = l + j * 16 - 1;
        int n = j >= sizeY ? bottomY : blockPos.getY() - k;
        int o = n + j * 16 - 1;
        int p = blockPos.getZ() - k;
        int q = p + j * 16 - 1;
        this.root = new Branch(this, new BlockBox(l, n, p, m, o, q));
    }

    public boolean add(ChunkBuilder.BuiltChunk chunk) {
        return this.root.add(chunk);
    }

    public void visit(Visitor visitor, Frustum frustum, int margin) {
        this.root.visit(visitor, false, frustum, 0, margin, true);
    }

    boolean isCenterWithin(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, int margin) {
        int i = this.centerPos.getX();
        int j = this.centerPos.getY();
        int k = this.centerPos.getZ();
        return (double)i > minX - (double)margin && (double)i < maxX + (double)margin && (double)j > minY - (double)margin && (double)j < maxY + (double)margin && (double)k > minZ - (double)margin && (double)k < maxZ + (double)margin;
    }
}

