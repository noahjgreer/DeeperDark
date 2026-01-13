/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.MapCodec
 *  org.slf4j.Logger
 */
package net.minecraft.util.collection;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.collection.ListOperation;
import org.slf4j.Logger;

public static class ListOperation.Append
implements ListOperation {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ListOperation.Append INSTANCE = new ListOperation.Append();
    public static final MapCodec<ListOperation.Append> CODEC = MapCodec.unit(() -> INSTANCE);

    private ListOperation.Append() {
    }

    @Override
    public ListOperation.Mode getMode() {
        return ListOperation.Mode.APPEND;
    }

    @Override
    public <T> List<T> apply(List<T> current, List<T> values, int maxSize) {
        if (current.size() + values.size() > maxSize) {
            LOGGER.error("Contents overflow in section append");
            return current;
        }
        return Stream.concat(current.stream(), values.stream()).toList();
    }
}
