/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.structure;

import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.IdList;
import org.jspecify.annotations.Nullable;

static class StructureTemplate.Palette
implements Iterable<BlockState> {
    public static final BlockState AIR = Blocks.AIR.getDefaultState();
    private final IdList<BlockState> ids = new IdList(16);
    private int currentIndex;

    StructureTemplate.Palette() {
    }

    public int getId(BlockState state) {
        int i = this.ids.getRawId(state);
        if (i == -1) {
            i = this.currentIndex++;
            this.ids.set(state, i);
        }
        return i;
    }

    public @Nullable BlockState getState(int id) {
        BlockState blockState = this.ids.get(id);
        return blockState == null ? AIR : blockState;
    }

    @Override
    public Iterator<BlockState> iterator() {
        return this.ids.iterator();
    }

    public void set(BlockState state, int id) {
        this.ids.set(state, id);
    }
}
