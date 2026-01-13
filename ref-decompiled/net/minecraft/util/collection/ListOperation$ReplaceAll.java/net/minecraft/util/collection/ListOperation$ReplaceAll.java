/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.util.collection;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.util.collection.ListOperation;

public static class ListOperation.ReplaceAll
implements ListOperation {
    public static final ListOperation.ReplaceAll INSTANCE = new ListOperation.ReplaceAll();
    public static final MapCodec<ListOperation.ReplaceAll> CODEC = MapCodec.unit(() -> INSTANCE);

    private ListOperation.ReplaceAll() {
    }

    @Override
    public ListOperation.Mode getMode() {
        return ListOperation.Mode.REPLACE_ALL;
    }

    @Override
    public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
        return values;
    }
}
