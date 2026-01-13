/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.chunk;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.Octree;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkSectionPos;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
class Octree.Branch
implements Octree.Node {
    private final  @Nullable Octree.Node[] children = new Octree.Node[8];
    private final BlockBox box;
    private final int centerX;
    private final int centerY;
    private final int centerZ;
    private final Octree.AxisOrder axisOrder;
    private final boolean easternSide;
    private final boolean topSide;
    private final boolean southernSide;

    public Octree.Branch(BlockBox box) {
        this.box = box;
        this.centerX = this.box.getMinX() + this.box.getBlockCountX() / 2;
        this.centerY = this.box.getMinY() + this.box.getBlockCountY() / 2;
        this.centerZ = this.box.getMinZ() + this.box.getBlockCountZ() / 2;
        int i = Octree.this.centerPos.getX() - this.centerX;
        int j = Octree.this.centerPos.getY() - this.centerY;
        int k = Octree.this.centerPos.getZ() - this.centerZ;
        this.axisOrder = Octree.AxisOrder.fromPos(Math.abs(i), Math.abs(j), Math.abs(k));
        this.easternSide = i < 0;
        this.topSide = j < 0;
        this.southernSide = k < 0;
    }

    public boolean add(ChunkBuilder.BuiltChunk chunk) {
        long l = chunk.getSectionPos();
        boolean bl = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackX(l)) - this.centerX < 0;
        boolean bl2 = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackY(l)) - this.centerY < 0;
        boolean bl3 = ChunkSectionPos.getBlockCoord(ChunkSectionPos.unpackZ(l)) - this.centerZ < 0;
        boolean bl4 = bl != this.easternSide;
        boolean bl5 = bl2 != this.topSide;
        boolean bl6 = bl3 != this.southernSide;
        int i = Octree.Branch.getIndex(this.axisOrder, bl4, bl5, bl6);
        if (this.areChildrenLeaves()) {
            boolean bl7 = this.children[i] != null;
            this.children[i] = new Octree.Leaf(Octree.this, chunk);
            return !bl7;
        }
        if (this.children[i] != null) {
            Octree.Branch branch = (Octree.Branch)this.children[i];
            return branch.add(chunk);
        }
        BlockBox blockBox = this.getChildBox(bl, bl2, bl3);
        Octree.Branch branch2 = new Octree.Branch(blockBox);
        this.children[i] = branch2;
        return branch2.add(chunk);
    }

    private static int getIndex(Octree.AxisOrder axisOrder, boolean sameRelativeSideX, boolean sameRelativeSideY, boolean sameRelativeSideZ) {
        int i = 0;
        if (sameRelativeSideX) {
            i += axisOrder.x;
        }
        if (sameRelativeSideY) {
            i += axisOrder.y;
        }
        if (sameRelativeSideZ) {
            i += axisOrder.z;
        }
        return i;
    }

    private boolean areChildrenLeaves() {
        return this.box.getBlockCountX() == 32;
    }

    private BlockBox getChildBox(boolean western, boolean bottom, boolean northern) {
        int n;
        int m;
        int l;
        int k;
        int j;
        int i;
        if (western) {
            i = this.box.getMinX();
            j = this.centerX - 1;
        } else {
            i = this.centerX;
            j = this.box.getMaxX();
        }
        if (bottom) {
            k = this.box.getMinY();
            l = this.centerY - 1;
        } else {
            k = this.centerY;
            l = this.box.getMaxY();
        }
        if (northern) {
            m = this.box.getMinZ();
            n = this.centerZ - 1;
        } else {
            m = this.centerZ;
            n = this.box.getMaxZ();
        }
        return new BlockBox(i, k, m, j, l, n);
    }

    @Override
    public void visit(Octree.Visitor visitor, boolean skipVisibilityCheck, Frustum frustum, int depth, int margin, boolean nearCenter) {
        boolean bl = skipVisibilityCheck;
        if (!skipVisibilityCheck) {
            int i = frustum.intersectAab(this.box);
            skipVisibilityCheck = i == -2;
            boolean bl2 = bl = i == -2 || i == -1;
        }
        if (bl) {
            nearCenter = nearCenter && Octree.this.isCenterWithin(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX(), this.box.getMaxY(), this.box.getMaxZ(), margin);
            visitor.visit(this, skipVisibilityCheck, depth, nearCenter);
            for (Octree.Node node : this.children) {
                if (node == null) continue;
                node.visit(visitor, skipVisibilityCheck, frustum, depth + 1, margin, nearCenter);
            }
        }
    }

    @Override
    public @Nullable ChunkBuilder.BuiltChunk getBuiltChunk() {
        return null;
    }

    @Override
    public Box getBoundingBox() {
        return new Box(this.box.getMinX(), this.box.getMinY(), this.box.getMinZ(), this.box.getMaxX() + 1, this.box.getMaxY() + 1, this.box.getMaxZ() + 1);
    }
}
