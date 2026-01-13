/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.datafixer.fix.ChunkPalettedStorageFix;
import net.minecraft.util.collection.Int2ObjectBiMap;
import net.minecraft.util.math.WordPackedArray;

static class ChunkPalettedStorageFix.Section {
    private final Int2ObjectBiMap<Dynamic<?>> paletteMap = Int2ObjectBiMap.create(32);
    private final List<Dynamic<?>> paletteData;
    private final Dynamic<?> section;
    private final boolean hasBlocks;
    final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap();
    final IntList innerPositions = new IntArrayList();
    public final int y;
    private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
    private final int[] states = new int[4096];

    public ChunkPalettedStorageFix.Section(Dynamic<?> section) {
        this.paletteData = Lists.newArrayList();
        this.section = section;
        this.y = section.get("Y").asInt(0);
        this.hasBlocks = section.get("Blocks").result().isPresent();
    }

    public Dynamic<?> getBlock(int index) {
        if (index < 0 || index > 4095) {
            return ChunkPalettedStorageFix.Mapping.AIR_STATE;
        }
        Dynamic<?> dynamic = this.paletteMap.get(this.states[index]);
        return dynamic == null ? ChunkPalettedStorageFix.Mapping.AIR_STATE : dynamic;
    }

    public void setBlock(int pos, Dynamic<?> dynamic) {
        if (this.seenStates.add(dynamic)) {
            this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? ChunkPalettedStorageFix.Mapping.AIR_STATE : dynamic);
        }
        this.states[pos] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
    }

    public int visit(int sidesToUpgrade) {
        if (!this.hasBlocks) {
            return sidesToUpgrade;
        }
        ByteBuffer byteBuffer2 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
        ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray = this.section.get("Data").asByteBufferOpt().map(byteBuffer -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray((ByteBuffer)byteBuffer))).result().orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
        ChunkPalettedStorageFix.ChunkNibbleArray chunkNibbleArray2 = this.section.get("Add").asByteBufferOpt().map(byteBuffer -> new ChunkPalettedStorageFix.ChunkNibbleArray(DataFixUtils.toArray((ByteBuffer)byteBuffer))).result().orElseGet(ChunkPalettedStorageFix.ChunkNibbleArray::new);
        this.seenStates.add(ChunkPalettedStorageFix.Mapping.AIR_STATE);
        ChunkPalettedStorageFix.addTo(this.paletteMap, ChunkPalettedStorageFix.Mapping.AIR_STATE);
        this.paletteData.add(ChunkPalettedStorageFix.Mapping.AIR_STATE);
        for (int i = 0; i < 4096; ++i) {
            int j = i & 0xF;
            int k = i >> 8 & 0xF;
            int l = i >> 4 & 0xF;
            int m = chunkNibbleArray2.get(j, k, l) << 12 | (byteBuffer2.get(i) & 0xFF) << 4 | chunkNibbleArray.get(j, k, l);
            if (ChunkPalettedStorageFix.Mapping.field_52402.get(m >> 4)) {
                this.addInPlaceUpdate(m >> 4, i);
            }
            if (ChunkPalettedStorageFix.Mapping.field_52401.get(m >> 4)) {
                int n = ChunkPalettedStorageFix.getSideToUpgradeFlag(j == 0, j == 15, l == 0, l == 15);
                if (n == 0) {
                    this.innerPositions.add(i);
                } else {
                    sidesToUpgrade |= n;
                }
            }
            this.setBlock(i, BlockStateFlattening.lookupState(m));
        }
        return sidesToUpgrade;
    }

    private void addInPlaceUpdate(int section, int index) {
        IntList intList = (IntList)this.inPlaceUpdates.get(section);
        if (intList == null) {
            intList = new IntArrayList();
            this.inPlaceUpdates.put(section, (Object)intList);
        }
        intList.add(index);
    }

    public Dynamic<?> transform() {
        Dynamic dynamic = this.section;
        if (!this.hasBlocks) {
            return dynamic;
        }
        dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
        int i = Math.max(4, DataFixUtils.ceillog2((int)this.seenStates.size()));
        WordPackedArray wordPackedArray = new WordPackedArray(i, 4096);
        for (int j = 0; j < this.states.length; ++j) {
            wordPackedArray.set(j, this.states[j]);
        }
        dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(wordPackedArray.getAlignedArray())));
        dynamic = dynamic.remove("Blocks");
        dynamic = dynamic.remove("Data");
        dynamic = dynamic.remove("Add");
        return dynamic;
    }
}
