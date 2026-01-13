/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.render.model.BlockStateModel$UnbakedGrouped
 *  net.minecraft.client.render.model.BlockStatesLoader$LoadedModels
 *  net.minecraft.client.render.model.ModelGrouper
 *  net.minecraft.client.render.model.ModelGrouper$GroupKey
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelGrouper;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ModelGrouper {
    static final int field_53673 = -1;
    private static final int field_53674 = 0;

    public static Object2IntMap<BlockState> group(BlockColors colors, BlockStatesLoader.LoadedModels definition) {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        definition.models().forEach((state, model) -> {
            List list = map.computeIfAbsent(state.getBlock(), block -> List.copyOf(colors.getProperties(block)));
            GroupKey groupKey = GroupKey.of((BlockState)state, (BlockStateModel.UnbakedGrouped)model, (List)list);
            map2.computeIfAbsent(groupKey, key -> Sets.newIdentityHashSet()).add(state);
        });
        int i = 1;
        Object2IntOpenHashMap object2IntMap = new Object2IntOpenHashMap();
        object2IntMap.defaultReturnValue(-1);
        for (Set set : map2.values()) {
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                BlockState blockState = (BlockState)iterator.next();
                if (blockState.getRenderType() == BlockRenderType.MODEL) continue;
                iterator.remove();
                object2IntMap.put((Object)blockState, 0);
            }
            if (set.size() <= 1) continue;
            int j = i++;
            set.forEach(arg_0 -> ModelGrouper.method_62649((Object2IntMap)object2IntMap, j, arg_0));
        }
        return object2IntMap;
    }

    private static /* synthetic */ void method_62649(Object2IntMap object2IntMap, int i, BlockState state) {
        object2IntMap.put((Object)state, i);
    }
}

