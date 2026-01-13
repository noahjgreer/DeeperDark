/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Multimap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedGeometry;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public static class BakedGeometry.Builder {
    private final ImmutableList.Builder<BakedQuad> sidelessQuads = ImmutableList.builder();
    private final Multimap<Direction, BakedQuad> sidedQuads = ArrayListMultimap.create();

    public BakedGeometry.Builder add(Direction side, BakedQuad quad) {
        this.sidedQuads.put((Object)side, (Object)quad);
        return this;
    }

    public BakedGeometry.Builder add(BakedQuad quad) {
        this.sidelessQuads.add((Object)quad);
        return this;
    }

    private static BakedGeometry buildFromList(List<BakedQuad> quads, int sidelessCount, int northCount, int southCount, int eastCount, int westCount, int upCount, int downCount) {
        int i = 0;
        List<BakedQuad> list = quads.subList(i, i += sidelessCount);
        List<BakedQuad> list2 = quads.subList(i, i += northCount);
        List<BakedQuad> list3 = quads.subList(i, i += southCount);
        List<BakedQuad> list4 = quads.subList(i, i += eastCount);
        List<BakedQuad> list5 = quads.subList(i, i += westCount);
        List<BakedQuad> list6 = quads.subList(i, i += upCount);
        List<BakedQuad> list7 = quads.subList(i, i + downCount);
        return new BakedGeometry(quads, list, list2, list3, list4, list5, list6, list7);
    }

    public BakedGeometry build() {
        ImmutableList immutableList = this.sidelessQuads.build();
        if (this.sidedQuads.isEmpty()) {
            if (immutableList.isEmpty()) {
                return EMPTY;
            }
            return new BakedGeometry((List<BakedQuad>)immutableList, (List<BakedQuad>)immutableList, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        builder.addAll((Iterable)immutableList);
        Collection collection = this.sidedQuads.get((Object)Direction.NORTH);
        builder.addAll((Iterable)collection);
        Collection collection2 = this.sidedQuads.get((Object)Direction.SOUTH);
        builder.addAll((Iterable)collection2);
        Collection collection3 = this.sidedQuads.get((Object)Direction.EAST);
        builder.addAll((Iterable)collection3);
        Collection collection4 = this.sidedQuads.get((Object)Direction.WEST);
        builder.addAll((Iterable)collection4);
        Collection collection5 = this.sidedQuads.get((Object)Direction.UP);
        builder.addAll((Iterable)collection5);
        Collection collection6 = this.sidedQuads.get((Object)Direction.DOWN);
        builder.addAll((Iterable)collection6);
        return BakedGeometry.Builder.buildFromList((List<BakedQuad>)builder.build(), immutableList.size(), collection.size(), collection2.size(), collection3.size(), collection4.size(), collection5.size(), collection6.size());
    }
}
