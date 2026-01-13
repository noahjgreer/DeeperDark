/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.SharedConstants
 *  net.minecraft.block.Block
 *  net.minecraft.block.MultifaceBlock
 *  net.minecraft.block.entity.SculkSpreadManager
 *  net.minecraft.block.entity.SculkSpreadManager$Cursor
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.registry.tag.TagKey
 *  net.minecraft.storage.ReadView
 *  net.minecraft.storage.WriteView
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.WorldAccess
 */
package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.entity.SculkSpreadManager;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;

public class SculkSpreadManager {
    public static final int field_37609 = 24;
    public static final int MAX_CHARGE = 1000;
    public static final float field_37611 = 0.5f;
    private static final int MAX_CURSORS = 32;
    public static final int field_37612 = 11;
    public static final int MAX_CURSOR_DISTANCE = 1024;
    final boolean worldGen;
    private final TagKey<Block> replaceableTag;
    private final int extraBlockChance;
    private final int maxDistance;
    private final int spreadChance;
    private final int decayChance;
    private List<Cursor> cursors = new ArrayList();

    public SculkSpreadManager(boolean worldGen, TagKey<Block> replaceableTag, int extraBlockChance, int maxDistance, int spreadChance, int decayChance) {
        this.worldGen = worldGen;
        this.replaceableTag = replaceableTag;
        this.extraBlockChance = extraBlockChance;
        this.maxDistance = maxDistance;
        this.spreadChance = spreadChance;
        this.decayChance = decayChance;
    }

    public static SculkSpreadManager create() {
        return new SculkSpreadManager(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
    }

    public static SculkSpreadManager createWorldGen() {
        return new SculkSpreadManager(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
    }

    public TagKey<Block> getReplaceableTag() {
        return this.replaceableTag;
    }

    public int getExtraBlockChance() {
        return this.extraBlockChance;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public int getSpreadChance() {
        return this.spreadChance;
    }

    public int getDecayChance() {
        return this.decayChance;
    }

    public boolean isWorldGen() {
        return this.worldGen;
    }

    @VisibleForTesting
    public List<Cursor> getCursors() {
        return this.cursors;
    }

    public void clearCursors() {
        this.cursors.clear();
    }

    public void readData(ReadView view) {
        this.cursors.clear();
        view.read("cursors", Cursor.CODEC.sizeLimitedListOf(32)).orElse(List.of()).forEach(arg_0 -> this.addCursor(arg_0));
    }

    public void writeData(WriteView view) {
        view.put("cursors", Cursor.CODEC.listOf(), (Object)this.cursors);
        if (SharedConstants.SCULK_CATALYST) {
            int i = this.getCursors().stream().map(Cursor::getCharge).reduce(0, Integer::sum);
            int j = this.getCursors().stream().map(cursor -> 1).reduce(0, Integer::sum);
            int k = this.getCursors().stream().map(Cursor::getCharge).reduce(0, Math::max);
            view.putInt("stats.total", i);
            view.putInt("stats.count", j);
            view.putInt("stats.max", k);
            view.putInt("stats.avg", i / (j + 1));
        }
    }

    public void spread(BlockPos pos, int charge) {
        while (charge > 0) {
            int i = Math.min(charge, 1000);
            this.addCursor(new Cursor(pos, i));
            charge -= i;
        }
    }

    private void addCursor(Cursor cursor) {
        if (this.cursors.size() >= 32) {
            return;
        }
        this.cursors.add(cursor);
    }

    public void tick(WorldAccess world, BlockPos pos, Random random, boolean shouldConvertToBlock) {
        BlockPos blockPos;
        if (this.cursors.isEmpty()) {
            return;
        }
        ArrayList<Cursor> list = new ArrayList<Cursor>();
        HashMap<BlockPos, Cursor> map = new HashMap<BlockPos, Cursor>();
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        for (Cursor cursor : this.cursors) {
            if (cursor.isTooFarFrom(pos)) continue;
            cursor.spread(world, pos, random, this, shouldConvertToBlock);
            if (cursor.charge <= 0) {
                world.syncWorldEvent(3006, cursor.getPos(), 0);
                continue;
            }
            blockPos = cursor.getPos();
            object2IntMap.computeInt((Object)blockPos, (posx, charge) -> (charge == null ? 0 : charge) + cursor.charge);
            Cursor cursor2 = (Cursor)map.get(blockPos);
            if (cursor2 == null) {
                map.put(blockPos, cursor);
                list.add(cursor);
                continue;
            }
            if (!this.isWorldGen() && cursor.charge + cursor2.charge <= 1000) {
                cursor2.merge(cursor);
                continue;
            }
            list.add(cursor);
            if (cursor.charge >= cursor2.charge) continue;
            map.put(blockPos, cursor);
        }
        for (Object2IntMap.Entry entry : object2IntMap.object2IntEntrySet()) {
            Set collection;
            blockPos = (BlockPos)entry.getKey();
            int i = entry.getIntValue();
            Cursor cursor3 = (Cursor)map.get(blockPos);
            Set set = collection = cursor3 == null ? null : cursor3.getFaces();
            if (i <= 0 || collection == null) continue;
            int j = (int)(Math.log1p(i) / (double)2.3f) + 1;
            int k = (j << 6) + MultifaceBlock.directionsToFlag((Collection)collection);
            world.syncWorldEvent(3006, blockPos, k);
        }
        this.cursors = list;
    }
}

