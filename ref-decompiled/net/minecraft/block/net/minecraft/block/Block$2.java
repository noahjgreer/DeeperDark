/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap
 */
package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.block.Block;

static class Block.2
extends Object2ByteLinkedOpenHashMap<Block.VoxelShapePair> {
    Block.2(int i, float f) {
        super(i, f);
    }

    protected void rehash(int newN) {
    }
}
